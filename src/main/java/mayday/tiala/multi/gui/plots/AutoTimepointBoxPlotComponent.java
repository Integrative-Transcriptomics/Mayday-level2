package mayday.tiala.multi.gui.plots;

import mayday.vis3.plots.boxplot.BoxPlotComponent;

/**
 * @author jaeger
 */
@SuppressWarnings("serial")
public class AutoTimepointBoxPlotComponent extends BoxPlotComponent {

	public void updatePlot() {
		// always remove old/stale time series information 
//		TimeseriesMIO.getGroupInstance(viewModel.getDataSet().getMIManager()).remove(viewModel.getDataSet());
//		TimeseriesMIO tsm = TimeseriesMIO.getForDataSet(viewModel.getDataSet(), true, true);
//		if (tsm!=null && tsm.applicableTo(viewModel.getDataSet())) {
//			experimentTimepoints = new double[tsm.getValue().size()];
//			for (int i=0; i!=experimentTimepoints.length; ++i) 
//				experimentTimepoints[i] = tsm.getValue().get(i);
//			timePoints.setExperimentTimepoints(experimentTimepoints);
//			timePoints.setUseTimepoints(true);
//		}
		super.updatePlot();	
	}
	
	//overrride this method to draw only time-points that are contained in the alignment
//	protected DataSeries viewProfile(Collection<Probe> pl) {
//		DataSeries series = new DataSeries();
//		series.setShape(new Shape() {
//			public void paint(Graphics2D g) {				
//				g.fillRect(-1,-1,3,3);
//			}
//			public boolean wantDeviceCoordinates() {
//				return true;
//			}
//		});	
//		
//		boolean useTimepoints = timePoints.useTimepoints();
//		List<Integer> expTPIndices = this.isStatsticsPlot();
//		
//		for(Probe p : pl) {	
//			for (int k=0; k!=viewModel.getProbeLists(false).size(); ++k) {
//				ProbeList plist = viewModel.getProbeLists(false).get(k);
//				
//				if (plist.contains(p)) {
//					double[] vals = viewModel.getProbeValues(p);
//					
//					for(int j = 0; j < p.getNumberOfExperiments(); j++) {
//						int x = k*vals.length+j;
//						double plotx;
//						
//						if (drawProbeListsOnTop.getBooleanValue()) {
//							plotx = j;
//						} else {
//							if (useTimepoints) {
//								if(expTPIndices.contains(j)) {
//									plotx = experimentTimepoints[j];
//								} else {
//									continue;
//								}
//							} else {
//								plotx = x;
//							}
//						}
//						
//						Double v = vals[j];
//						
//						if (!Double.isNaN(v)) {
//							series.addPoint(plotx, v, p);
//						} else {
//							series.jump();
//						}
//					}
//					series.jump();					
//				}
//			}
//		}
//		series.setConnected(true);
//		return series;
//	}
	
	//override this class in order to draw only time-points that are contained in the alignment
//	public void createView() {
//		int experiments = viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
//		int probelists = viewModel.getProbeLists(false).size();
//		DataSeries series_arr[] = new DataSeries[probelists*experiments];
//
//		double maxY = 0;
//		double minX = Double.POSITIVE_INFINITY;
//		double maxX = Double.NEGATIVE_INFINITY;		
//
//		int h=0;
//		
//		boolean useTimepoints = timePoints.useTimepoints();
//		List<Integer> expTPIndices = this.isStatsticsPlot();
//		
//		for(ProbeList pl : viewModel.getProbeLists(false)) {
//			ProbeList.Statistics plStat = viewModel.getStatistics(pl.getAllProbes()); 
//			// create quartiles for this probe list
//			for(int i = 0; i < experiments; i++) {
//				int x = h * experiments + i;
//				double plotx;
//				
//				if (drawProbeListsOnTop.getBooleanValue()) {
//					plotx = i;
//				} else {
//					if (useTimepoints) {
//						if(expTPIndices.contains(i)) {
//							plotx = experimentTimepoints[i];
//						} else {
//							continue;
//						}
//					} else {
//						plotx = x;
//					}	
//				}
//				
//				minX = Math.min(plotx, minX);
//				maxX = Math.max(plotx, maxX);
//				
//				series_arr[x] = new DataSeries();				
//				
//				double[] quartiles = new double[5];
//				
//				if (pl.getNumberOfProbes()>0) {
//					quartiles[4] = viewModel.getMaximum(i, pl.getAllProbes());
//					quartiles[3] = plStat.getQ1().getValues()[i];
//					quartiles[2] = plStat.getMedian().getValues()[i];
//					quartiles[1] = plStat.getQ3().getValues()[i];
//					quartiles[0] = viewModel.getMinimum(i, pl.getAllProbes());					
//				}
//				
//				if (Double.isNaN(quartiles[0]) || Double.isNaN(quartiles[1]) || Double.isNaN(quartiles[2]) ||
//						Double.isNaN(quartiles[3]) || Double.isNaN(quartiles[4])) {
//					// nothing to paint
//				} else {				
//					series_arr[x].setShape(new BoxShape(quartiles, pl));	
//					series_arr[x].addPoint(plotx, quartiles[0], null);
//					series_arr[x].setConnected(true);
//					addDataSeries(series_arr[x]);
//					maxY = Math.max(maxY, quartiles[4]);
//				}								
//			}
//			h++;
//		}
//		
//		// draw these invisible points to get a good grid
//		DataSeries beauty = new DataSeries();
//		beauty.addPoint(minX-.5, maxY+.1, null);
//		beauty.addPoint(maxX+.5, maxY+.1, null);
//		beauty.setColor(Color.WHITE);
//		beauty.setConnected(true);			
//		addDataSeries(beauty);
//		
//		setScalingUnitX(1.0);
//		select(Color.RED);
//	}
	
	/*
	 * this class creates indices of the time-points that should be used for display
	 */
//	private List<Integer> isStatsticsPlot() {
//		List<Integer> expTPIndices = new ArrayList<Integer>();
//		
//		//check whether this plot is used to display alignment statistics
//		if(viewModel.getDataSet().getMasterTable() instanceof StatisticsMasterTable) {
//			StatisticsMasterTable smt = (StatisticsMasterTable)viewModel.getDataSet().getMasterTable();
//			List<Double> alignment = smt.getStore().getAlignment();
//			//determine the experiment time points that should be shown 
//			for(int i = 0; i < experimentTimepoints.length; i++) {
//				if(alignment.contains(experimentTimepoints[i])) {
//					expTPIndices.add(i);
//				}
//			}
//		} else {
//			for(int i = 0; i < experimentTimepoints.length; i++) {
//				expTPIndices.add(i);
//			}
//		}
//		
//		return expTPIndices;
//	}
}
