package mayday.vis3.plots.chromogram.probelistsort;

public enum ProbeComparisonMode 
{
	NAME,
	DISPLAY_NAME,	
	NUMBER_OF_PROBELIST,
	EXPERIMENT_VALUE,
	MEAN, MIN, MAX, SD, VAR,
	META_INFORMATION,
	MAXIMUM_EXPERIMENT,
	MINIMUM_EXPERIMENT;
	
	public static final String[] NAMES={"Probe Name", "Probe Display Name", "Number of Probe List","Experiment Value",
		"Minimum","Mean","Maximum", "Standard Deviation","Variance", "Meta Information","Experiment with maximum","Experiment with minimum"};
	
	public static ProbeComparisonMode forString(String s)
	{
		if(s.equals(NAMES[0]))	return ProbeComparisonMode.NAME;
		if(s.equals(NAMES[1]))	return ProbeComparisonMode.DISPLAY_NAME;
		if(s.equals(NAMES[2]))	return ProbeComparisonMode.NUMBER_OF_PROBELIST;
		if(s.equals(NAMES[3]))	return ProbeComparisonMode.EXPERIMENT_VALUE;
		if(s.equals(NAMES[4]))	return ProbeComparisonMode.MIN;
		if(s.equals(NAMES[5]))	return ProbeComparisonMode.MEAN;
		if(s.equals(NAMES[6]))	return ProbeComparisonMode.MAX;
		if(s.equals(NAMES[7]))	return ProbeComparisonMode.SD;
		if(s.equals(NAMES[8]))	return ProbeComparisonMode.VAR;
		if(s.equals(NAMES[9]))	return ProbeComparisonMode.META_INFORMATION;
		if(s.equals(NAMES[10]))	return ProbeComparisonMode.MAXIMUM_EXPERIMENT;
		if(s.equals(NAMES[11]))	return ProbeComparisonMode.MINIMUM_EXPERIMENT;
			
		return NAME;
	}
}
