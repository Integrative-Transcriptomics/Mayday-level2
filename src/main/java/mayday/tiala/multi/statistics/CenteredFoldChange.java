package mayday.tiala.multi.statistics;

import mayday.core.math.Statistics;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * @author jaeger
 */
public class CenteredFoldChange extends AbstractCombinationStatistic {

	public double[] computeStatisticFromVectors(double[][] source) {
		double[] v = new double[source[0].length];
		double m1 = Statistics.mean(source[0], true);
		double m2 = Statistics.mean(source[1], true);
		for (int i=0; i!=v.length; ++i) {
			double d1 = source[0][i]-m1;
			double d2 = source[1][i]-m2;
			v[i] = d1-d2;
		}
		return v;
	}

	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(	
				getClass(), 
				"PAS.tentakelstat.centeredfoldchange2", 
				null,
				MC, 
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Computes the fold-change (difference) between the CENTERED experiment values of two probes",
				"Centered Fold-change");				
	}
}
