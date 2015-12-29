package mayday.expressionmapping.clustering.neuralgas;

import mayday.core.MasterTable;
import mayday.core.settings.typed.DoubleSetting;
import mayday.expressionmapping.clustering.PartitioningClusteringSetting;

/**
 * @author jaeger
 *
 */
public class NeuralGasSetting extends PartitioningClusteringSetting {

	private DoubleSetting ngNeighborPar;
	
	/**
	 * Constructor
	 * @param masterTable 
	 * @param numProbes 
	 */
	public NeuralGasSetting(MasterTable masterTable, int numProbes) {
		super("Neural Gas Setting", masterTable, numProbes);
		
		root.addSetting(ngNeighborPar = new DoubleSetting("Neural Gas Neighbor Parameter", null, 0.5));
	}
	
	/**
	 * @return neural gas neighbor parameter
	 */
	public double getNeighborParameter() {
		return this.ngNeighborPar.getDoubleValue();
	}
}
