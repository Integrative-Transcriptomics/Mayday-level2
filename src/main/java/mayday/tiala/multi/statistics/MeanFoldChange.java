package mayday.tiala.multi.statistics;

import java.util.LinkedList;
import java.util.List;

import mayday.core.math.Statistics;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * @author jaeger
 */
public class MeanFoldChange extends AbstractCombinationStatistic {

	public double[] computeStatisticFromVectors(double[][] source) {
		double[] v = new double[source[0].length];
		for (int i=0; i!=v.length; ++i) {
			double d1 = source[0][i];
			double d2 = source[1][i];
			v[i] = d1-d2;
		}
		double m = Statistics.mean(v);
		return new double[]{m};
	}

	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(	
				getClass(), 
				"PAS.tentakelstat.meanfc2", 
				null,
				MC, 
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Computes the mean fold-change (difference) between the experiment values of two probes",
				"Mean Fold Change");				
	}

	public int getOutputDimension() {
		return 1;
	}
	
	public List<String> getOutputNames(List<String> inputNames) {
		LinkedList<String> l = new LinkedList<String>();
		l.add("Mean Fold Change");
		return l;
	}
}
