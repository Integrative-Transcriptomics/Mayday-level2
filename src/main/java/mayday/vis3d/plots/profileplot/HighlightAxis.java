package mayday.vis3d.plots.profileplot;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLContext;

import mayday.vis3d.utilities.Camera2D;

/**
 * @author jaeger
 *
 */
public class HighlightAxis implements MouseListener, MouseMotionListener {
	
	private boolean axisSelected = false;
	
	private ProfilePlotPanel panel;
	
	private Integer labelAxis;
	private int anchorDim;
	private double mousePosition;
	private double[] experimentTimepoints;
	
	/**
	 * @param panel
	 */
	public HighlightAxis(ProfilePlotPanel panel) {
		this.panel = panel;
		this.initialize();
		panel.getCanvas().addMouseListener(this);
		panel.getCanvas().addMouseMotionListener(this);
	}
	
	private void initialize() {
		mousePosition = panel.translateX;
		anchorDim = panel.settings.getLabelingSetting().getLabelAnchorDim();
		labelAxis = new String("LabelAxis").hashCode();
		this.update();
	}
	
	/**
	 * update the highlight axis
	 */
	public void update() {
		double xIt = panel.coordSystem.getSetting().getChartSetting().getWidth() / panel.coordSystem.getSetting().getIteration()[0];
		experimentTimepoints = panel.settings.getTimepoints().getExperimentTimpoints();
		
		anchorDim = panel.settings.getLabelingSetting().getLabelAnchorDim();
		
		if(panel.settings.getTimepoints().useTimepoints()) {
			this.mousePosition = xIt * (experimentTimepoints[anchorDim] - experimentTimepoints[0]);
		} else {
			this.mousePosition = anchorDim * xIt;
		}
	}
	
	private double getLabelAxisPosition() {
		double xIt = panel.coordSystem.getSetting().getXIteration();
		
		if(panel.settings.getTimepoints().useTimepoints()) {
			return xIt * (experimentTimepoints[anchorDim] - experimentTimepoints[0]);
		} else {
			return xIt * anchorDim;
		}
	}
	
	private double getProjectedMousePositionX(MouseEvent e, GL gl) {
		int x = e.getX();
		
		int viewport[] = new int[4];
	    double mvmatrix[] = new double[16];
	    double projmatrix[] = new double[16];
	    double wcoord[] = new double[4];

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
        gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix, 0);
        gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmatrix, 0);

        double scale = ((Camera2D)panel.camera).getScale();
        
        //get the unprojected mouse position
        panel.glu.gluUnProject((double) x, (double)0, (double)1, 
        	mvmatrix, 0,
            projmatrix, 0, 
            viewport, 0, 
            wcoord, 0);
        
        //correct for the scale
        double newM = (wcoord[0] / (4.0 * scale) + 200);
        
        //correct for the translation in x direction
        double scaleI = 1.0 / scale;
        double xtrans = -((Camera2D)panel.camera).getPosition2D().getX() * scaleI;
        newM += xtrans;
        
        return newM;
	}
	
	private int calculateAD(double aPos) {
		if(panel.settings.getTimepoints().useTimepoints()) {
			double minDist = Double.MAX_VALUE;
			int minDistIndex = 0;
			
			for(int i = 0; i < experimentTimepoints.length; i++) {
				double curDist = Math.abs(aPos - (experimentTimepoints[i] - experimentTimepoints[0]));
				if(curDist < minDist) {
					minDist = curDist;
					minDistIndex = i;
				}
			}
			return minDistIndex;
		} else {
			return (int)Math.round(aPos);
		}
	}
	
	/**
	 * @param gl
	 * @param glRender
	 */
	public void draw(GL gl, int glRender) {
		double xPos = this.getLabelAxisPosition();
		
		if(glRender == GL.GL_SELECT) {
			gl.glLoadName(this.labelAxis.intValue());
		}
		
		gl.glPushMatrix();
			gl.glColor3fv(panel.convertColor(panel.settings.getSelectionColor().getColorValue()), 0);
			gl.glLineWidth(3.0f);
			gl.glBegin(GL.GL_LINES);
				gl.glVertex3d(xPos, 0, 0.05);
				gl.glVertex3d(xPos, panel.coordSystem.getSetting().getChartSetting().getHeight() + 10, 0.05);
			gl.glEnd();
		gl.glPopMatrix();
		gl.glLineWidth(1.1f);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}
	
	public int getAxisHashCode() {
		return this.labelAxis.intValue();
	}
	
	public void setSelected(boolean selected) {
		this.axisSelected = selected;
		if(axisSelected) {
			panel.getCanvas().repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch(e.getButton()) {
			case MouseEvent.BUTTON3:
			if(axisSelected) {
				int numExps = panel.viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
				if(anchorDim >= 0 && anchorDim < numExps) {
					panel.settings.labelingSetting.histAnchors.setSelectedIndex(anchorDim);
				}
				axisSelected = false;
			}
			break;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(axisSelected) {
			int current = panel.getCanvas().getContext().makeCurrent();
			if(current == GLContext.CONTEXT_CURRENT) {
				GL gl = panel.getCanvas().getContext().getGL();
				this.mousePosition = getProjectedMousePositionX(e, gl);
				panel.getCanvas().getContext().release();
			} else {
				return;
			}
			
			double xit = panel.coordSystem.getSetting().getXIteration();
			double aPos = this.mousePosition / xit;
			
			int aD = calculateAD(aPos);
			
			//ensure ranges!
			if(aD < 0) {
				aD = 0;
			}
			if(aD >= experimentTimepoints.length) {
				aD = experimentTimepoints.length - 1;
			}
			
			//perform update if anchor dimension changed
			if(aD != anchorDim) {
				this.anchorDim = aD;
			}
			panel.getCanvas().repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
}
