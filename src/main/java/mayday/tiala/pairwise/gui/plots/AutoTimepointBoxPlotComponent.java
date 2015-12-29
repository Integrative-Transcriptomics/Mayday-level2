package mayday.tiala.pairwise.gui.plots;

import mayday.core.meta.types.TimeseriesMIO;
import mayday.vis3.plots.boxplot.BoxPlotComponent;

@SuppressWarnings("serial")
public class AutoTimepointBoxPlotComponent extends BoxPlotComponent {

	
//	public void updatePlot() {
//		// always remove old/stale time series information 
//		TimeseriesMIO.getGroupInstance(viewModel.getDataSet().getMIManager()).remove(viewModel.getDataSet());
//		TimeseriesMIO tsm = TimeseriesMIO.getForDataSet(viewModel.getDataSet(), true, true);
//		if (tsm!=null && tsm.applicableTo(viewModel.getDataSet())) {
//			experimentTimepoints = new double[tsm.getValue().size()];
//			for (int i=0; i!=experimentTimepoints.length; ++i) 
//				experimentTimepoints[i] = tsm.getValue().get(i);
//		}
//		timePoints.setExperimentTimepoints(experimentTimepoints);
//		timePoints.setUseTimepoints(true);
//		super.updatePlot();		
//	}
	
}
