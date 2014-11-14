package mayday.vis3d.plots.radialprofileplot;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Set;

import javax.media.opengl.GL;

import mayday.core.Probe;
import mayday.core.settings.Setting;
import mayday.vis3.SortedProbeList;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3d.AbstractPlot3DPanel;
import mayday.vis3d.cs.CoordinateSystem3D;
import mayday.vis3d.cs.StandardCoordinateSystem3D;
import mayday.vis3d.utilities.Camera3D;

import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.j2d.TextRenderer;
/**
 * 
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class RadialProfileplotPanel extends AbstractPlot3DPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3094357806252560484L;
	protected SortedProbeList probes;
	private TextRenderer renderer = new TextRenderer(new Font("Sans.Serif", Font.BOLD, 24),
			true, true);
	
	private RadialProfileplotSetting settings;
	private CoordinateSystem3D coordSystem;
	private double[] experimentTimepoints = new double[0];
	private double maxValue;
	private double minValue;
	private double spreadY;
	
	private int data;
	private int surface;
	
	private double maxLabelWidth = 2;
	private int tmpNumProbes = 0;
	
	private FPSAnimator animator;
	
	private double fontScale = 0;

	@Override
	public void drawNotSelectable(GL gl) {
		//check if scene should be animated
		if(settings.animateScene()) {
			if(!this.animator.isAnimating()) {
				this.animator.start();
			}
			double[] rotation = ((Camera3D)this.camera).getRotation();
			((Camera3D)this.camera).setRotation(rotation[Camera3D.X], rotation[Camera3D.Y], 
					rotation[Camera3D.Z] + settings.getAnimationSpeed());
		} else {
			if(this.animator.isAnimating()) {
				this.animator.stop();
			}
		}
		
		if(settings.getDrawSurface()) {
			double width2 = coordSystem.getSetting().getVisibleArea().getWidth() / 2.0;
			gl.glPushMatrix();
			gl.glTranslated(0, 0, width2);
				gl.glCallList(surface);
			gl.glPopMatrix();
		}
	}

	@Override
	public void drawSelectable(GL gl, int glRender) {
		//check if there is to do something
		if(settings.getDrawLabels() || settings.getDrawWireframe()) {
			double width2 = coordSystem.getSetting().getVisibleArea().getWidth() / 2.0;
			gl.glPushMatrix();
			gl.glTranslated(0, 0, width2);
			if(settings.getDrawWireframe()) {
				if(glRender == GL.GL_SELECT) {
					this.drawData(gl, glRender);
				} else {
					gl.glCallList(data);
				}
				
			}
			if(settings.getDrawLabels()) {
				this.drawLabeling(gl, glRender);
			}
			gl.glPopMatrix();
		}
	}
	
	private void drawLabeling(GL gl, int glRender) {
		float scale = (float)settings.getFontScale();
		double angleIncrement = 360.0 / probes.size();
		//double backRot = -angleIncrement;
		
		gl.glPushMatrix();
			for(int i = 0; i < this.probes.size(); i++) {
				Probe pb = probes.get(i);
				
				if(viewModel.isSelected(pb)) {
					renderer.setColor(settings.getSelectionColor());
				}
				
				gl.glPushMatrix();
					gl.glTranslated(0, settings.getRadius(), 0);
					if(glRender == GL.GL_SELECT) {
						gl.glLoadName(pb.hashCode());
					}
					gl.glPushMatrix();
						gl.glTranslated(0, adjust(this.maxValue), maxLabelWidth);
						gl.glRotated(90, 0, 0, 1);
						gl.glRotated(90, 0, 1, 0);
						//gl.glRotated(((Camera3D)this.camera).getRotation()[Camera3D.Z], 1, 0, 0);
						//gl.glRotated(-((Camera3D)this.camera).getRotation()[Camera3D.X], 1, 0, 0);
						gl.glPushMatrix();
						//gl.glRotated(backRot, 1, 0, 0);
						renderer.begin3DRendering();
						renderer.draw3D(pb.getDisplayName(), 0, 0, 0, scale);
						renderer.end3DRendering();
						gl.glPopMatrix();
					gl.glPopMatrix();
				gl.glPopMatrix();
				
				gl.glRotated(angleIncrement, 0, 0, 1);
				
				if(viewModel.isSelected(pb)) {
					renderer.setColor(Color.BLACK);
				}
				//backRot -= angleIncrement;
			}
		gl.glPopMatrix();
	}
	
	private void drawSurface(GL gl) {
		boolean useTimepoints = settings.getTimepoints().useTimepoints();
		int numProbes = viewModel.getProbes().size();
		int numExps = viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
		double angleIncrement = 360.0 / this.viewModel.getProbes().size();
		
		ArrayList<double[]> terrainColors = new ArrayList<double[]>();
		double[] terrainHeights = new double[numProbes * numExps];
		
		for(int i = 0; i < probes.size(); i++) {
			Probe pb = probes.get(i);
			double[] profile = this.adjust(viewModel.getProbeValues(pb));
			double[] c = this.convertColord(this.coloring.getColor(pb));
			for(int j = 0; j < profile.length; j++) {
				terrainColors.add(c);
				terrainHeights[i * numExps + j] = (float)profile[j];
			}
		}
		
		double radius = settings.getRadius();
		
		gl.glPushMatrix();
		for(int i = 0; i < numProbes - 1; i++) {
			gl.glBegin(GL.GL_QUADS);
			for(int j = 0; j < numExps - 1; j++) {
				double[] color = terrainColors.get(i * numExps + j);
				
				double x = 0;
				double y = terrainHeights[i * numExps + j] + radius;
				double z = j;
				if(useTimepoints) {
					z = experimentTimepoints[j] - experimentTimepoints[0];
				}
	
				gl.glColor3dv(this.getVertexColor(y - radius, color), 0);
				gl.glVertex3d(x, y, -z);
				
				color = terrainColors.get(i * numExps + j + 1);
				x = 0;
				y = terrainHeights[i * numExps + j + 1] + radius;
				
				if(useTimepoints) {
					z = experimentTimepoints[j+1] - experimentTimepoints[0];
				} else {
					z = j + 1;
				}
				
				gl.glColor3dv(this.getVertexColor(y - radius, color), 0);
				gl.glVertex3d(x, y, -z);
				
				color = terrainColors.get((i+1) * numExps + j + 1);
				y = terrainHeights[(i+1) * numExps + j + 1];
				gl.glColor3dv(this.getVertexColor(y, color), 0);
				
				x = -Math.cos(Math.toRadians(90.0 - angleIncrement)) * (y + radius);
				y = Math.sin(Math.toRadians(90.0 - angleIncrement)) * (y + radius);
				
				if(useTimepoints) {
					z = experimentTimepoints[j+1] - experimentTimepoints[0];
				} else {
					z = j + 1;
				}
				
				gl.glVertex3d(x, y, -z);
				
				color = terrainColors.get((i+1) * numExps + j);
				y = terrainHeights[(i+1) * numExps + j];
				gl.glColor3dv(this.getVertexColor(y, color), 0);
				
				x = -Math.cos(Math.toRadians(90.0 - angleIncrement)) * (y + radius);
				y = Math.sin(Math.toRadians(90.0 - angleIncrement)) * (y + radius);
				
				if(useTimepoints) {
					z = experimentTimepoints[j] - experimentTimepoints[0];
				} else {
					z = j;
				}

				gl.glVertex3d(x, y, -z);
			}
			gl.glEnd();
			gl.glRotated(angleIncrement, 0, 0, 1);
		}
		
		//add planes from last profile to the first
		int i = numProbes - 1;
		gl.glBegin(GL.GL_QUADS);
		for(int j = 0; j < numExps - 1; j++) {
			double[] color = terrainColors.get(i * numExps + j);
			
			double x = 0;
			double y = terrainHeights[i * numExps + j] + radius;
			double z = j;
			if(useTimepoints) {
				z = experimentTimepoints[j] - experimentTimepoints[0];
			}

			gl.glColor3dv(this.getVertexColor(y - radius, color), 0);
			gl.glVertex3d(x, y, -z);
			
			color = terrainColors.get(i * numExps + j + 1);
			x = 0;
			y = terrainHeights[i * numExps + j + 1] + radius;
			
			if(useTimepoints) {
				z = experimentTimepoints[j+1] - experimentTimepoints[0];
			} else {
				z = j + 1;
			}
			
			gl.glColor3dv(this.getVertexColor(y - radius, color), 0);
			gl.glVertex3d(x, y, -z);
			
			color = terrainColors.get(j + 1);
			y = terrainHeights[j + 1];
			gl.glColor3dv(this.getVertexColor(y, color), 0);
			
			x = -Math.cos(Math.toRadians(90.0 - angleIncrement)) * (y + radius);
			y = Math.sin(Math.toRadians(90.0 - angleIncrement)) * (y + radius);
			
			if(useTimepoints) {
				z = experimentTimepoints[j+1] - experimentTimepoints[0];
			} else {
				z = j + 1;
			}
			
			gl.glVertex3d(x, y, -z);
			
			color = terrainColors.get(j);
			y = terrainHeights[j];
			gl.glColor3dv(this.getVertexColor(y, color), 0);
			
			x = -Math.cos(Math.toRadians(90.0 - angleIncrement)) * (y + radius);
			y = Math.sin(Math.toRadians(90.0 - angleIncrement)) * (y + radius);
			
			if(useTimepoints) {
				z = experimentTimepoints[j] - experimentTimepoints[0];
			} else {
				z = j;
			}

			gl.glVertex3d(x, y, -z);
		}
		gl.glEnd();
		
		gl.glPopMatrix();
	}
	
	private double[] getVertexColor(double height, double[] color) {
		double f = (height - adjust(minValue)) / (adjust(maxValue) - adjust(minValue));
		return new double[]{color[0] * f, color[1] * f, color[2] * f};
	}
	
	private double[] convertColord(Color c) {
		if(c == null){
			c = Color.DARK_GRAY;
		}
		if(c.equals(Color.BLACK)) {
			c = Color.DARK_GRAY;
		}
		return new double[] {c.getRed() / 255.0, c.getGreen() / 255.0,
				c.getBlue() / 255.0};
	}

	private void drawData(GL gl, int glRender) {
		boolean useTimepoints = settings.getTimepoints().useTimepoints();
		double angleIncrement = 360.0 / this.viewModel.getProbes().size();
		
		gl.glPushMatrix();
		for(int i = 0; i < probes.size(); i++) {
			Probe pb = probes.get(i);
			double[] values = this.adjust(viewModel.getProbeValues(pb));
			
			//check for selected probes and change color and line width if necessary
			if(viewModel.isSelected(pb)) {
				if(glRender != GL.GL_SELECT) {
					gl.glColor3fv(convertColor(settings.getSelectionColor()), 0);
					gl.glLineWidth(2.0f);
				}
			} else {
				if(!settings.getDrawSurface()) {
					gl.glColor3fv(convertColor(coloring.getColor(pb)), 0);
				} else {
					gl.glColor3dv(getVertexColor(this.minValue, convertColord(Color.GRAY)), 0);
				}
				gl.glLineWidth(1.1f);
			}
			
			if(glRender == GL.GL_SELECT) {
				gl.glLoadName(pb.hashCode());
			}
			
			gl.glPushMatrix();
			gl.glTranslated(0, settings.getRadius(), 0);
			gl.glBegin(GL.GL_LINE_STRIP);
				for(int k = 0; k < values.length; k++) {
					double zpos = k;
					if(useTimepoints) {
						zpos = experimentTimepoints[k] - experimentTimepoints[0];
					}
					// + 0.01 to draw lines slightly on top of the surface for better visualization
					gl.glVertex3d(0, values[k] + 0.01, -zpos);
				}
			gl.glEnd();
			gl.glPopMatrix();
			gl.glRotated(angleIncrement, 0, 0, 1);
		}
		gl.glPopMatrix();
	}

	@Override
	public void initializeDisplay(GL gl) {
		renderer.setSmoothing(true);
		renderer.setColor(Color.BLACK);
		
		data = gl.glGenLists(2);
		surface = data + 1;
		
		this.calculateMinMax();
		this.updateDrawTypes(gl);
	}

	@Override
	public void setupPanel(PlotContainer plotContainer) {
		plotContainer.setPreferredTitle("Radial Profile Plot", this);
		
		this.coordSystem = new StandardCoordinateSystem3D(this, null);
		this.probes = new SortedProbeList(viewModel, viewModel.getProbes());
		
		if(this.settings == null) {
			this.settings = new RadialProfileplotSetting(this);
			this.settings.addSetting(this.coloring.getSetting());
			this.settings.addSetting(probes.getSetting());
		}
		
		//add everything to the view menu
		for (Setting s : settings.getChildren()){
			plotContainer.addViewSetting(s, this);
		}
		
		this.animator = new FPSAnimator(this.getCanvas(), 25);
		
		//terrible hack to add a new WindowListener to stop the animator if the plot closes
		PlotWindow c = (PlotWindow)this.getParent().getParent().getParent().getParent().getParent();
		c.addWindowListener(new RadialPlotCloser());
		
		this.updateOptimalView();
	}

	@Override
	public void update(GL gl) {
		this.updateDrawTypes(gl);
		this.setBackgroundColor(settings.getBackgroundColor());
	}
	
	private void updateDrawTypes(GL gl) {
		this.calculateMinMax();
		experimentTimepoints = settings.getTimepoints().getExperimentTimpoints();
		
		if(settings.getTimepoints().useTimepoints()) {
			coordSystem.getSetting().getVisibleArea()
				.setWidth((experimentTimepoints[experimentTimepoints.length-1] - experimentTimepoints[0]));
		} else {
			coordSystem.getSetting().getVisibleArea()
				.setWidth(viewModel.getDataSet().getMasterTable().getNumberOfExperiments());
		}
		
		gl.glDeleteLists(data, 2);
		
		gl.glNewList(data, GL.GL_COMPILE);
			this.drawData(gl, GL.GL_RENDER);
		gl.glEndList();
		
		gl.glNewList(surface, GL.GL_COMPILE);
			this.drawSurface(gl);
		gl.glEndList();
		
		this.updateOptimalView();
	}
	
	private void updateOptimalView() {
		if(tmpNumProbes != this.probes.size() || fontScale != settings.getFontScale()) {
			tmpNumProbes = this.probes.size();
			fontScale = settings.getFontScale();
			double circumference = 0;
			for(int i = 0; i < this.probes.size(); i++) {
				double lw = renderer.getBounds(probes.get(i).getDisplayName()).getWidth() * fontScale;
				if(lw > maxLabelWidth) {
					maxLabelWidth = lw;
				}
				circumference += renderer.getBounds(probes.get(i).getDisplayName()).getHeight() * fontScale;
			}
			settings.setRadius(circumference / (2.0 * Math.PI));
		}
	}
	
	private double[] adjust(double[] values) {
		double[] adjusted = new double[values.length];
		for(int i = 0; i < values.length; i++) {
			adjusted[i] = adjust(values[i]);
		}
		return adjusted;
	}
	
	private double adjust(double value) {
		double height = this.coordSystem.getSetting().getVisibleArea().getHeight();
		return (value / this.spreadY) * 2 * height;
	}
	
	private void calculateMinMax() {
		Set<Probe> allProbes = this.viewModel.getProbes();
		this.maxValue = Math.ceil(this.viewModel.getMaximum(null, allProbes));
		this.minValue = Math.floor(this.viewModel.getMinimum(null, allProbes));
		this.spreadY = this.maxValue - this.minValue;
	}

	@Override
	public Object[] getSelectableObjects() {
		return null;
	}

	@Override
	public void processSelectedObjects(Object[] objects, boolean controlDown, boolean altDown) {}

	@Override
	public double[] getInitDimension() {
		double zdim = (this.viewModel.getDataSet().getMasterTable().getNumberOfExperiments() - 1) / 2.0;
		return new double[]{5.0, 5.0, zdim};
	}
	
	private class RadialPlotCloser extends WindowAdapter {
		public void windowClosed(WindowEvent w) {
			//stop the animator if the plot gets closed and the animator is still running
			if(animator.isAnimating()) {
				animator.stop();
			}
		}
	}
}
