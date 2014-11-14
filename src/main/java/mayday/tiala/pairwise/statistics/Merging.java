package mayday.tiala.pairwise.statistics;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;


public class Merging extends AbstractCombinationStatistic {

	public double[] computeStatisticFromVectors(double[][] source) {
		double[] v = new double[source[0].length];
		for (int i=0; i!=v.length; ++i) {
			double d1 = source[0][i];
			double d2 = source[1][i];
			double d = d1; // we use d1 if it is not NaN
			if (Double.isNaN(d)) // if d1 is NaN we use d2
				d = d2; // maybe d2 is also NaN, we don't care
			else if (!Double.isNaN(d2)) 
				d = (d+d2)/2; // neither is NaN, we use the mean
			v[i] = d;
		}
		return v;
	}

	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(	
				getClass(), 
				"PAS.tentakelstat.merging", 
				null,
				MC, 
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Creates new probes by merging time points from two datasets",
				"Merge Time Points");				
	}
	
}
