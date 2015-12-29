package mayday.tiala.multi.gui.plots;

import static mayday.vis3.plots.profile.BreakSetting.BREAK_IGNORE;
import static mayday.vis3.plots.profile.BreakSetting.BREAK_START_LEFT;
import static mayday.vis3.plots.profile.BreakSetting.BREAK_START_LEFT_SHIFTED;
import static mayday.vis3.plots.profile.BreakSetting.BREAK_UNCONNECTED;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import mayday.core.Probe;
import mayday.core.meta.types.TimeseriesMIO;
import mayday.tiala.multi.data.mastertables.StatisticsMasterTable;
import mayday.vis3.plots.profile.ProfilePlotComponent;
import mayday.vis3.vis2base.DataSeries;
import mayday.vis3.vis2base.Shape;

/**
 * @author jaeger
 */
@SuppressWarnings("serial")
public class AutoTimepointProfilePlotComponent extends ProfilePlotComponent {
	
	protected boolean firststart = true;
	
	public void updatePlot() {
		if(settings != null) {
			// always remove old/stale time series information 
			if (settings.getTimepoints().useTimepoints() || firststart) {
				TimeseriesMIO.getGroupInstance(viewModel.getDataSet().getMIManager()).remove(viewModel.getDataSet());
				TimeseriesMIO tsm = TimeseriesMIO.getForDataSet(viewModel.getDataSet(), true, true);
				if (tsm != null && tsm.applicableTo(viewModel.getDataSet())) {
					experimentTimepoints = new double[tsm.getValue().size()];
					for (int i = 0; i != experimentTimepoints.length; ++i) {
							experimentTimepoints[i] = tsm.getValue().get(i);
					}
					settings.getTimepoints().setExperimentTimepoints(experimentTimepoints);
					settings.getTimepoints().setUseTimepoints(true);
				}
				firststart = false;
			}
			super.updatePlot();
		}
	}
	
	//override this method in order to draw only time-points that are contained in the alignment
	protected DataSeries view(Collection<Probe> pl) {
		DataSeries series = new DataSeries();
		TreeSet<Integer> breaks = settings.getBreakPositions();
		int break_type = settings.getBreakType();
		
		double yshift=break_type == BREAK_START_LEFT_SHIFTED ? 
				Math.ceil(viewModel.getMaximum(null, null)-viewModel.getMinimum(null, null)+1) : 0;
		
		if (settings.getShowDots().getBooleanValue()) {
			series.setShape(new Shape() {
				public void paint(Graphics2D g) {
					g.fillRect(-1,-1,3,3);
				}
				public boolean wantDeviceCoordinates() {
					return true;
				}
			});
		}

		applyChangedTimepointSettings();

		boolean useTimepoints = settings.getTimepoints().useTimepoints();
		List<Integer> expTPIndices = new ArrayList<Integer>();
		
		if(viewModel.getDataSet().getMasterTable() instanceof StatisticsMasterTable) {
			StatisticsMasterTable smt = (StatisticsMasterTable)viewModel.getDataSet().getMasterTable();
			List<Double> alignment = smt.getStore().getAlignment();
			//determine the experiment time points that should be shown
			for(int i = 0; i < experimentTimepoints.length; i++) {
				if(alignment.contains(experimentTimepoints[i])) {
					expTPIndices.add(i);
				}
			}
		} else {
			for(int i = 0; i < experimentTimepoints.length; i++) {
				expTPIndices.add(i);
			}
		}
		
		for(Probe p : pl) {
			int xmodifier = 0;
			double ymodifier = 0;
			double[] probeValues;
			
			if (p.isImplicitProbe()) {
				probeValues = p.getValues();
			} else {
				probeValues = viewModel.getProbeValues(p);
			}
			
			for(int j = 0; j < p.getNumberOfExperiments(); j++) {
				double xposition = j;

				if (useTimepoints) {
					if(expTPIndices.contains(j)) {
						xposition = experimentTimepoints[j];
					} else {
						continue;
					}
				}

				xposition += xmodifier;
				double theValue = probeValues[j];
				
				if (Double.isNaN(theValue)) {
					if (!settings.getInferData().getBooleanValue()) {
						series.jump();
					}
				} else {
					series.addPoint(xposition, theValue+ymodifier, p);
				}
				
				if (break_type!=BREAK_IGNORE && breaks.contains(j)) {
					switch (break_type) {
					case BREAK_START_LEFT_SHIFTED:
						ymodifier+=yshift;
					case BREAK_START_LEFT:
						xmodifier = (useTimepoints? -j : -(j+1));  // fall through
					case BREAK_UNCONNECTED:
						series.jump();
					}
				}
			}
			series.jump();
		}
		series.setConnected(true);
		return series;
	}
}
