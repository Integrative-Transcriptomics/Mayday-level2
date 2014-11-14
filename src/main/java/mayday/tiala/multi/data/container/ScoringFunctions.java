package mayday.tiala.multi.data.container;

import java.util.ArrayList;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.math.distance.measures.PearsonCorrelationDistance;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class ScoringFunctions extends ArrayList<DistanceMeasurePlugin> {

	/**
	 * @param size
	 */
	public void initialize(int size) {
		for(int i = 0; i < size; i++)
			this.add(new PearsonCorrelationDistance());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.ArrayList#get(int)
	 * reduce the index by 1 because we here look at the pairs not at the data set itself
	 * there are n-1 pairs for n data sets
	 * obviously there is no scoring function available for the reference data set!
	 */
	public DistanceMeasurePlugin get(int datasetID) {
		if(datasetID <= 0) throw new RuntimeException("No scoring function available for this data set combination!!");
		return super.get(datasetID - 1);
	}
}
