package mayday.tiala.multi.gui.plots.multiprobemultiprofileplot3d;

import mayday.core.meta.types.TimeseriesMIO;
import mayday.tiala.multi.data.AlignmentStore;

/**
 * @author jaeger
 */
public class AutoTimepointMultiProfileplot3DComponent extends MultiProbeMultiProfileplot3DPanel {

	/**
	 * @param store
	 */
	public AutoTimepointMultiProfileplot3DComponent(AlignmentStore store) {
		super(store);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3363629671470121479L;
	
	protected boolean firststart = true;
	
	@Override
	public void updatePlot() {
		// always remove old/stale time series information
		if (settings.getTimepointSetting().useTimepoints() || firststart) {
			TimeseriesMIO.getGroupInstance(viewModel.getDataSet().getMIManager()).remove(viewModel.getDataSet());
			TimeseriesMIO tsm = TimeseriesMIO.getForDataSet(viewModel.getDataSet(), true, true);
			if (tsm!=null && tsm.applicableTo(viewModel.getDataSet())) {
				double[] experimentTimepoints = new double[tsm.getValue().size()];
				for (int i=0; i!=experimentTimepoints.length; ++i) 
					experimentTimepoints[i] = tsm.getValue().get(i);
				settings.getTimepointSetting().setExperimentTimepoints(experimentTimepoints);
				settings.getTimepointSetting().setUseTimepoints(true);
			}			
			firststart = false;			
		}
		super.updatePlot();
	}
}
