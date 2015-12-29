package mayday.clustering.extras.quality.dunnindex;

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
public class DunnIndexSetting extends HierarchicalSetting{
	
	public static final int DIAMETER_MAXIMUM = 0;
	public static final int DIAMETER_MEAN = 1;
	public static final int DIAMETER_CENTROID = 2;
	
	public static final int LINKAGE_SINGLE = 0;
	public static final int LINKAGE_COMPLETE = 1;
	public static final int LINKAGE_AVERAGE = 2;

	private RestrictedStringSetting diameterMethod;
	private RestrictedStringSetting linkageMethod;
	private DistanceMeasureSetting distanceMeasure;
	
	public DunnIndexSetting() {
		super("dunn index");

		diameterMethod = new RestrictedStringSetting("Cluster Diameter Method", "Chose the method for calculating the cluster diameter\n" +
				"Max: Maximum distance between any two pairs of data points in the cluster\n" +
				"Mean: Mean distance between all pairs of data points in the cluster\n" +
				"Centroid: Average distance of all data points to the cluster center", 2, "Maximum", "Mean", "Centroid");
		linkageMethod = new RestrictedStringSetting("Linkage Method", null, 2, "Single", "Complete", "Average");
		distanceMeasure = new DistanceMeasureSetting("Distance Measure", "The distance measure that is used for the calculation of the Dunn Index.\n This should be the same as for the preliminary clustering", DistanceMeasureManager.get("Euclidean"));
		
		this.addSetting(diameterMethod);
		this.addSetting(linkageMethod);
		this.addSetting(distanceMeasure);
	}
	
	public DunnIndexSetting clone() {
		return (DunnIndexSetting)this.reflectiveClone();
	}
	
	public int getClusterDiameterMethod() {
		return diameterMethod.getSelectedIndex();
	}
	
	public int getLinkageMethod() {
		return linkageMethod.getSelectedIndex();
	}
	
	public DistanceMeasurePlugin getDistanceMeasure() {
		return distanceMeasure.getInstance();
	}
}
