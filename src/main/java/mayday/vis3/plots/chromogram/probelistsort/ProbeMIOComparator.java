package mayday.vis3.plots.chromogram.probelistsort;

import java.util.Comparator;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;

public class ProbeMIOComparator implements Comparator<Probe>
{
	private MIGroup group;
	
	public ProbeMIOComparator(MIGroup group)
	{
		this.group=group;
	}
	
	@SuppressWarnings("unchecked")
	public int compare(Probe o1, Probe o2) 
	{
		try{
		return ((Comparable)group.getMIO(o1)).compareTo((Comparable)group.getMIO(o2));
		}catch(Exception e)
		{
			if(!group.contains(o1))
				return -1;
			if(!group.contains(o2))
				return 1;
			return 0;
		}
	}
	
}
