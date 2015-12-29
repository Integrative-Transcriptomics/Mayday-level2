package mayday.vis3.plots.chromogram.probelistsort;

import java.util.Comparator;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;

public class ProbeComparatorFactory
{
	public static Comparator<Probe> createProbeComparator(ProbeComparisonMode mode, Integer experiment, MIGroup group)
	{
		switch (mode) 
		{
		case DISPLAY_NAME:// fallthrough!
		case NAME:
			return new ProbeNameComparator(mode);		
		case EXPERIMENT_VALUE:
			if(experiment==null) throw new RuntimeException("No such experiment.");
			return new ProbeExperimentComparator(experiment);
		case MAX: // fallthrough!
		case MIN: // fallthrough!
		case MEAN: // fallthrough!
		case SD: // fallthrough!
		case VAR: // fallthrough!
		case NUMBER_OF_PROBELIST:	
			return new ProbeStatisticComparator(mode);
		case META_INFORMATION:
			if(group==null) throw new RuntimeException("No MI Group available.");
			return new ProbeMIOComparator(group);
		case MAXIMUM_EXPERIMENT:// fallthrough!
		case MINIMUM_EXPERIMENT:	
			return new ProbeExtremeExperimentComparator(mode);
		default:
			return new ProbeNameComparator(ProbeComparisonMode.NAME);
		}
	}
	
	
}
