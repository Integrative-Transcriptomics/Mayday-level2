package mayday.expressionmapping.clustering.weightedkmeans;

import mayday.core.MasterTable;
import mayday.expressionmapping.clustering.PartitioningClusteringSetting;

/**
 * @author jaeger
 *
 */
public class WeightedKMeansSetting extends PartitioningClusteringSetting {

	/**
	 * @param name
	 * @param masterTable
	 * @param numProbes
	 */
	public WeightedKMeansSetting(MasterTable masterTable,
			int numProbes) {
		super("Weighted K-Means Setting", masterTable, numProbes);
	}
}
