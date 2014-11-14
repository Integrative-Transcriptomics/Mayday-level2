package mayday.vis3.plots.chromogram.probelistsort;

import java.util.Comparator;

import mayday.core.Probe;
import mayday.vis3.model.ViewModel;

public class ProbeExtremeExperimentComparator implements Comparator<Probe>
{
	public ProbeComparisonMode mode;
	private ViewModel viewModel;
	
	public ProbeExtremeExperimentComparator(ProbeComparisonMode mode) 
	{
		this.mode=mode;
	}
	
	public int compare(Probe o1, Probe o2) 
	{
		int e1=-1;
		int e2=-1;
		switch (mode) 
		{
		case MAXIMUM_EXPERIMENT:
			e1=whichMax(o1,viewModel);
			e2=whichMax(o2,viewModel);
			break;
		case MINIMUM_EXPERIMENT:
			e1=whichMax(o1,viewModel);
			e2=whichMax(o2,viewModel);
			break;
		default:
			break;
		}
		if(e1 < e2) return -1;
		if(e1==e2) return 0;
		return 1;
	}
	
	public int whichMax(Probe p)
	{
		double m=Double.MIN_VALUE;
		int mi=-1;
		for(int i=0; i!=p.getNumberOfExperiments(); ++i)
		{
			if(p.getValue(i) > m)
			{
				m=p.getValue(i);
				mi=i;
			}
		}
		return mi;
	}
	
	public int whichMax(Probe p, ViewModel viewModel)
	{
		if(viewModel==null)
			return whichMax(p);
		
		double m=Double.MIN_VALUE;
		int mi=-1;
		double[] pd=viewModel.getDataManipulator().getProbeValues(p);
		for(int i=0; i!=pd.length; ++i)
		{
			if(pd[i] > m)
			{
				m=pd[i];
				mi=i;
			}
		}
		return mi;
	}
	
	public int whichMin(Probe p, ViewModel viewModel)
	{
		if(viewModel==null)
			return whichMin(p);
		
		double m=Double.MAX_VALUE;
		int mi=-1;
		double[] pd=viewModel.getDataManipulator().getProbeValues(p);
		for(int i=0; i!=pd.length; ++i)
		{
			if(pd[i] < m)
			{
				m=pd[i];
				mi=i;
			}
		}
		return mi;
	}
	
	public int whichMin(Probe p)
	{
		double m=Double.MAX_VALUE;
		int mi=-1;
		for(int i=0; i!=p.getNumberOfExperiments(); ++i)
		{
			if(p.getValue(i) < m)
			{
				m=p.getValue(i);
				mi=i;
			}
		}
		return mi;
	}

	public void setViewModel(ViewModel viewModel) {
		this.viewModel = viewModel;
	}
	
	
}
