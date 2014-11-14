package mayday.vis3.plots.chromogram.probelistsort;

import java.util.Comparator;

import mayday.core.Probe;

public class ProbeStatisticComparator implements Comparator<Probe>
{
	private ProbeComparisonMode mode;
	
	public ProbeStatisticComparator(ProbeComparisonMode mode) 
	{
		this.mode=mode;
	}
	
	public int compare(Probe o1, Probe o2) 
	{
		switch (mode) 
		{
		case MIN:
			return o1.getMinValue().compareTo(o2.getMinValue());
		case MAX:
			return o1.getMaxValue().compareTo(o2.getMaxValue());	
		case MEAN:
			return Double.compare(o1.getMean(),o2.getMean());
		case SD:
			return Double.compare(o1.getStandardDeviation(),o2.getStandardDeviation());
		case VAR:
			return Double.compare(o1.getVariance(),o2.getVariance());
		case NUMBER_OF_PROBELIST:
			return Double.compare(o1.getNumberOfProbeLists(),o2.getNumberOfProbeLists());
		default:
			return -1;
		}
	}
}
