package mayday.vis3.plots.chromogram.probelistsort;

import java.util.Comparator;

import mayday.core.Probe;

public class ProbeNameComparator implements Comparator<Probe>
{
	private ProbeComparisonMode mode;
	
	public ProbeNameComparator(ProbeComparisonMode mode) 
	{
		this.mode=mode;
	}
	
	public int compare(Probe o1, Probe o2) 
	{
		if(mode==ProbeComparisonMode.NAME)
			return o1.getName().compareTo(o2.getName());
		else
			return o1.getDisplayName().compareTo(o2.getDisplayName());
	}
	
	
}
