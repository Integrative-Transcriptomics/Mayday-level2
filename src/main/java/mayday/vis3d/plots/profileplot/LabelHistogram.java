package mayday.vis3d.plots.profileplot;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GLContext;

import mayday.core.Probe;
import mayday.vis3d.primitives.DraggableProbe;
import mayday.vis3d.utilities.Camera2D;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.j2d.TextRenderer;

/**
 * @author jaeger
 *
 */
public class LabelHistogram implements MouseListener, MouseMotionListener, KeyListener {

	private ProfilePlotPanel panel;
	
	private DraggableProbe selectedProbeForDragging;
	private HashMap<Probe, DraggableProbe> probeTable;
	
	private HashMap<Integer, ArrayList<DraggableProbe>> labels = new HashMap<Integer, ArrayList<DraggableProbe>>();
	private HashSet<Object> boxIds = new HashSet<Object>();
	private TextRenderer renderer;
	private ArrayList<Integer> boxes = new ArrayList<Integer>();
	private ArrayList<DraggableProbe> freePlacedLabels = new ArrayList<DraggableProbe>();
	
	private Animator animator;
	
	private Integer selectedBox = null;
	private double moveRight = 0;
	private boolean animatorRunning = false;
	private double moveDist = 0;
	private boolean open = false;
	private boolean newBoxSelected = false;

	private boolean selected = false;
	
	private int tmpAnchorDim = 0;

	private float spaceForLines;
	
	private boolean altGraphDown = false;
	private boolean MB3 = false;
	
	/**
	 * @param panel
	 */
	public LabelHistogram(ProfilePlotPanel panel) {
		this.panel = panel;
		this.panel.getCanvas().addMouseListener(this);
		this.panel.getCanvas().addMouseMotionListener(this);
		this.panel.getCanvas().addKeyListener(this);
	}
	
	/**
	 * update the histogram
	 */
	public void update() {
		if(tmpAnchorDim != panel.settings.getLabelingSetting().getLabelAnchorDim() || panel.viewModelChanged) {
			tmpAnchorDim = panel.settings.getLabelingSetting().getLabelAnchorDim();
			this.calculateLabelingBoxes();
			if(panel.viewModelChanged) {
				panel.viewModelChanged = false;
			}
		}
	}
	
	/**
	 * @param gl
	 */
	public void initialize(GL gl) {
		//store probes and corresponding hash codes for selection processing
		Object[] probes = this.panel.viewModel.getProbes().toArray();
		probeTable = new HashMap<Probe, DraggableProbe>();
		
		for(int i = 0; i < probes.length; i++) {
			probeTable.put((Probe)probes[i], new DraggableProbe((Probe)probes[i]));
		}
		
		animator = new Animator(this.panel.getCanvas());
		
		this.renderer = new TextRenderer(new Font("Sans.Serif", Font.BOLD, 
				panel.settings.getLabelingSetting().getLabelsSize()),
					true, true);
			
		renderer.setSmoothing(true);
		renderer.setColor(0.0f, 0.0f, 0.0f, 1.0f);
	}
	
	/**
	 * @return maximal width
	 */
	public double getMaxWidth() {
		double move = this.moveDist * panel.settings.getLabelingSetting().getLabelsScale();
		return move + panel.coordSystem.getSetting().getChartSetting().getWidth() * 0.2 + spaceForLines;
	}
	
	private double[] getProjectedMousePositionXY(MouseEvent e, GL gl) {
		int x = e.getX();
		int y = e.getY();
		
		int viewport[] = new int[4];
	    double mvmatrix[] = new double[16];
	    double projmatrix[] = new double[16];
	    double wcoord[] = new double[4];

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
        gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix, 0);
        gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmatrix, 0);

        double scale = ((Camera2D)panel.camera).getScale();
        
        //get the unprojected mouse position
        panel.glu.gluUnProject((double)x, (double)y, (double)1, 
        	mvmatrix, 0,
            projmatrix, 0, 
            viewport, 0, 
            wcoord, 0);
        
        //correct for the scale
        double s = (4.0 * scale);
        double newMX = (wcoord[0] / s + 200);
        double newMY = (-wcoord[1] / s + 100);
        
        //correct for the translation in x direction
        double scaleI = 1.0 / scale;
        double xtrans = -((Camera2D)panel.camera).getPosition2D().getX() * scaleI;
        newMX += xtrans;
        
        //correct for the translation in y direction
        double ytrans = -((Camera2D)panel.camera).getPosition2D().getY() * scaleI;
        newMY += ytrans;
        
        System.out.println(newMX + " : " + newMY);
        return new double[]{newMX, newMY};
	}
	
	private void clear() {
		this.boxes.clear();
		this.labels.clear();
		this.boxIds.clear();
		System.gc();
	}
	
	/**
	 * @param gl
	 * @param glRender
	 */
	public void draw(GL gl, int glRender) {
		spaceForLines = (float)panel.coordSystem.getSetting().getChartSetting().getWidth() * 0.1f;
		
		if(selected) {
			move();
			if(!animatorRunning && open) {
				drawLabels(gl, glRender);
			}
		}
		this.drawFreePlacedLabels(gl, glRender);
		drawHistogram(gl, glRender);
	}
	
	private int getMaxSize() {
		int maxSize = 0;
		for(ArrayList<DraggableProbe> label : labels.values()) {
			if(label.size() > maxSize) {
				maxSize = label.size();
			}
		}
		return maxSize;
	}
	
	private void drawHistogram(GL gl, int glRender) {
		double scale = panel.coordSystem.getSetting().getFontScale();
		int numBoxes = this.labels.size();
		double boxHeight = (double)panel.coordSystem.getSetting().getChartSetting().getHeight() / (double)numBoxes;
		int maxSize = getMaxSize();
		gl.glLineWidth(1.1f);
		renderer.setColor(Color.BLACK);
		double width = panel.coordSystem.getSetting().getChartSetting().getWidth() * 0.2;
		
		for(Integer id : this.labels.keySet()) {
			ArrayList<DraggableProbe> probes = labels.get(id);
			int size = probes.size();
			double boxWidth = ((double)size / (double)maxSize) * width;
			
			if(boxWidth > 0) {
				gl.glPushMatrix();
					gl.glTranslated(panel.coordSystem.getSetting().getChartSetting().getWidth() + moveRight * (scale + 0.05) + spaceForLines, id.intValue() * boxHeight, 0);
					
					double factor = boxWidth / size;
					
					String label = " " + size;
					Rectangle2D bounds = renderer.getBounds(label);
					float h = ((float)boxHeight - (float)bounds.getHeight()*(float)scale) / 2.0f;
					
					if(selectedBox != null) {
						if(selectedBox.intValue() == id.intValue()) {
							renderer.setColor(panel.settings.getSelectionColor().getColorValue());
							gl.glColor3fv(panel.convertColor(Color.PINK), 0);
						} else {
							renderer.setColor(Color.BLACK);
							gl.glColor3fv(panel.convertColor(Color.CYAN), 0);
						}
					} else {
						gl.glColor3fv(panel.convertColor(Color.CYAN), 0);
					}
					
					if(glRender == GL.GL_SELECT) {
						gl.glLoadName(id.hashCode());
					}
			
					gl.glBegin(GL.GL_QUADS);
						gl.glVertex3d(0, 0, 0);
						gl.glVertex3d(0, boxHeight, 0);
						gl.glVertex3d(boxWidth, boxHeight, 0);
						gl.glVertex3d(boxWidth, 0, 0);
					gl.glEnd();
					
					gl.glColor3d(0, 0, 0);
					gl.glBegin(GL.GL_LINE_LOOP);
						gl.glVertex3d(0, 0, 0.01);
						gl.glVertex3d(0, boxHeight, 0.01);
						gl.glVertex3d(boxWidth, boxHeight, 0.01);
						gl.glVertex3d(boxWidth, 0, 0.01);
					gl.glEnd();
					
					gl.glColor3fv(panel.convertColor(panel.settings.getSelectionColor().getColorValue()), 0);
					for(DraggableProbe pb : probes) {
						if(panel.viewModel.isSelected(pb.getProbe())) {
							int index = probes.indexOf(pb);
							double index1 = (double)index * factor;
							double index2 = (double)(index + 1) * factor;
							
							gl.glBegin(GL.GL_QUADS);
								gl.glVertex3d(index1, 0, 0.01);
								gl.glVertex3d(index1, boxHeight, 0.01);
								gl.glVertex3d(index2, boxHeight, 0.01);
								gl.glVertex3d(index2, 0, 0.01);
							gl.glEnd();
						}
					}
					
					renderer.begin3DRendering();
					renderer.draw3D(label, (float)boxWidth, h, 0, (float)scale);
					renderer.end3DRendering();
										
				gl.glPopMatrix();
			}
		}
	}
	
	private void drawLabels(GL gl, int glRender) {
		double scale = panel.settings.getLabelingSetting().getLabelsScale(); 
		ArrayList<DraggableProbe> dlabels = this.labels.get(this.selectedBox);
		if(dlabels != null) {
			gl.glPushMatrix();
			for(int i = 0; i < dlabels.size(); i++) {
				if(dlabels.get(i) != null) {
					float tmpDepth = 0.01f;
					if(panel.viewModel.isSelected(dlabels.get(i).getProbe())) {
						renderer.setColor(panel.settings.getSelectionColor().getColorValue());
						gl.glColor3fv(panel.convertColor(panel.settings.getSelectionColor().getColorValue()), 0);
						tmpDepth = 0.05f;
					} else {
						renderer.setColor(panel.coloring.getColor(dlabels.get(i).getProbe()));
						gl.glColor3fv(panel.convertColor(panel.coloring.getColor(dlabels.get(i).getProbe())), 0);
					}
					
					String curLabel = dlabels.get(i).getProbeName();

					
					
					if(!dlabels.get(i).freePlaced()) {
						float h = (float)renderer.getBounds(curLabel).getHeight() * (float)scale;
						float ydist = ((float)panel.coordSystem.getSetting().getChartSetting().getHeight() - dlabels.size() * h) / 2.0f;
						dlabels.get(i).setX(panel.coordSystem.getSetting().getChartSetting().getWidth() + spaceForLines);
						dlabels.get(i).setY(i*h + ydist);
					}
					
					if(glRender == GL.GL_SELECT) {
						gl.glLoadName(dlabels.get(i).getProbe().hashCode());
					}
					
					gl.glPushMatrix();
						renderer.begin3DRendering();
						renderer.draw3D(curLabel, (float)dlabels.get(i).getX(), (float)dlabels.get(i).getY(), tmpDepth, (float)scale);
						renderer.end3DRendering();
					gl.glPopMatrix();
					
					gl.glPushMatrix();
					if(panel.settings.getLabelingSetting().drawAnchorLines()) {
						boolean drawLine = true;
						if(!panel.settings.getLabelingSetting().drawAllAnchorLines()) {
							if(!panel.viewModel.isSelected(dlabels.get(i).getProbe())) {
								drawLine = false;
							}
						}
						if(drawLine) {
							gl.glBegin(GL.GL_LINES);
								double w2 = (this.renderer.getBounds(dlabels.get(i)).getHeight() * scale) / 2.0;
								gl.glVertex3d(dlabels.get(i).getX(), dlabels.get(i).getY()+w2, tmpDepth);
								double x = panel.coordSystem.getSetting().getChartSetting().getWidth();
								double y = panel.coordSystem.getSetting().getChartSetting().getHeight() / 2.0 
									+ panel.adjust(panel.viewModel.getProbeValues(dlabels.get(i).getProbe())[panel.viewModel.getProbeValues(dlabels.get(i).getProbe()).length - 1]);
								gl.glVertex3d(x, y, tmpDepth);
							gl.glEnd();
						}
					}
					gl.glPopMatrix();
				}
			}
			gl.glPopMatrix();
		}
	}
	
	private void drawFreePlacedLabels(GL gl, int glRender) {
		double scale = panel.settings.getLabelingSetting().getLabelsScale();
		for(DraggableProbe pb : this.freePlacedLabels) {
			if(pb.freePlaced()) {
				float tmpDepth = 0.01f;
				if(panel.viewModel.isSelected(pb.getProbe())) {
					renderer.setColor(panel.settings.getSelectionColor().getColorValue());
					gl.glColor3fv(panel.convertColor(panel.settings.getSelectionColor().getColorValue()), 0);
					tmpDepth = 0.05f;
				} else {
					renderer.setColor(panel.coloring.getColor(pb.getProbe()));
					gl.glColor3fv(panel.convertColor(panel.coloring.getColor(pb.getProbe())), 0);
				}
				
				String curLabel = pb.getProbeName();
				
				if(glRender == GL.GL_SELECT) {
					gl.glLoadName(pb.getProbe().hashCode());
				}
				
				gl.glPushMatrix();
					renderer.begin3DRendering();
					renderer.draw3D(curLabel, (float)pb.getX(), (float)pb.getY(), tmpDepth, (float)scale);
					renderer.end3DRendering();
				gl.glPopMatrix();
				
				gl.glPushMatrix();
				if(panel.settings.getLabelingSetting().drawAnchorLines()) {
					boolean drawLine = true;
					if(!panel.settings.getLabelingSetting().drawAllAnchorLines()) {
						if(!panel.viewModel.isSelected(pb.getProbe())) {
							drawLine = false;
						}
					}
					if(drawLine) {
						gl.glBegin(GL.GL_LINES);
							double w2 = (this.renderer.getBounds(pb).getHeight() * scale) / 2.0;
							gl.glVertex3d(pb.getX(), pb.getY()+w2, tmpDepth);
							double x = panel.coordSystem.getSetting().getChartSetting().getWidth();
							double y = panel.coordSystem.getSetting().getChartSetting().getHeight() / 2.0 
								+ panel.adjust(panel.viewModel.getProbeValues(pb.getProbe())[panel.viewModel.getProbeValues(pb.getProbe()).length - 1]);
							gl.glVertex3d(x, y, tmpDepth);
						gl.glEnd();
					}
				}
				gl.glPopMatrix();
			}
		}
	}
	
	private void addLabel(Integer identifier, ArrayList<DraggableProbe> labels) {
		if(identifier == null) 
			return;
		
		if(this.labels.containsKey(identifier)) {
			throw new IllegalArgumentException("Identifier already in use !");
		}
		
		this.labels.put(identifier, labels);
		this.boxes.add(identifier);
		
		for(DraggableProbe pb : labels) {
			double nameLength = renderer.getBounds(pb).getWidth() ;
			if(nameLength > this.moveDist) {
				this.moveDist = nameLength;
			}
		}
	}
	
	/**
	 * @param selection
	 */
	public void setSelection(Integer selection) {
		if(selection == null) {
			this.selected = false;
			this.selectedBox = null;
		} else {
			this.selected = true;
			selectedBox = selection;
			System.out.println("LabelHist: Changed the selcted box, box with id=" + selection + " is now selected.");
		}
		newBoxSelected = true;
	}
	
	/**
	 * @return all box ids
	 */
	public Object[] getBoxIds() {
		return this.boxIds.toArray();
	}
	
	private void calculateLabelingBoxes() {
		clear();
		
		int numBoxes = (int)panel.coordSystem.getSetting().getIteration()[1];
		double boxHeight = (panel.minMax[0]-panel.minMax[1]) / numBoxes;
		Set<Probe> allProbes = panel.viewModel.getProbes();
		ArrayList<SortableProbe> sortedProbes = new ArrayList<SortableProbe>();
		
		for(Probe pb : allProbes) {
			sortedProbes.add(new SortableProbe(pb, panel.viewModel.getProbeValues(pb)[panel.settings.getLabelingSetting().getLabelAnchorDim()]));
		}
		
		Collections.sort(sortedProbes);
		
		double iteration = panel.minMax[1] + boxHeight;
		ArrayList<DraggableProbe> boxLabels = new ArrayList<DraggableProbe>();
		
		for(int i = 0; i < numBoxes;) {
			if(!sortedProbes.isEmpty()) {
				if(sortedProbes.get(0).lastValue <= iteration) {
					boxLabels.add(this.probeTable.get(sortedProbes.get(0).pb));
					sortedProbes.remove(0);
				} else {
					iteration += boxHeight;
					Integer id = new Integer(i);
					addLabel(id, boxLabels);
					this.boxIds.add(id);
					boxLabels = new ArrayList<DraggableProbe>();
					i++;
				}
			} else {
				Integer id = new Integer(i);
				addLabel(id, boxLabels);
				this.boxIds.add(id);
				boxLabels = new ArrayList<DraggableProbe>();
				i++;
			}
		}
		
		sortedProbes = null;
		System.gc();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON3:
			this.MB3 = true;
			break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON3:
			this.MB3 = false;
			break;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(MB3) {
			if(this.selectedProbeForDragging != null && !altGraphDown) {
				if(panel.viewModel.isSelected(this.selectedProbeForDragging.getProbe())) {
					//drag & drop the selected probe label
					double mX,mY;
					
					int current = panel.getCanvas().getContext().makeCurrent();
					if(current == GLContext.CONTEXT_CURRENT) {
						GL gl = panel.getCanvas().getContext().getGL();
						double[] mXY = this.getProjectedMousePositionXY(e, gl);
						mX = mXY[0];
						mY = mXY[1];
						
						if(!this.selectedProbeForDragging.freePlaced()) {
							this.setFreePlaced(this.selectedProbeForDragging);
						}
						this.selectedProbeForDragging.setX(mX);
						this.selectedProbeForDragging.setY(mY);
						
						panel.getCanvas().getContext().release();
					}
					panel.getCanvas().repaint();
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
	
	private void move() {
		if(open && newBoxSelected) {
			close();
		} else {
			open();
		}
	}
	
	private void close() {
		//start animation to start moving the histogram to the left
		if(moveRight > 0) {
			if(!animatorRunning ) {
				if(!animator.isAnimating()) {
					animatorRunning = true;
					animator.start();
				}
			}
			if(moveRight - moveDist / 10.0 >= 0) {
				moveRight -= moveDist / 10.0;
			} else {
				moveRight = 0;
			}
			
		} else {
			//stop animation when moving has finished
			if(animatorRunning) {
				open = false;
				open();
			}
		}
	}
	
	private void open() {
		//start animation to start moving the histogram to the right
		if(moveRight < moveDist) {
			if(!animatorRunning ) {
				if(!animator.isAnimating()) {
					animatorRunning = true;
					animator.start();
				}
			}
			moveRight += moveDist / 10.0;
		} else {
			//stop animation when moving has finished
			if(animatorRunning) {
				animatorRunning = false;
				newBoxSelected = false;
				open = true;
				this.animator.stop();
			}
		}
	}
	
	private void removeFreePlacedForSelectedProbes() {
		Set<Probe> sProbes = panel.viewModel.getSelectedProbes();
		for(Probe pb : sProbes) {
			this.removeFreePlaced(this.probeTable.get(pb));
		}
		this.panel.updatePlot();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_ALT_GRAPH:
			altGraphDown = true;
			break;
		case KeyEvent.VK_Z:
			removeFreePlacedForSelectedProbes();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_ALT_GRAPH:
			altGraphDown = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	public void setProbeForDragging(Probe pb) {
		this.selectedProbeForDragging = this.probeTable.get(pb);
		panel.getCanvas().repaint();
	}
	
	public void setFreePlaced(DraggableProbe pb) {
		pb.setFreePlaced();
		this.freePlacedLabels.add(pb);
	}
	
	/**
	 * remove free placed
	 */
	public void removeFreePlaced(DraggableProbe pb) {
		pb.removeFreePlaced();
		this.freePlacedLabels.remove(pb);
	}
}

/**
 * Class that allows to sort probes according to their latest expression value
 * @author jaeger
 */
class SortableProbe implements Comparable<SortableProbe> {
	public Probe pb;
	public double lastValue;
	
	public SortableProbe(Probe pb, double lastValue) {
		this.pb = pb;
		this.lastValue = lastValue;
	}

	@Override
	public int compareTo(SortableProbe p) {
		if(this.lastValue < p.lastValue) return -1;
		if(this.lastValue > p.lastValue) return 1;
		return 0;
	}
}
