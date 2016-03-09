package mayday.vis3d.plots.heightmap;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Set;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.awt.TextRenderer;

import mayday.core.Probe;
import mayday.core.settings.Setting;
import mayday.vis3.SortedProbeList;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3d.AbstractPlot3DPanel;
import mayday.vis3d.cs.CoordinateSystem3D;
import mayday.vis3d.cs.PlaneCoordinateSystem3D;
import mayday.vis3d.utilities.Camera3D;
import mayday.vis3d.utilities.VectorText;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class HeightMapPanel extends AbstractPlot3DPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3598297487490537107L;
	
	private TextRenderer renderer = new TextRenderer(new Font("Sans.Serif", Font.BOLD, 24),
			true, true);
	private VectorText vectorText;
	
	protected HeightMapSetting settings;
	
	
	private int profiles;
	private int surface;

	private double maxLabelWidth = 2;
	
	private double maxValue;
	private double minValue;
	private double spreadY;
	
	protected SortedProbeList probes;
	private double[] experimentTimepoints = new double[0];
	
	private CoordinateSystem3D coordSystem;

	@Override
	public void drawNotSelectable(GL2 gl) {
		double width2 = coordSystem.getSetting().getVisibleArea().getWidth() / 2.0;
		double y = (this.maxValue + this.minValue)/2.0;
		double depth2 = coordSystem.getSetting().getVisibleArea().getDepth() / 2.0;
		depth2 *= settings.getProfileDistance();
		
		gl.glPushMatrix();
		gl.glTranslated(-depth2, -adjust(y), width2);
			gl.glCallList(surface);
		gl.glPopMatrix();
	}

	@Override
	public void drawSelectable(GL2 gl, int glRender) {
		double width2 = coordSystem.getSetting().getVisibleArea().getWidth() / 2.0;
		double y = (this.maxValue + this.minValue)/2.0;
		double depth2 = coordSystem.getSetting().getVisibleArea().getDepth() / 2.0;
		depth2 *= settings.getProfileDistance();
		
		gl.glPushMatrix();
		gl.glTranslated(-depth2, -adjust(y), width2);
		if(settings.showProfiles()) {
			if(glRender == GL2.GL_SELECT) {
				this.drawData(gl, glRender);
			} else {
				gl.glCallList(profiles);
			}
		}
		if(settings.showLabels()) {
			this.drawLabeling(gl, glRender);
		}
		gl.glPopMatrix();
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
	
	private void drawLabeling(GL2 gl, int glRender) {
		gl.glPushMatrix();
			for(int i = 0; i < this.probes.size(); i++) {
				Probe pb = probes.get(i);

				// get rgb values
				float r, g, b;
				if(viewModel.isSelected(pb)) {
					r = settings.getSelectionColor().getRed();
					g = settings.getSelectionColor().getGreen();
					b = settings.getSelectionColor().getBlue();
				} else {
					// default black
					r = 0;
					g = 0;
					b = 0;
				}
				
				gl.glPushMatrix();
					gl.glTranslated(i * settings.getProfileDistance(), 0, 0);
					if(glRender == GL2.GL_SELECT) {
						gl.glLoadName(pb.hashCode());
					}
					gl.glPushMatrix();
						gl.glTranslated(0, adjust(viewModel.getProbeValues(pb)[0]), maxLabelWidth);
						gl.glRotated(90, 0, 1, 0);
						gl.glRotated(-((Camera3D)this.camera).getRotation()[Camera3D.Z], 0, 0, 1);
						gl.glRotated(-((Camera3D)this.camera).getRotation()[Camera3D.X], 1, 0, 0);
						gl.glPushMatrix();
						vectorText.setGL(gl);
						vectorText.drawText(pb.getDisplayName(), renderer, 0, 0, 0,
								(float)settings.getFontScale(),
								r, g, b);
						gl.glPopMatrix();
					gl.glPopMatrix();
				gl.glPopMatrix();
			}
		gl.glPopMatrix();
	}
	
	private void drawData(GL2 gl, int glRender) {
		boolean useTimepoints = settings.getTimepoints().useTimepoints();
		
		gl.glPushMatrix();
		for(int i = 0; i < probes.size(); i++) {
			Probe pb = probes.get(i);
			double[] values = this.adjust(viewModel.getProbeValues(pb));
			
			//check for selected probes and change color and line width if necessary
			if(viewModel.isSelected(pb)) {
				if(glRender != GL2.GL_SELECT) {
					gl.glColor3fv(convertColor(settings.getSelectionColor()), 0);
					gl.glLineWidth(2.0f);
				}
			} else {
				gl.glColor3fv(convertColor(coloring.getColor(pb)), 0);
				gl.glLineWidth(1.1f);
			}
			
			if(glRender == GL2.GL_SELECT) {
				gl.glLoadName(pb.hashCode());
			}
			
			gl.glPushMatrix();
			gl.glTranslated(i * settings.getProfileDistance(), 0, 0);
			gl.glBegin(GL2.GL_LINE_STRIP);
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
		}
		gl.glPopMatrix();
	}
	
	private void drawSurface(GL2 gl) {
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		boolean useTimepoints = settings.getTimepoints().useTimepoints();
		
		int numProbes = viewModel.getProbes().size();
		int numExps = viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
		
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
		
		gl.glPushMatrix();
		for(int i = 0; i < numProbes - 1; i++) {
			gl.glBegin(GL2.GL_QUADS);
			for(int j = 0; j < numExps - 1; j++) {
				double[] color = terrainColors.get(i * numExps + j);

				double x = i * settings.getProfileDistance();
				double y = terrainHeights[i * numExps + j];
				double z = j;
				
				if(useTimepoints) {
					z = experimentTimepoints[j] - experimentTimepoints[0];
				}
	
				gl.glColor4dv(this.getVertexColor4d(y - 1, color), 0);
				gl.glVertex3d(x, y, -z);
				
				color = terrainColors.get(i * numExps + j + 1);
				x = i * settings.getProfileDistance();
				y = terrainHeights[i * numExps + j + 1];
				
				if(useTimepoints) {
					z = experimentTimepoints[j+1] - experimentTimepoints[0];
				} else {
					z = j + 1;
				}
				
				gl.glColor4dv(this.getVertexColor4d(y - 1, color), 0);
				gl.glVertex3d(x, y, -z);
				
				color = terrainColors.get((i+1) * numExps + j + 1);
				x = (i + 1) * settings.getProfileDistance();
				y = terrainHeights[(i+1) * numExps + j + 1];
				gl.glColor4dv(this.getVertexColor4d(y, color), 0);

				if(useTimepoints) {
					z = experimentTimepoints[j+1] - experimentTimepoints[0];
				} else {
					z = j + 1;
				}
				
				gl.glVertex3d(x, y, -z);
				
				color = terrainColors.get((i+1) * numExps + j);
				x = (i + 1) * settings.getProfileDistance();
				y = terrainHeights[(i+1) * numExps + j];
				gl.glColor4dv(this.getVertexColor4d(y, color), 0);
				
				if(useTimepoints) {
					z = experimentTimepoints[j] - experimentTimepoints[0];
				} else {
					z = j;
				}

				gl.glVertex3d(x, y, -z);
			}
			gl.glEnd();
		}
		
		gl.glPopMatrix();
		gl.glDisable(GL2.GL_BLEND);
	}
	
	private double[] getVertexColor4d(double height, double[] color) {
		double f = (height - adjust(minValue)) / (adjust(maxValue) - adjust(minValue));
		return new double[]{color[0] * f, color[1] * f, color[2] * f, settings.getTransparencyValue()};
	}
	
	private void calculateMinMax() {
		Set<Probe> allProbes = this.viewModel.getProbes();
		this.maxValue = Math.ceil(this.viewModel.getMaximum(null, allProbes));
		this.minValue = Math.floor(this.viewModel.getMinimum(null, allProbes));
		this.spreadY = this.maxValue - this.minValue;
	}

	@Override
	public void initializeDisplay(GL2 gl) {
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glDisable(GL2.GL_CULL_FACE);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		
		renderer.setSmoothing(true);

		vectorText = new VectorText(this.glu);

		this.profiles = gl.glGenLists(1);
		this.surface = gl.glGenLists(1);
		
		this.calculateMinMax();
		this.updateDrawTypes(gl);
	}
	
	private void updateDrawTypes(GL2 gl) {
		this.calculateMinMax();
		experimentTimepoints = settings.getTimepoints().getExperimentTimpoints();
		
		if(settings.getTimepoints().useTimepoints()) {
			coordSystem.getSetting().getVisibleArea()
				.setWidth((experimentTimepoints[experimentTimepoints.length-1] - experimentTimepoints[0]));
		} else {
			coordSystem.getSetting().getVisibleArea()
				.setWidth(viewModel.getDataSet().getMasterTable().getNumberOfExperiments());
		}
		
		gl.glDeleteLists(profiles, 1);
		gl.glDeleteLists(surface, 1);
		
		gl.glNewList(profiles, GL2.GL_COMPILE);
			this.drawData(gl, GL2.GL_RENDER);
		gl.glEndList();
		
		gl.glNewList(surface, GL2.GL_COMPILE);
			this.drawSurface(gl);
		gl.glEndList();
		
		this.updateOptimalView();
	}
	
	private void updateOptimalView() {
		maxLabelWidth = 0.0;
		for(int i = 0; i < this.probes.size(); i++) {
			double lw = renderer.getBounds(probes.get(i).getDisplayName()).getWidth() * (float)settings.getFontScale();
			if(lw > maxLabelWidth) {
				maxLabelWidth = lw;
			}
		}
	}

	private double[] convertColord(Color c) {
		if(c == null){
			c = Color.WHITE;
		}
		if(c.equals(Color.BLACK)) {
			c = Color.WHITE;
		}
		return new double[] {c.getRed() / 255.0, c.getGreen() / 255.0,
				c.getBlue() / 255.0};
	}
	
	@Override
	public void setupPanel(PlotContainer plotContainer) {
		plotContainer.setPreferredTitle("Height Map", this);
		
		this.probes = new SortedProbeList(viewModel, viewModel.getProbes());
		
		this.coordSystem = new PlaneCoordinateSystem3D(this, null);
		
		if(settings == null) {
			settings = new HeightMapSetting(this);
			settings.addSetting(coloring.getSetting());
			this.settings.addSetting(probes.getSetting());
		}

		for (Setting s : settings.getChildren()){
			plotContainer.addViewSetting(s, this);
		}
	}

	@Override
	public Object[] getSelectableObjects() {
		return null;
	}

	@Override
	public void processSelectedObjects(Object[] objects, boolean controlDown, boolean altDown) {}

	@Override
	public void update(GL2 gl) {
		vectorText.setGL(gl);
		this.updateDrawTypes(gl);
		this.setBackgroundColor(settings.getBackgroundColor());
	}

	@Override
	public double[] getInitDimension() {
		double xdim = viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
		double zdim = viewModel.getProbes().size();
		return new double[]{xdim, 10.0, zdim};	
	}
}
