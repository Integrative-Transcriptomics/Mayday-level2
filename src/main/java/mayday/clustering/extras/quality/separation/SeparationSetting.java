package mayday.clustering.extras.quality.separation;

import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

/**
 * 
 * @author jaeger
 *
 */
public class SeparationSetting extends HierarchicalSetting {
	
	public static final int SINGLE = 0;
	public static final int COMPLETE = 1;
	public static final int AVERAGE = 2;
	
	public static final int CENTROID_MEAN = 0;
	public static final int CENTROID_MEDIAN = 1;

	private RestrictedStringSetting linkage;
	private DistanceMeasureSetting distanceMeasureSetting;
	private RestrictedStringSetting centroidMethod;
	
	public SeparationSetting() {
		super("Separation Setting");

		linkage = new RestrictedStringSetting("Linkage Method", null, 3, "Single", "Complete","Average");
		centroidMethod = new RestrictedStringSetting("Centroid Method", null, 0, "Mean", "Median");
		distanceMeasureSetting = new DistanceMeasureSetting("Distance Measure", "The distance measure to be used for the calculation of the separation value.\nThis should be the same as for the preliminary clustering", DistanceMeasureManager.get("Euclidean"));
		
		this.addSetting(linkage);
		this.addSetting(centroidMethod);
		this.addSetting(distanceMeasureSetting);
	}
	
	public SeparationSetting clone() {
		return (SeparationSetting)this.reflectiveClone();
	}
	
	public int getLinkageMethod() {
		return linkage.getSelectedIndex();
	}
	
	public DistanceMeasurePlugin getDistanceMeasure() {
		return distanceMeasureSetting.getInstance();
	}
	
	public int getCentroidMethod() {
		return this.centroidMethod.getSelectedIndex();
	}
}
