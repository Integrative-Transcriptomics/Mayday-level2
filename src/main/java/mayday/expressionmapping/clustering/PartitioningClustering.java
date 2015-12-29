package mayday.expressionmapping.clustering;

import mayday.clustering.ClusterAlgorithms;
import mayday.core.structures.linalg.matrix.PermutableMatrix;

/**
 * @author jaeger
 *
 */
public abstract class PartitioningClustering extends ClusterAlgorithms {

	protected PartitioningClusteringSetting settings;
	
	
	/**
	 * @param Data
	 * @param settings 
	 */
	public PartitioningClustering(PermutableMatrix Data, PartitioningClusteringSetting settings) {
		super(Data);
		this.settings = settings;
	}

	/**
	 * @param probeLists
	 * @param settings
	 * @return clusters
	 */
	public abstract int[] cluster();

	@Override
	public int[] runClustering() {
		return cluster();
	}
}
