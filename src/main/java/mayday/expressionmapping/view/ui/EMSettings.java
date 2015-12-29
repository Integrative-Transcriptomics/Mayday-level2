package mayday.expressionmapping.view.ui;

import mayday.core.ClassSelectionModel;
import mayday.core.MasterTable;
import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.settings.Settings;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.settings.typed.ClassSelectionSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.expressionmapping.controller.Constants;

/**
 * @author jaeger
 *
 */
public class EMSettings extends Settings {
	/**
	 * Clustering algorithms
	 */
	public static String[] algorithms = new String[] { "<none>", "Neural Gas",
			"Weighted K-Means", "Simple K-Means" };
	/**
	 * Combining Methods
	 */
	public static String[] combiningMethods = new String[] { "Mean", "Median" };
	/**
	 * Barycentric Coordinates Computation Methods
	 */
	public static String[] computationMethods = new String[] { "Simple", "Fold Change", "Rank" };

	private ClassSelectionSetting classSelection;
	private RestrictedStringSetting combiningMethod;
	private RestrictedStringSetting computationMethod;
	private RestrictedStringSetting clusterAlgorithm;
	private DistanceMeasureSetting distanceMeasure;
	private IntSetting noClusters;
	private IntSetting noRounds;
	private DoubleSetting ngNeighborPar;

	/**
	 * @param masterTable
	 * @param numProbes
	 */
	public EMSettings(MasterTable masterTable, int numProbes) {
		super(new HierarchicalSetting("Expression Mapping")
				.setLayoutStyle(HierarchicalSetting.LayoutStyle.TABBED), null);

		HierarchicalSetting groups = new HierarchicalSetting("Group Selection");

		groups
				.addSetting(
						classSelection = new ClassSelectionSetting("-", null,
								new ClassSelectionModel(masterTable), 2, 4,
								masterTable.getDataSet())
								.setLayoutStyle(ClassSelectionSetting.LayoutStyle.FULL))
				.addSetting(
						combiningMethod = new RestrictedStringSetting(
								"Combining Method", null, 0, combiningMethods)
								.setLayoutStyle(RestrictedStringSetting.LayoutStyle.RADIOBUTTONS_HORIZONTAL))

				.addSetting(
						computationMethod = new RestrictedStringSetting(
								"Barycentric Coordinates", null, 0,
								computationMethods)
								.setLayoutStyle(RestrictedStringSetting.LayoutStyle.RADIOBUTTONS_HORIZONTAL));

		HierarchicalSetting clustering = new HierarchicalSetting("Clustering");

		clustering
				.addSetting(
						clusterAlgorithm = new RestrictedStringSetting(
								"Cluster Algorithm", null, 0, algorithms)
								.setLayoutStyle(RestrictedStringSetting.LayoutStyle.COMBOBOX))
				.addSetting(
						distanceMeasure = new DistanceMeasureSetting(
								"Distance Measure", null,
								DistanceMeasureManager.get("Euclidean")))
				.addSetting(
						noClusters = new IntSetting("# Clusters", null, 1, 1,
								numProbes, true, true)
								.setLayoutStyle(IntSetting.LayoutStyle.DEFAULT))
				.addSetting(
						noRounds = new IntSetting("max # Rounds", null, 100,
								10, 300, true, true)
								.setLayoutStyle(IntSetting.LayoutStyle.DEFAULT))
				.addSetting(
						ngNeighborPar = new DoubleSetting(
								"NG Neighbor Parameter", null, 0.5));

		root.addSetting(groups).addSetting(clustering);
	}

	/**
	 * @return number of clusters for clustering
	 */
	public int getNumberOfClusters() {
		return this.noClusters.getIntValue();
	}

	/**
	 * @return maximal number of rounds for clustering
	 */
	public int getMaxNumberOfRounds() {
		return this.noRounds.getIntValue();
	}

	/**
	 * @return neural gas parameter
	 */
	public double getNGNeighborParameter() {
		return this.ngNeighborPar.getDoubleValue();
	}

	/**
	 * @return Barycentric Coordinates computation method
	 */
	public int getBarycentricCoordsCompMethod() {
		return this.computationMethod.getSelectedIndex();
	}

	/**
	 * @return combining method
	 */
	public int getCombiningMethod() {
		switch(this.combiningMethod.getSelectedIndex()) {
		case 0:
			return Constants.COMBINE_MEAN;
		case 1:
			return Constants.COMBINE_MEDIAN;
		default:
			return Constants.COMBINE_MEAN;
		}
	}

	/**
	 * @return chosen clustering algorithm
	 */
	public int getClusteringAlgorithm() {
		switch (this.clusterAlgorithm.getSelectedIndex()) {
		case 1:
			return Constants.NG;
		case 2: 
			return Constants.WKMEANS;
		case 3:
			return Constants.KMEANS;
		default:
			return 0;
		}
	}

	/**
	 * @return distance measure for clustering
	 */
	public DistanceMeasurePlugin getDistanceMeasure() {
		return this.distanceMeasure.getInstance();
	}
	
	/**
	 * @return class selection model
	 */
	public ClassSelectionModel getClassSelectionModel() {
		return this.classSelection.getModel();
	}
}
