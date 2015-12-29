package mayday.vis3d.plots.profileplot;

import java.awt.Dimension;
import java.util.Set;

import com.jogamp.opengl.GL2;

import mayday.core.Probe;
import mayday.core.settings.Setting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3d.AbstractPlot2DPanel;
import mayday.vis3d.cs.CoordinateSystem2D;
import mayday.vis3d.cs.StandardCoordinateSystem2D;

/**
 * @author jaeger
 *
 */
public class ProfilePlotPanel extends AbstractPlot2DPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5669904148815114457L;
	
	protected CoordinateSystem2D coordSystem;
	protected ProfilePlotSetting settings;
	
	protected HighlightAxis highlightAxis;
	protected LabelHistogram labelHistogram;
	
	protected double translateX = 50;
	protected double translateY = 50;
	
	protected double[] minMax;

	@Override
	public double[] getBestStartIteration() {
		double xIt = this.viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
		double yIt = 20.0;
		return new double[]{xIt, yIt};
	}

	@Override
	public void drawNotSelectable(GL2 gl) {
		gl.glPushMatrix();
			gl.glTranslated(translateX, translateY, 0);
			this.coordSystem.draw(gl, glu);
			this.coordSystem.drawLabeling(gl, settings.getTimepoints());
		gl.glPopMatrix();
	}

	@Override
	public void drawSelectable(GL2 gl, int glRender) {
		gl.glPushMatrix();
			gl.glTranslated(translateX, translateY, 0);
			this.drawProfiles(gl, glRender);
			if(settings.getLabelingSetting().drawProbeLabels()) {
				this.highlightAxis.draw(gl, glRender);
				this.labelHistogram.draw(gl, glRender);
			} else {
				this.labelHistogram.setSelection(null);
			}
		gl.glPopMatrix();
	}
	
	
	private void drawProfiles(GL2 gl, int glRender) {
		Set<Probe> allProbes = viewModel.getProbes();
		boolean useTimepoints = settings.getTimepoints().useTimepoints();
		double[] experimentTimepoints = settings.getTimepoints().getExperimentTimpoints();
		
		double xIt = coordSystem.getSetting().getXIteration();
		double tmp = 0.0;
		
		for(Probe pb : allProbes) {
			double[] profile = adjust(viewModel.getProbeValues(pb));
			double iterationX = 0;
			
			if(viewModel.isSelected(pb)) {
				gl.glColor3fv(convertColor(this.settings.getSelectionColor().getColorValue()), 0);
				gl.glLineWidth(2.0f);
				tmp = 0.01;
			} else {
				gl.glColor3fv(convertColor(coloring.getColor(pb)), 0);
				gl.glLineWidth(1.1f);
				tmp = 0.0;
			}
			
			if(glRender == GL2.GL_SELECT) {
				gl.glLoadName(pb.hashCode());
			}
			
			gl.glBegin(GL2.GL_LINE_STRIP);
				for(int i = 0; i < profile.length; i++) {
					double xpos = iterationX;
					if(useTimepoints) {
						xpos = (experimentTimepoints[i] - experimentTimepoints[0]) * xIt;
					}
					double ypos = coordSystem.getSetting().getChartSetting().getHeight()/2.0 + profile[i];
					gl.glVertex3d(xpos, ypos, tmp);
					iterationX += xIt;
				}
			gl.glEnd();
		}
	}

	@Override
	public double[] getInitDimension() {
		return null;
	}

	@Override
	public void initializeDisplay(GL2 gl) {
		this.coordSystem.initLabeling(gl);
		this.labelHistogram.initialize(gl);
		this.update(gl);
	}

	@Override
	public void setupPanel(PlotContainer plotContainer) {
		plotContainer.setPreferredTitle("Profile Plot with Label Histogram", this);
		this.setMinimumSize(new Dimension(800, 400));
		this.setPreferredSize(new Dimension(800, 400));
		super.setMinimumSize(new Dimension(100, 100));
		
		this.coordSystem = new StandardCoordinateSystem2D(this, null);
		
		if(settings == null) {
			settings = new ProfilePlotSetting(this);
			settings.addSetting(coloring.getSetting());
			settings.addSetting(coordSystem.getSetting());
		}
		
		for (Setting s : settings.getChildren()){
			plotContainer.addViewSetting(s, this);
		}
		
		this.highlightAxis = new HighlightAxis(this);
		this.labelHistogram = new LabelHistogram(this);
		
		this.selectionHandler = new ProfilePlotSelectionHandler(this, highlightAxis, labelHistogram);
		this.selectionHandler.initializeHashMaps();
		this.selectionHandler.registerTo(this.canvas);
	}

	@Override
	public void update(GL2 gl) {
		this.setBackgroundColor(settings.getBackgroundColor());
		//center the plot after resize event
		this.recalculateXY();
		
		//update min max expression values
		this.calcMinMaxExpressionValues();
		
		//update axis labeling
		this.updateAxisLabeling();
		
		//update time-point settings
		this.updateTimpointSettings();
		
		if(settings.getLabelingSetting().drawProbeLabels()) {
			//update highlight axis
			this.highlightAxis.update();
			
			//update label histogram
			this.labelHistogram.update();
		}
		
		translateX = (-coordSystem.getSetting().getChartSetting().getWidth()) / 2.0;
		translateY = (-coordSystem.getSetting().getChartSetting().getHeight()) / 2.0;
	}
	
	@Override
	public Object[] getSelectableObjects() {
		return this.labelHistogram.getBoxIds();
	}
	
	@Override
	public void processSelectedObjects(Object[] objects, boolean controlDown, boolean altDown) {
		Integer box = (Integer)objects[0];
		this.labelHistogram.setSelection(box);
	}
	
	private void updateTimpointSettings() {
		double[] experimentTimepoints = settings.getTimepoints().getExperimentTimpoints();
		if(settings.getTimepoints().useTimepoints()) {
			if(experimentTimepoints.length > 0) {
				double max = experimentTimepoints[experimentTimepoints.length - 1];
				double min = experimentTimepoints[0];
				coordSystem.getSetting().getGridSetting().setXIteration((int)(max - min + 1));
			}
		} else {
			int numExp = this.viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
			coordSystem.getSetting().getGridSetting().setXIteration(numExp);
		}
	}
	
	private void updateAxisLabeling() {
		String[] expNames = viewModel.getDataSet().getMasterTable().getExperimentDisplayNames().toArray(new String[0]);
		this.coordSystem.getLabeling().setXLabels(expNames);
		this.calcMinMaxExpressionValues();
		this.coordSystem.getLabeling().setYLabels(minMax[1], minMax[0], coordSystem.getSetting().getChartSetting().getHeight());
	}

	private void calcMinMaxExpressionValues() {
		minMax = new double[2];
		Set<Probe> allProbes = this.viewModel.getProbes();
		minMax[0] = Math.ceil(this.viewModel.getMaximum(null, allProbes));
		minMax[1] = Math.floor(this.viewModel.getMinimum(null, allProbes));
	}

	private void recalculateXY() {
//		double width = coordSystem.getSetting().getChartSetting().getWidth() + labelHistogram.getMaxWidth();
//		double height = coordSystem.getSetting().getChartSetting().getHeight();
//		
//		double widthDif = getWidth() - width + 20;
//		widthDif /= 2.0;
//		double heightDif = getHeight() - height + 20;
//		heightDif /= 2.0;
//		
////		double oldScale = ((Camera2D)this.camera).getScale();
////		
////		double newScale = oldScale;
////		
////		if((totalWidth-20)*oldScale > getWidth()) {
////			newScale = oldScale * (getWidth()/((totalWidth-20)*oldScale));
////		}
////		
////		if(totalHeight * newScale > getHeight()) {
////			newScale = newScale * (getHeight()/(newScale*totalHeight));
////		}
////		
////		((Camera2D)this.camera).setScale(newScale);
//		
//		this.translateX = widthDif;
//		this.translateY = heightDif;
	}

	protected double adjust(double value) {
		double spreadY = minMax[0] - minMax[1];
		double tmp = minMax[0] - spreadY / 2.0;
		return ((value - tmp) / spreadY) * coordSystem.getSetting().getChartSetting().getHeight();
	}
	
	private double[] adjust(double[] values) {
		double[] adjusted = new double[values.length];
		for(int i = 0; i < values.length; i++) {
			adjusted[i] = adjust(values[i]);
		}
		return adjusted;
	}
}