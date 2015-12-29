package mayday.tiala.multi.gui.plots.multiprobemultiprofileplot3d;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Set;

import com.jogamp.opengl.GL2;

import mayday.core.Probe;
import mayday.core.settings.Setting;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.probes.MultiProbe;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3d.AbstractPlot3DPanel;
import mayday.vis3d.cs.CoordinateSystem3D;
import mayday.vis3d.cs.PlaneCoordinateSystem3D;
import mayday.vis3d.primitives.Lighting;
import mayday.vis3d.utilities.Camera3D;

/**
 * @author jaeger
 */
public class MultiProbeMultiProfileplot3DPanel extends AbstractPlot3DPanel {

	protected MultiProbeMultiProfileplot3DSetting settings;
	private CoordinateSystem3D coordSystem;

	private double minValue = 0;
	private double maxValue = 0;
	private double spreadY = 0;

	private int numOfMultiProbes = 0;

	protected AlignmentStore Store;
	
	/**
	 * @param store
	 */
	public MultiProbeMultiProfileplot3DPanel(AlignmentStore store) {
		super();
		this.Store = store;
		this.numOfMultiProbes = store.getAlignedDataSets().getNumberOfDataSets();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5565445579611874316L;

	@Override
	public void drawNotSelectable(GL2 gl) {
		gl.glPushMatrix();
		this.coordSystem.draw(gl, glu);

		if(this.coordSystem.getSetting().getGridSetting().getGridVisible()) {
			this.coordSystem.drawGrid(gl);
		}

		if(this.coordSystem.getSetting().getLabelingSetting().getAxesLabeling()) {
			if(settings.getTimepointSetting().useTimepoints()) {
				double[] experimentTimepoints = settings.getTimepointSetting().getExperimentTimpoints();
				this.coordSystem.drawLabeling(gl, ((Camera3D)this.camera).getRotation(), experimentTimepoints);
			} else {
				this.coordSystem.drawLabeling(gl, ((Camera3D)this.camera).getRotation());
			}
		}

		if(settings.getDrawProfilePlanes()) {
			double[] dimension = coordSystem.getDimension3D();
			gl.glPushMatrix();
			gl.glTranslated(-dimension[0], -(dimension[1] + adjust(this.minValue)), -dimension[2]);
			this.drawProfilePlanes(gl);
			gl.glPopMatrix();
		}

		gl.glPopMatrix();	
	}

	@Override
	public void drawSelectable(GL2 gl, int glRender) {
		double[] dimension = coordSystem.getDimension3D();

		gl.glPushMatrix();
		gl.glTranslated(-dimension[0], -(dimension[1] + adjust(this.minValue)), -dimension[2]);
		this.drawData(gl, glRender);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(dimension[0], -dimension[1], 0);
		this.drawZLabels(gl, glRender);
		gl.glPopMatrix();
	}

	private void drawData(GL2 gl, int glRender) {
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		MultiProbe[] allProbes = viewModel.getProbes().toArray(new MultiProbe[0]);
		double zit = this.coordSystem.getIteration()[2];

		double k = 0;
		for(int i = 0; i < allProbes.length; i += this.numOfMultiProbes, k += zit) {
			gl.glPushMatrix();
			gl.glTranslated(0, 0, k);
			this.drawMultiProbes(gl, allProbes, i, glRender);
			gl.glPopMatrix();
		}
		
		gl.glDisable(GL2.GL_BLEND);
	}
	
	protected double[] convertColor4d(Color c, double alpha) {
		return new double[]{c.getRed()/255.0, c.getGreen()/255.0, c.getBlue()/255.0, alpha};
	}

	private void drawMultiProbes(GL2 gl, MultiProbe[] allProbes, int startIndex, int glRender) {
		double xit = coordSystem.getSetting().getIteration()[0];
		boolean useTimepoints = settings.getTimepointSetting().useTimepoints();

		for(int i = startIndex; i < startIndex + this.numOfMultiProbes; i++) {
			MultiProbe pb = allProbes[i];
			
			double[] values = adjust(viewModel.getProbeValues(pb));
			if(viewModel.isSelected(pb)) {
				if(glRender != GL2.GL_SELECT) {
					gl.glColor4dv(convertColor4d(Store.getSettings().getSelectionColors()[pb.position], 1), 0);
					gl.glLineWidth(3.0f);
				}
			} else {
				if(glRender != GL2.GL_SELECT) {
					double minTrans = 0.6 * settings.getProfileTransparency();
					gl.glColor4dv(convertColor4d(Store.getSettings().getSelectionColors()[pb.position], minTrans), 0);
					gl.glLineWidth(1.1f);
				}
			}

			if(glRender == GL2.GL_SELECT) {
				gl.glLoadName(pb.hashCode());
			}

			gl.glBegin(GL2.GL_LINE_STRIP);
			for(int j = 0; j < values.length; j++) {
				double xpos = j * xit;
				if(useTimepoints) {
					double[] experimentTimepoints = settings.getTimepointSetting().getExperimentTimpoints();
					xpos = (experimentTimepoints[j] - experimentTimepoints[0]) * xit;
				}
				gl.glVertex3d(xpos, values[j], 0);
			}
			gl.glEnd();
		}
	}

	private void drawProfilePlanes(GL2 gl) {
		MultiProbe[] allProbes = viewModel.getProbes().toArray(new MultiProbe[0]);
		boolean useTimepoints = settings.getTimepointSetting().useTimepoints();
		double xit = coordSystem.getIteration()[0];
		double zit = coordSystem.getIteration()[2];

		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		gl.glPushMatrix();
		gl.glBegin(GL2.GL_QUADS);
		int k = -1;
		int maxIt = allProbes.length - this.numOfMultiProbes;

		for(int i = 0; i < maxIt; i++) {
			MultiProbe mpb1 = allProbes[i];
			MultiProbe mpb2 = allProbes[i + this.numOfMultiProbes];
			
			if(!settings.drawProfileSurface(mpb1.position)) {
				if(i % this.numOfMultiProbes == 0) {
					k++;
				}
				continue;
			}
			
			double[] values1 = adjust(viewModel.getProbeValues(mpb1));
			double[] values2 = adjust(viewModel.getProbeValues(mpb2));

			float[] c = convertColor(Store.getSettings().getSelectionColors()[mpb1.position]);
			gl.glColor4d(c[0], c[1], c[2], settings.getTransparencyValue());

			if(i%this.numOfMultiProbes == 0) {
				k++;
			}

			double z1 = k * zit;
			double z2 = (k+1) * zit;

			for(int j = 0; j < values1.length - 1; j++) {
				double x1 = j * xit;
				double x2 = (j+1) * xit;
				if(useTimepoints) {
					double[] experimentTimepoints = settings.getTimepointSetting().getExperimentTimpoints();
					x1 = (experimentTimepoints[j] - experimentTimepoints[0]) * xit;
					x2 = (experimentTimepoints[j+1] - experimentTimepoints[0]) * xit;
				}

				gl.glVertex3d(x1, values1[j], z1);
				gl.glVertex3d(x2, values1[j+1], z1);
				gl.glVertex3d(x2, values2[j+1], z2);
				gl.glVertex3d(x1, values2[j], z2);
			}
		}
		gl.glEnd();
		gl.glPopMatrix();

		gl.glDisable(GL2.GL_BLEND);
	}

	private void drawZLabels(GL2 gl, int glRender) {
		double zit = this.coordSystem.getIteration()[2];
		double k = -this.coordSystem.getSetting().getVisibleArea().getDepth();
		this.coordSystem.getRenderer().setColor(settings.getLabelsColor());
		MultiProbe[] allProbes = viewModel.getProbes().toArray(new MultiProbe[0]);
		
		for(int i = 0; i < allProbes.length; i++) {
			//take display names from the top-most data set
			//the top-most data set has index 0
			if(allProbes[i].position == 0) {
				if(glRender == GL2.GL_SELECT) {
					gl.glLoadName(allProbes[i].hashCode());
				}
				if(viewModel.isSelected(allProbes[i])) {
					this.coordSystem.getRenderer().setColor(Color.RED);
				} else {
					this.coordSystem.getRenderer().setColor(Color.BLACK);
				}
				this.drawZLabel(gl, allProbes[i].getDisplayName(), 0, 0, k);
				k += zit;
			}
		}
		
		//restore default value
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
	public double[] getInitDimension() {
		double width = viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
		if(width == 0.0){
			width = 20.0;
		}
		return new double[]{width, 5.0, 5.0};
	}

	@Override
	public void initializeDisplay(GL2 gl) {
		Lighting.initLighting(gl);

		this.coordSystem.initAxesLabeling(gl);
//		this.settings.getTimepointSetting().setUseTimepoints(true);

		this.updateDrawTypes(gl);
	}

	@Override
	public void setupPanel(PlotContainer plotContainer) {
		plotContainer.setPreferredTitle("3D Multi-Probe Plot", this);
		this.coordSystem = new PlaneCoordinateSystem3D(this, null);

		if(settings == null) {
			settings = new MultiProbeMultiProfileplot3DSetting(this, this.numOfMultiProbes);
			settings.addSetting(coloring.getSetting());
			settings.addCoordinateSystemSetting(this.coordSystem.getSetting());
		}

		for (Setting s : settings.getChildren()){
			plotContainer.addViewSetting(s, this);
		}
	}

	private void updateDrawTypes(GL2 gl) {
		this.calculateMinMax();
		coordSystem.initAxesLabeling(gl);
		
		double depth = coordSystem.getSetting().getVisibleArea().getDepth() * 2.0;

		int numProbes = viewModel.getProbes().size() / this.numOfMultiProbes;

		if(numProbes > 1) {
			if(depth == 0.0) {
				coordSystem.getSetting().getVisibleArea().setDepth(5.0);
			} else {
				coordSystem.getSetting().getGridSetting().setZIteration((numProbes - 1));
			}
		} else {
			coordSystem.getSetting().getVisibleArea().setDepth(0.0);
			coordSystem.getSetting().getGridSetting().setZIteration(1);
		}

		if(settings.getTimepointSetting().getExperimentTimpoints().length > 0) {
			//set x iteration depending on whether time points should be used
			if(settings.getTimepointSetting().useTimepoints()) {
				double[] experimentTimepoints = settings.getTimepointSetting().getExperimentTimpoints();
				int range = (int)(experimentTimepoints[experimentTimepoints.length - 1] - experimentTimepoints[0] + 1);
				coordSystem.getSetting().getGridSetting().setXIteration(range);
			} else {
				int numExps = viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
				coordSystem.getSetting().getGridSetting().setXIteration(numExps);
			}
		}
		
		String[] xlabels;
		if(settings.getTimepointSetting().useTimepoints()) {
			//set x labels when using time points
			xlabels = new String[settings.getTimepointSetting().getExperimentTimpoints().length];
			for(int i = 0; i < xlabels.length; i++) {
				xlabels[i] = settings.getTimepointSetting().getExperimentTimpoints()[i]+"";
				//remove unnecessary ending
				if(xlabels[i].endsWith(".0")) {
					xlabels[i] = xlabels[i].substring(0, xlabels[i].length()-2);
				}
			}
		} else {
			//set x labels when not using time points
			xlabels = new String[viewModel.getDataSet().getMasterTable().getNumberOfExperiments()];
			for(int i = 0; i < xlabels.length; i++) {
				xlabels[i] = viewModel.getDataSet().getMasterTable().getExperimentName(i);
				//remove unnecessary ending
				if(xlabels[i].endsWith(".0")) {
					xlabels[i] = xlabels[i].substring(0, xlabels[i].length()-2);
				}
			}
		}

		this.coordSystem.getLabeling().setXLabels(xlabels);

		if(settings.getTimepointSetting().getExperimentTimpoints().length > 0) {
			//set y labels only if there is data to display
			if(spreadY != 0) {
				this.coordSystem.getLabeling().setYLabels(minValue, maxValue, coordSystem.getSetting().getVisibleArea().getHeight());
			}
		} else {
			this.coordSystem.getLabeling().setYLabels(new String[0]);
		}
		
		settings.setSelectionColors(Store.getSettings().getSelectionColors());
	}

	@Override
	public void update(GL2 gl) {
		this.updateDrawTypes(gl);
		this.setBackgroundColor(settings.getBackgroundColor());
	}

	/**
	 * @param numberOfDataSets
	 */
	public void setNumOfMultiProbes(int numberOfDataSets) {
		this.numOfMultiProbes = numberOfDataSets;
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
		if(allProbes.size() != 0) {
			this.maxValue = Math.ceil(this.viewModel.getMaximum(null, allProbes));
			this.minValue = Math.floor(this.viewModel.getMinimum(null, allProbes));
			this.spreadY = this.maxValue - this.minValue;
		}
	}
}
