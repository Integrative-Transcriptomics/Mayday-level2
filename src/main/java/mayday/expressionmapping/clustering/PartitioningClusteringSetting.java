package mayday.expressionmapping.clustering;

import mayday.core.MasterTable;
import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.settings.Settings;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.settings.typed.IntSetting;

/**
 * @author jaeger
 *
 */
public class PartitioningClusteringSetting extends Settings {

	//private ClassSelectionSetting classSelection;
	private DistanceMeasureSetting distanceMeasure;
	private IntSetting maxRounds;
	private IntSetting numClusters;
	
	private MasterTable masterTable;
	
	/**
	 * Constructor
	 * @param name 
	 * @param masterTable 
	 * @param numProbes 
	 */
	public PartitioningClusteringSetting(String name, MasterTable masterTable, int numProbes) {
		super(new HierarchicalSetting(name)
		.setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL), null);
		
		this.masterTable = masterTable;
//		int numExp = masterTable.getNumberOfExperiments();
		
		root
//		.addSetting(classSelection = new ClassSelectionSetting("-", null,
//								new ClassSelectionModel(masterTable), 2, numExp,
//								masterTable.getDataSet())
//								.setLayoutStyle(ClassSelectionSetting.LayoutStyle.FULL))
		.addSetting(distanceMeasure = new DistanceMeasureSetting("Distance Measure", null, DistanceMeasureManager.get("Euclidean")))
		.addSetting(maxRounds = new IntSetting("Maximal number of Rounds", null, 100,
								10, 300, true, true))
		.addSetting(numClusters = new IntSetting("Number of Clusters", null, 9, 1, numProbes, true, true));
	}
	
//	/**
//	 * @return class selection model
//	 */
//	public ClassSelectionModel getClassSelection() {
//		return this.classSelection.getModel();
//	}
	
	/**
	 * @return distance measure
	 */
	public DistanceMeasurePlugin getDistanceMeasure() {
		return distanceMeasure.getInstance();
	}
	
	
	/**
	 * @return maximum number of rounds
	 */
	public int getMaxNumOfRounds() {
		return this.maxRounds.getIntValue();
	}
	
	/**
	 * @return number of clusters
	 */
	public int getNumOfClusters() {
		return this.numClusters.getIntValue();
	}
	
	/**
	 * @return dimension of the data (2 - 4 is possible)
	 */
	public int getDimension() {
		if(masterTable != null) {
			return masterTable.getNumberOfExperiments();
		}
		return 0;
	}
	
	/**
	 * @return master table
	 */
	public MasterTable getMasterTable() {
		return this.masterTable;
	}
}
