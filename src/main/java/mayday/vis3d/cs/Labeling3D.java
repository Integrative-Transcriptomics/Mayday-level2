package mayday.vis3d.cs;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import mayday.vis3d.cs.settings.CoordinateSystemSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class Labeling3D {
	
	private String[] xlabels = new String[0];
	private String[] ylabels = new String[0];
	private String[] zlabels = new String[0];
	
	private String xAxisLabel = "";
	private String yAxisLabel = "";
	private String zAxisLabel = "";
	
	private CoordinateSystemSetting settings;
	
	private boolean checkXHorizontal = false;
	
	/**
	 * @param settings
	 */
	public Labeling3D(CoordinateSystemSetting settings) {
		this.settings = settings;
	}
	
	/**
	 * set x axis labels
	 * @param labels
	 */
	public void setXLabels(String[] labels) {
		this.xlabels = labels;
	}
	
	/**
	 * set y axis labels
	 * @param labels
	 */
	public void setYLabels(String[] labels) {
		this.ylabels = labels;
	}
	
	/**
	 * set z axis labels
	 * @param labels
	 */
	public void setZLabels(String[] labels) {
		this.zlabels = labels;
	}
	
	/**
	 * @param labels
	 */
	public void setZLabels(List<String> labels) {
		this.zlabels = new String[labels.size()];
		for(int i = 0; i < labels.size(); i++) {
			this.zlabels[i] = labels.get(i);
		}
	}
	
	private String[] initLabel(double min, double max, double width, double iteration) {
		int numSteps = (int)(width * 2 / iteration);
		double step = getBestStepWidth((max-min)/2, numSteps/2, iteration, 0.25);
		
		String[] labels = new String[numSteps];
		Arrays.fill(labels, "");
		
		int i = 0;
		for(double iter = min; iter <= max && i < numSteps; iter += step) {
			labels[i++] = Double.toString(Math.round(iter * 1000000.0) / 1000000.0);
		}
		
		labels[0] = "";
		
		return labels;
	}
	
	/**
	 * Generate x labels given the maximum spread of the data set in x direction
	 * max should be a positive number 
	 * @param min 
	 * @param max
	 */
	public void setXLabels(double min, double max) {
		double width = settings.getVisibleArea().getWidth();
		double[] iteration = settings.getIteration();
		xlabels = this.initLabel(min, max, width, iteration[0]);
	}
	
	/**
	 * Generate y labels given the maximum spread of the data set in y direction
	 * max should be a positive number 
	 * @param min 
	 * @param max
	 * @param dim 
	 */
	public void setYLabels(double min, double max, double dim) {
		double[] iteration = settings.getIteration();
		ylabels = this.initLabel(min, max, dim, iteration[1]);
	}
	/**
	 * Generate z labels given the maximum spread of the data set in z direction
	 * max should be a positive number 
	 * @param min 
	 * @param max
	 */
	public void setZLabels(double min, double max) {
		double depth = settings.getVisibleArea().getDepth();
		double[] iteration = settings.getIteration();
		zlabels = this.initLabel(min, max, depth, iteration[2]);
	}
	
	/**
	 * @return labels of the x axis
	 */
	public String[] getXLabels() {
		return this.xlabels;
	}
	
	/**
	 * @return labels of the y axis
	 */
	public String[] getYLabels() {
		return this.ylabels;
	}
	
	/**
	 * @return labels of the z axis
	 */
	public String[] getZLabels() {
		return this.zlabels;
	}
	
	/*
	 * calculate the best step width for a given number of labels and a given iteration
	 */
	private double getBestStepWidth(double max, int numSteps, double it, double minIncr) {
		
		if(max == 0) {
			return 1;
		}
		
		double p = 0;
		BigDecimal s = new BigDecimal(0);
		BigDecimal inc = new BigDecimal(minIncr);
		BigDecimal sf;
		//determine power
		double log = Math.log10(max/(numSteps - 1));
		
		if(log <= 0) {
			p = Math.ceil(log);
		} else {
			p = Math.floor(log);
		}
		
		BigDecimal f = new BigDecimal(Math.pow(10, p));
		
		while(true) {
			s = s.add(inc);
			sf = s.multiply(f);
			//stop if the last label is larger then max
			if(sf.doubleValue() * (numSteps) >= max) {
				if(sf.doubleValue() * (numSteps) >= max + sf.doubleValue()) {
					//minimal increase was too large, recalculate with smaller minimal increase
					return getBestStepWidth(max, numSteps, it, minIncr / 5.0);
				}
				return sf.doubleValue();
			}
		}
	}
	
	/**
	 * enable check for horizontal/vertical drawing
	 */
	public void enableCheckXHorizontal() {
		this.checkXHorizontal = true;
	}
	
	/**
	 * disable check for horizontal/vertical drawing 
	 */
	public void disableCheckXHorizontal() {
		this.checkXHorizontal = false;
	}
	
	/**
	 * @return true, if a check for horizontal or vertical drawing should be performed, else false
	 */
	public boolean checkXHorizontal() {
		return this.checkXHorizontal;
	}

	/**
	 * @param xAxisLabel
	 */
	public void setXAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	/**
	 * @return x-axis label
	 */
	public String getXAxisLabel() {
		return xAxisLabel;
	}

	/**
	 * @param yAxisLabel
	 */
	public void setYAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}

	/**
	 * @return y-axis label
	 */
	public String getYAxisLabel() {
		return yAxisLabel;
	}

	/**
	 * @param zAxisLabel
	 */
	public void setZAxisLabel(String zAxisLabel) {
		this.zAxisLabel = zAxisLabel;
	}

	/**
	 * @return z-axis label
	 */
	public String getZAxisLabel() {
		return zAxisLabel;
	}
}
