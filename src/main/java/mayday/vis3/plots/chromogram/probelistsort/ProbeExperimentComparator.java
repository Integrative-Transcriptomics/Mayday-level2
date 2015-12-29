package mayday.vis3.plots.chromogram.probelistsort;

import java.util.Comparator;

import mayday.core.Probe;
import mayday.vis3.model.ViewModel;

public class ProbeExperimentComparator implements Comparator<Probe>
{
	private int experiment;
	private ViewModel viewModel;
	
	public ProbeExperimentComparator(int exp) 
	{
		experiment=exp;
	}
	
	public int compare(Probe o1, Probe o2)
	{
		if(viewModel!=null)
			return (int)(viewModel.getDataManipulator().getProbeValues(o2)[experiment] -  viewModel.getDataManipulator().getProbeValues(o1)[experiment]);
		return o1.getValue(experiment).compareTo(o2.getValue(experiment));
	}

	public void setViewModel(ViewModel viewModel) {
		this.viewModel = viewModel;
	}
	
	
}
