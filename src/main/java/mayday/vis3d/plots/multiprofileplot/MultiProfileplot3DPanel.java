package mayday.vis3d.plots.multiprofileplot;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jogamp.opengl.GL2;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.settings.Setting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.VolatileProbeList;
import mayday.vis3d.AbstractPlot3DPanel;
import mayday.vis3d.cs.CoordinateSystem3D;
import mayday.vis3d.cs.PlaneCoordinateSystem3D;
import mayday.vis3d.primitives.Lighting;
import mayday.vis3d.utilities.Camera3D;
/**
 * 
 * @author G\u00FCnter J\u00E4ger
 * @version 20100715
 */
public class MultiProfileplot3DPanel extends AbstractPlot3DPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 900105847764132495L;
	
	private MultiProfileplot3DSetting settings;
	private CoordinateSystem3D coordSystem;
	private List<ProbeList> probeLists;
	private int[] profileIdentifier;
	private int allData;
	private int centroids;
	private int centroidPlanes;
	private ArrayList<String> zLabels;

	private double minValue = 0;
	private double maxValue = 0;
	private double spreadY = 0;

	@Override
	public void drawNotSelectable(GL2 gl) {
		gl.glPushMatrix();
			this.coordSystem.draw(gl, glu);
			
			if(this.coordSystem.getSetting().getGridSetting().getGridVisible()) {
				this.coordSystem.drawGrid(gl);
			}
			
			if(this.coordSystem.getSetting().getLabelingSetting().getAxesLabeling()) {
				if(settings.getTimepoints().useTimepoints()) {
					double[] experimentTimepoints = settings.getTimepoints().getExperimentTimpoints();
					this.coordSystem.drawLabeling(gl, ((Camera3D)this.camera).getRotation(), experimentTimepoints);
				} else {
					this.coordSystem.drawLabeling(gl, ((Camera3D)this.camera).getRotation());
				}
			}
			
			if(settings.getDrawCentroids()) {
				double[] dimension = coordSystem.getDimension3D();
				gl.glPushMatrix();
				gl.glTranslated(-dimension[0], -(dimension[1] + adjust(this.minValue)), -dimension[2]);
					gl.glCallList(centroids);
				gl.glPopMatrix();
			}
			
			if(settings.drawCentroidPlanes()) {
				double[] dimension = coordSystem.getDimension3D();
				gl.glPushMatrix();
				gl.glTranslated(-dimension[0], -(dimension[1] + adjust(this.minValue)), -dimension[2]);
					gl.glCallList(centroidPlanes);
				gl.glPopMatrix();
			}
		gl.glPopMatrix();
	}
	
	private void drawZLabels(GL2 gl, int glRender) {		
		double zit = this.coordSystem.getIteration()[2];
		double k = -this.coordSystem.getSetting().getVisibleArea().getDepth();
		
		if(glRender == GL2.GL_SELECT) {
			if(settings.getDrawProfiles()) {
				for(int i = 0; i  < zLabels.size(); i++, k += zit) {
					gl.glLoadName(zLabels.get(i).hashCode());
					this.drawZLabel(gl, zLabels.get(i), 
							0, 0, k);
				}
			}
		} else {
			for(int i = 0; i  < zLabels.size(); i++, k += zit) {
				this.coordSystem.getRenderer().setColor(this.probeLists.get(this.probeLists.size()-i-1).getColor());
				this.drawZLabel(gl, zLabels.get(i), 0, 0, k);
			}
		}
		
		this.coordSystem.getRenderer().setColor(Color.BLACK);
	}
	
	private void drawZLabel(GL2 gl, String label, double x, double y,double z) {
		Rectangle2D bounds = this.coordSystem.getRenderer().getBounds(label);
		double h2 = bounds.getHeight() * this.coordSystem.getScale() / 2.0;
		
		gl.glPushMatrix();
		gl.glTranslated(x+0.1, y-h2, z);
		
		gl.glRotated(-((Camera3D)this.camera).getRotation()[2], 0, 0, 1);
		gl.glRotated(-((Camera3D)this.camera).getRotation()[1], 0, 1, 0);
		gl.glRotated(-((Camera3D)this.camera).getRotation()[0], 1, 0, 0);
		
		this.coordSystem.getRenderer().begin3DRendering();
		this.coordSystem.getRenderer().draw3D(label, 0.0f, -(float)h2, 0.0f, (float)this.coordSystem.getScale());
		this.coordSystem.getRenderer().end3DRendering();
		
		gl.glPopMatrix();
	}

	@Override
	public void drawSelectable(GL2 gl, int glRender) {
		double[] dimension = coordSystem.getDimension3D();
		
		if(settings.getDrawProfiles()) {
			gl.glPushMatrix();
			gl.glTranslated(-dimension[0], -(dimension[1] + adjust(this.minValue)), -dimension[2]);
				if(glRender == GL2.GL_SELECT) {
					this.drawData(gl, GL2.GL_SELECT);
				} else {
					gl.glDisable(GL2.GL_LIGHTING);
					gl.glCallList(allData);
				}
			gl.glPopMatrix();
		}
		
		gl.glPushMatrix();
		gl.glTranslated(dimension[0], -dimension[1], 0);
			this.drawZLabels(gl, glRender);
		gl.glPopMatrix();
	}

	@Override
	public void initializeDisplay(GL2 gl) {
		Lighting.initLighting(gl);
		//initialize probe list identifiers
		int start = gl.glGenLists(this.profileIdentifier.length);
		for(int i = 0; i < this.profileIdentifier.length; i++) {
			this.profileIdentifier[i] = start + i;
		}
		
		allData = gl.glGenLists(3);
 		this.centroids = allData + 1;
		this.centroidPlanes = allData + 2;
		
		this.coordSystem.initAxesLabeling(gl);
		
		if(this.probeLists.size() > 1) {
			coordSystem.getSetting().getGridSetting().setZIteration(this.probeLists.size()-1);
			//TODO set camera position!
		} else {
			coordSystem.getSetting().getGridSetting().setZIteration(1);
		}
		
		this.updateDrawTypes(gl);
	}

	@Override
	public void setupPanel(PlotContainer plotContainer) {
		plotContainer.setPreferredTitle("3D Multi Profile Plot", this);
		this.probeLists = this.viewModel.getProbeLists(true);
		this.profileIdentifier = new int[this.probeLists.size()];
		this.coordSystem = new PlaneCoordinateSystem3D(this, null, true);
		
		if(settings == null) {
			settings = new MultiProfileplot3DSetting(this);
			settings.addSetting(coloring.getSetting());
			settings.addCoordinateSystemSetting(this.coordSystem.getSetting());
		}
		
		for (Setting s : settings.getChildren()){
			plotContainer.addViewSetting(s, this);
		}
		
		this.probeLists = viewModel.getProbeLists(true);
		
		this.zLabels = new ArrayList<String>(this.probeLists.size());
		for(int i = 0; i < this.probeLists.size(); i++) {
			zLabels.add("");
		}
	}

	@Override
	public void update(GL2 gl) {
		this.updateDrawTypes(gl);
		this.setBackgroundColor(settings.getBackgroundColor());
	}

	private void updateDrawTypes(GL2 gl) {
		this.calculateMinMax();

		if(settings.getTimepoints().useTimepoints()) {
			double[] experimentTimepoints = settings.getTimepoints().getExperimentTimpoints();
			int range = (int)(experimentTimepoints[experimentTimepoints.length - 1] - experimentTimepoints[0] + 1);
			coordSystem.getSetting().getGridSetting().setXIteration(range);
		} else {
			int numExps = viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
			coordSystem.getSetting().getGridSetting().setXIteration(numExps);
		}
		
		gl.glDeleteLists(this.profileIdentifier[0], this.profileIdentifier.length);
		for(int i = 0; i < this.profileIdentifier.length; i++) {
			gl.glNewList(this.profileIdentifier[i], GL2.GL_COMPILE);
				this.drawProbeList(gl, this.probeLists.get(i), GL2.GL_RENDER);
			gl.glEndList();
		}
		
		String[] xlabels = new String[viewModel.getDataSet().getMasterTable().getNumberOfExperiments()];
		
		for(int i = 0; i < xlabels.length; i++) {
			xlabels[i] = viewModel.getDataSet().getMasterTable().getExperimentDisplayName(i);
		}
		
		this.coordSystem.getLabeling().setXLabels(xlabels);
		this.coordSystem.getLabeling().setYLabels(minValue, maxValue, coordSystem.getSetting().getVisibleArea().getHeight());
		
		int i = this.probeLists.size() - 1;
		for(ProbeList pl: viewModel.getProbeLists(true)) {
			String name = ((VolatileProbeList)pl).getOriginalName();
			zLabels.set(i--, name);
		}
		
		gl.glDeleteLists(allData, 3);
		
		gl.glNewList(allData, GL2.GL_COMPILE);
			this.drawData(gl, GL2.GL_RENDER);
		gl.glEndList();
		
		gl.glNewList(centroids, GL2.GL_COMPILE);
			this.drawCentroids(gl);
		gl.glEndList();
		
		gl.glNewList(centroidPlanes, GL2.GL_COMPILE);
			this.drawCentroidPlanes(gl);
		gl.glEndList();
	}
	
	private void drawData(GL2 gl, int glRender) {
		if(glRender == GL2.GL_SELECT) {
			for(int i = this.probeLists.size()-1; i >= 0; i--) {
				gl.glPushMatrix();
				gl.glTranslated(0, 0, this.coordSystem.getIteration()[2] * (this.probeLists.size()-1-i));
				this.drawProbeList(gl, this.probeLists.get(i), glRender);
				gl.glPopMatrix();
			}
		} else {
			for(int i = this.probeLists.size()-1; i >= 0 ; i--) {
				gl.glPushMatrix();
				gl.glTranslated(0, 0, this.coordSystem.getIteration()[2] * (this.probeLists.size()-1-i));
				gl.glCallList(this.profileIdentifier[i]);
				gl.glPopMatrix();
			}
		}
	}
	
	private void drawCentroids(GL2 gl) {
		boolean useTimepoints = settings.getTimepoints().useTimepoints();
		double xit = coordSystem.getSetting().getIteration()[0];
		//some settings for centroids
		gl.glLineWidth(2.0f);
		
		for(int i = 0; i < this.probeLists.size(); i++) {
			gl.glColor3fv(convertColor(this.probeLists.get(i).getColor()), 0);
			gl.glPushMatrix();
			gl.glTranslated(0, 0, this.coordSystem.getIteration()[2] * (this.probeLists.size()-1-i));
			double[] centroid = adjust(viewModel.getProbeValues(this.probeLists.get(i).getMean()));
			
			gl.glBegin(GL2.GL_LINE_STRIP);
				for(int j = 0; j < centroid.length; j++) {
					double xpos = j * xit;
					if(useTimepoints) {
						double[] experimentTimepoints = settings.getTimepoints().getExperimentTimpoints();
						xpos = (experimentTimepoints[j] - experimentTimepoints[0]) * xit;
					}
					gl.glVertex3d(xpos, centroid[j], 0.02);
				}
			gl.glEnd();
			gl.glPopMatrix();
		}
	}
	
	private void drawCentroidPlanes(GL2 gl) {
		boolean useTimepoints = settings.getTimepoints().useTimepoints();
		double xit = coordSystem.getSetting().getIteration()[0];
		List<double[]> centroids = new ArrayList<double[]>();
		for(int i = 0; i < this.probeLists.size(); i++) {
			centroids.add(adjust(viewModel.getProbeValues(this.probeLists.get(i).getMean())));
		}
		
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glPushMatrix();
		gl.glBegin(GL2.GL_QUADS);
			for(int i = 0; i < centroids.size() - 1; i++) {
				double[] centroid1 = centroids.get(i);
				double[] centroid2 = centroids.get(i+1);
				
				double z1 = this.coordSystem.getIteration()[2] * (this.probeLists.size()-1-i);
				double z2 = this.coordSystem.getIteration()[2] * (this.probeLists.size()-2-i);
				
				float[] c1 = convertColor(this.probeLists.get(i).getColor());
				float[] c2 = convertColor(this.probeLists.get(i+1).getColor());
				
				for(int j = 0; j < centroid1.length-1; j++) {
					double x1 = j * xit;
					double x2 = (j+1) * xit;
					
					if(useTimepoints) {
						double[] experimentTimepoints = settings.getTimepoints().getExperimentTimpoints();
						x1 = (experimentTimepoints[j] - experimentTimepoints[0]) * xit;
						x2 = (experimentTimepoints[j+1] - experimentTimepoints[0]) * xit;
					}
					
					gl.glColor4d(c1[0], c1[1], c1[2], settings.getTransparencyValue());
					gl.glVertex3d(x1, centroid1[j], z1);
					gl.glVertex3d(x2, centroid1[j+1], z1);
					gl.glColor4d(c2[0], c2[1], c2[2], settings.getTransparencyValue());
					gl.glVertex3d(x2, centroid2[j+1], z2);
					gl.glVertex3d(x1, centroid2[j], z2);
				}
			}
		gl.glEnd();
		gl.glPopMatrix();
		
		gl.glDisable(GL2.GL_BLEND);
	}

	private void drawProbeList(GL2 gl, ProbeList probeList, int glRender) {
		double tmp = 0;
		double xit = coordSystem.getSetting().getIteration()[0];
		boolean useTimepoints = settings.getTimepoints().useTimepoints();
		
		Set<Probe> allProbes = probeList.getAllProbes();
		for(Probe pb : allProbes) {
			double[] values = adjust(this.viewModel.getProbeValues(pb));
			
			if(viewModel.isSelected(pb)) {
				if(glRender != GL2.GL_SELECT) {
					gl.glColor3fv(convertColor(settings.getSelectionColor()), 0);
					gl.glLineWidth(2.0f);
				}
				tmp = 0.01;
			} else {
				if(glRender != GL2.GL_SELECT) {
					gl.glColor3fv(convertColor(coloring.getColor(pb)), 0);
					gl.glLineWidth(1.1f);
				}
				tmp = 0;
			}
			
			if(glRender == GL2.GL_SELECT) {
				gl.glLoadName(pb.hashCode());
			}
			
			gl.glBegin(GL2.GL_LINE_STRIP);
				for(int j = 0; j < values.length; j++) {
					double xpos = j * xit;
					if(useTimepoints) {
						double[] experimentTimepoints = settings.getTimepoints().getExperimentTimpoints();
						xpos = (experimentTimepoints[j] - experimentTimepoints[0]) * xit;
					}
					gl.glVertex3d(xpos, values[j], tmp);
				}
			gl.glEnd();
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
		return zLabels.toArray();
	}

	@Override
	public void processSelectedObjects(Object[] objects, boolean controlDown, boolean altDown) {
		Set<Probe> newSelection = new HashSet<Probe>();
		for(int i = 0; i < objects.length; i++) {
			int index = zLabels.indexOf(objects[i]);
			if(index != -1) {
				newSelection.addAll(this.probeLists.get(this.probeLists.size()-1-index).toCollection());
			}
		}
		
		Set<Probe> previousSelection = viewModel.getSelectedProbes();
		if (controlDown && altDown) {
			previousSelection.removeAll(newSelection);
			newSelection = previousSelection;
		} else if (controlDown) {
			Set<Probe> intersection = new HashSet<Probe>();
			intersection.addAll(newSelection);
			intersection.retainAll(previousSelection);
			newSelection.addAll(previousSelection);
			newSelection.removeAll(intersection);
		} else if (altDown) {
			newSelection.retainAll(previousSelection);
		}
		viewModel.setProbeSelection(newSelection);
	}

	@Override
	public double[] getInitDimension() {
		double  zdim = viewModel.getProbeLists(true).size() > 1 ? 
				viewModel.getProbeLists(true).size() : 0.0; 
		return new double[]{10.0, 5.0, zdim};
	}
}
