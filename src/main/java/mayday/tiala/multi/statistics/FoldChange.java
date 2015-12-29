package mayday.tiala.multi.statistics;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * @author jaeger
 */
public class FoldChange extends AbstractCombinationStatistic {

	public double[] computeStatisticFromVectors(double[][] source) {
		double[] v = new double[source[0].length];
		for (int i=0; i!=v.length; ++i) {
			double d1 = source[0][i];
			double d2 = source[1][i];
			v[i] = d1-d2;
		}
		return v;
	}

	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(	
				getClass(), 
				"PAS.tentakelstat.foldchange2", 
				null,
				MC, 
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Computes the fold-change (difference) between the experiment values of two probes",
				"Fold-change");				
	}
}
