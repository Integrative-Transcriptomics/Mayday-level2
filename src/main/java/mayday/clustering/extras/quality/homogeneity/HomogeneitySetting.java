package mayday.clustering.extras.quality.homogeneity;

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
public class HomogeneitySetting extends HierarchicalSetting {
	
	public static final int HOMOGENEITY_MEAN = 0;
	public static final int HOMOGENEITY_MAX = 1;
	
	public static final int CENTROID_MEAN = 0;
	public static final int CENTROID_MEDIAN = 1;

	private RestrictedStringSetting method;
	private DistanceMeasureSetting distance;
	private RestrictedStringSetting centroidMethod;
	
	public HomogeneitySetting() {
		super("Homogeneity Setting");
		
		method = new RestrictedStringSetting("Homogeneity Method", "Homogeneity calculation method", 0, "Mean", "Max");
		centroidMethod = new RestrictedStringSetting("Centroid Method", "The centroid calculation method", 0, "Mean", "Median");
		distance = new DistanceMeasureSetting("Distance Measure", null, DistanceMeasureManager.get("Euclidean"));
		
		this.addSetting(method);
		this.addSetting(centroidMethod);
		this.addSetting(distance);
	}
	
	public HomogeneitySetting clone() {
		return (HomogeneitySetting)this.reflectiveClone();
	}
	
	public int getHomogeneityMethod() {
		return method.getSelectedIndex();
	}
	
	public DistanceMeasurePlugin getDistanceMeasure() {
		return distance.getInstance();
	}
	
	public int getCentroidMethod() {
		return this.centroidMethod.getSelectedIndex();
	}
}
