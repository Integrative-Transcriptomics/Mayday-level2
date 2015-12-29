package mayday.clustering.extras.quality.potentialfunction;

import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

/**
 * 
 * @author Jennifer Lange
 *
 */
public class PotentialFunctionSetting extends HierarchicalSetting {
	
	public static final int MEAN = 0;
	public static final int MEDIAN = 1;

	private RestrictedStringSetting centroidCalculationMethod;
	private DistanceMeasureSetting distanceMeasure;
	
	public PotentialFunctionSetting() {
		super("Potential Function Setting");
		centroidCalculationMethod = new RestrictedStringSetting("Centroid Method", null, 0, "mean", "median");
		distanceMeasure = new DistanceMeasureSetting("Distance Measure", "The distance measure used for the calculation of the potential function.\nThis should be the same as for the preliminary clustering", DistanceMeasureManager.get("Euclidean"));
		this.addSetting(centroidCalculationMethod);
		this.addSetting(distanceMeasure);
	}

	public PotentialFunctionSetting clone() {
		return (PotentialFunctionSetting)this.reflectiveClone();
	}
	
	public int getCentroidCalculationMethod() {
		return centroidCalculationMethod.getSelectedIndex();
	}
	
	public DistanceMeasurePlugin getDistanceMeasure() {
		return distanceMeasure.getInstance();
	}
}
