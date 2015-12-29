package mayday.tiala.multi.statistics;

import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Settings;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.tiala.multi.data.AlignedDataSets;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.container.TimeShifts;
import mayday.tiala.multi.data.container.TimepointDataSets;
import mayday.tiala.multi.data.probes.StatisticsProbe;

/**
 * @author jaeger
 */
public class OptimalShift extends AbstractCombinationStatistic implements SettingChangeListener {

	protected DistanceMeasureSetting distanceSetting;
	protected DistanceMeasurePlugin distance;
	
	protected LinkedList<Integer[]> ind1 = new LinkedList<Integer[]>();
	protected LinkedList<Integer[]> ind2 = new LinkedList<Integer[]>();
	protected LinkedList<Double> shift = new LinkedList<Double>();
	
	protected int datasetID = 1;
		
	public void applyStatistic() {
		initDistance(); 
		distance = distanceSetting.getInstance();
		super.applyStatistic();
	}
	
	protected void initDistance() {
		if (distanceSetting==null)
			distanceSetting = new DistanceMeasureSetting("Distance measure", null, DistanceMeasureManager.get("Pearson Correlation"));
	}
	
	public void setStore(AlignmentStore Store, final int id) {
		this.datasetID = id;
		if (store!=Store) {
			super.setStore(Store, id);
			ind1.clear();
			ind2.clear();
			shift.clear();
			if (store!=null) {
				SwingUtilities.invokeLater(new Runnable(){
					public void run() {
						for (Double shiftTmp : store.getPossibleAlignments().getAlignments(id).everything()) {
//							TimepointDataSet[] tpds = {store.get(0), store.get(id + 1)};
							TimepointDataSets tpds = new TimepointDataSets(2);
							tpds.add(store.get(0));
							tpds.add(store.get(id + 1));
							AlignedDataSets ads = new AlignedDataSets(store, tpds, new TimeShifts(new Double[]{shiftTmp}));
							if(store.getSettings().showOnlyMatching()) {
								ind1.add(AlignedDataSets.firstIndices(ads.getMatchingAll().get(0)));
								ind2.add(AlignedDataSets.secondIndices(ads.getMatchingAll().get(id)));
							} else {
								ind1.add(AlignedDataSets.firstIndices(ads.getMappingAll().get(0)));
								ind2.add(AlignedDataSets.secondIndices(ads.getMappingAll().get(id)));
							}					
							shift.add(ads.getTimeShifts().get(0));
						}
					}
				});
			}
		}
	}
	
	public double[] computeStatisticForProbe(StatisticsProbe p) {
		double bestDist = Double.MAX_VALUE;
		double bestShift = 0;
		for (int i = 0; i != shift.size(); ++i) {
			double[] v1 = p.getMappedSourceValues(0, ind1.get(i));
			double[] v2 = p.getMappedSourceValues(datasetID, ind2.get(i));
			double newDist = distance.getDistance(v1, v2);
			if (newDist<bestDist) {
				bestDist = newDist;
				bestShift = shift.get(i);
			}
		}
		return new double[]{bestShift};
	}

	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(	
				getClass(), 
				"PAS.tentakelstat.optimalshift2", 
				null,
				MC, 
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Computes the optimal time shift between the experiment values of two probes (based on a distance measure)",
				"Optimal Time Shift");
	}

	public Settings getSettings() {
		if (settings==null) {
			initDistance();
			HierarchicalSetting root = new HierarchicalSetting("Optimal Shift")
			.addSetting(distanceSetting);
			root.addChangeListener(this);
			settings = new Settings(root, null);
		}
		return settings;
	}
	
	public int getOutputDimension() {
		return 1;
	}

	public List<String> getOutputNames(List<String> inputNames) {
		LinkedList<String> lls = new LinkedList<String>();
		lls.add("Optimal Shift");
		return lls;
	}

	public double[] computeStatisticFromVectors(double[][] source) {
		throw new RuntimeException("You're doing it wrong");
	}

	public void stateChanged(SettingChangeEvent e) {
		invalidateCurrent();
		store.statisticChanged();
	}
}
