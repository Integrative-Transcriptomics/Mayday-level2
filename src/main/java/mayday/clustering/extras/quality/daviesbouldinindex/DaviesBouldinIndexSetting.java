package mayday.clustering.extras.quality.daviesbouldinindex;

import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class DaviesBouldinIndexSetting extends HierarchicalSetting {
	
	public static final int MEAN = 0;
	public static final int MEDIAN = 1;

	private RestrictedStringSetting centroidCalcMethod;
	private DistanceMeasureSetting distanceMeasure;
	
	public DaviesBouldinIndexSetting() {
		super("Davies Bouldin Index - Setting");
		
		centroidCalcMethod = new RestrictedStringSetting("Centroid Calculation Method", "Method for calculating the centroid(s)", 0, "mean", "median");
		distanceMeasure = new DistanceMeasureSetting("Distance Measure", "The distance measure should be the same as for the clustering for meaningful results!", DistanceMeasureManager.get("Euclidean"));
		this.addSetting(centroidCalcMethod);
		this.addSetting(distanceMeasure);
	}
	
	public DaviesBouldinIndexSetting clone() {
		return (DaviesBouldinIndexSetting)this.reflectiveClone();
	}
	
	public int getCentroidCalculationMethod() {
		return centroidCalcMethod.getSelectedIndex();
	}
	
	public void setCentroidCalculationMethod(int centroidCalc) {
		this.centroidCalcMethod.setSelectedIndex(centroidCalc);
	}
	
	public DistanceMeasurePlugin getDistanceMeasure() {
		return distanceMeasure.getInstance();
	}
	
	public void setDistanceMeasure(DistanceMeasurePlugin dist) {
		this.distanceMeasure.setInstance(dist);
	}
}
