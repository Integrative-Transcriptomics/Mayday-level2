package mayday.tiala.pairwise.gui.plots;

import mayday.core.meta.types.TimeseriesMIO;
import mayday.vis3.plots.profile.ProfilePlotComponent;

@SuppressWarnings("serial")
public class AutoTimepointProfilePlotComponent extends ProfilePlotComponent {
	
	protected boolean firststart=true;
	
	public AutoTimepointProfilePlotComponent() {
	}
	
	public void updatePlot() {
		// always remove old/stale time series information 
		if (settings.getTimepoints().useTimepoints() || firststart) {
			TimeseriesMIO.getGroupInstance(viewModel.getDataSet().getMIManager()).remove(viewModel.getDataSet());
			TimeseriesMIO tsm = TimeseriesMIO.getForDataSet(viewModel.getDataSet(), true, true);
			if (tsm!=null && tsm.applicableTo(viewModel.getDataSet())) {
				experimentTimepoints = new double[tsm.getValue().size()];
				for (int i=0; i!=experimentTimepoints.length; ++i) 
					experimentTimepoints[i] = tsm.getValue().get(i);
				settings.getTimepoints().setExperimentTimepoints(experimentTimepoints);
				settings.getTimepoints().setUseTimepoints(true);
			}			
			firststart = false;			
		}
		super.updatePlot();
	}
	

}
