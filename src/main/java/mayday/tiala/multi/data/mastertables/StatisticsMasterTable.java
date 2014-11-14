package mayday.tiala.multi.data.mastertables;

import java.util.Collection;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.MasterTableEvent;
import mayday.core.Probe;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;
import mayday.tiala.multi.data.probes.StatisticsProbe;
import mayday.tiala.multi.statistics.ProbeCombinationStatistic;

/**
 * @author jaeger
 *
 */
public class StatisticsMasterTable extends AlignmentDerivedMasterTable {
	
	/**
	 * @param ID
	 * @param Store
	 * @param dataSet
	 */
	public StatisticsMasterTable(int ID, AlignmentStore Store, DataSet dataSet) {
		super(ID, Store, dataSet);
	}
	
	protected void initProbes() {
		super.initProbes();
		refreshProbes(true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void refreshProbes(boolean force) {
		if (store!=null) {
			ProbeCombinationStatistic statistic = store.getProbeStatistic(ID);
			if (statistic != null) {
				statistic.initStatistics(probes.size(), getAlignmentNumberOfExperiments());
				statistic.setInput((Collection)probes.values());
				int noe = statistic.getOutputDimension();
				if (noe != this.getNumberOfExperiments() || force)
					statistic.invalidateCurrent();
				this.setNumberOfExperiments(noe, true);
				statistic.applyStatistic();
				List<String> nexpn = statistic.getOutputNames(getAlignmentExperimentNames());
				setExperimentNames(nexpn);
			}
		}
	}
	
	public void alignmentChanged(AlignmentStoreEvent evt) {		
		if (evt.getChange()==AlignmentStoreEvent.STATISTIC_CHANGED) {
			refreshProbes(true);
			fireMasterTableChanged(MasterTableEvent.OVERALL_CHANGE);
		} else {
			super.alignmentChanged(evt);
		}
	}

	protected void addDerivedProbes(String sourceName, int numOfProbes) {
		Probe pb = new StatisticsProbe(ID, sourceName, this);
		probes.put(pb.getName(), pb);
	}
	
	public int getNumberOfExperiments() {
		return this.getAlignmentNumberOfExperiments();
	}
}
