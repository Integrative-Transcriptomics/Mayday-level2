package mayday.tiala.pairwise.data.mastertables;

import java.util.Collection;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.MasterTableEvent;
import mayday.core.Probe;
import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.data.AlignmentStoreEvent;
import mayday.tiala.pairwise.data.probes.StatisticsProbe;
import mayday.tiala.pairwise.statistics.ProbeCombinationStatistic;

public class StatisticsMasterTable extends AlignmentDerivedMasterTable {
	
	public StatisticsMasterTable(AlignmentStore Store, DataSet dataSet) {
		super(Store, dataSet);
	}
	
	protected void initProbes() {
		super.initProbes();
		refreshProbes(false);	
	}

	@SuppressWarnings("unchecked")
	protected void refreshProbes(boolean force) {
		if (store!=null) {
			ProbeCombinationStatistic statistic = store.getProbeStatistic();
			if (statistic!=null) {
				statistic.initStatistics(probes.size(), getAlignmentNumberOfExperiments());
				statistic.setInput((Collection)probes.values());
				int noe = statistic.getOutputDimension();				
				if (noe!=this.getNumberOfExperiments() || force)
					statistic.invalidateCurrent();
				this.setNumberOfExperiments(noe, true);
				List<String> nexpn = statistic.getOutputNames(getAlignmentExperimentNames());
				setExperimentNames(nexpn);
				statistic.applyStatistic();
			}			
		}
	}
	
	public void alignmentChanged(AlignmentStoreEvent evt) {		
		if (evt.getChange()==AlignmentStoreEvent.STATISTIC_CHANGED) {
			refreshProbes(false);
			fireMasterTableChanged(MasterTableEvent.OVERALL_CHANGE);
		} else {
			super.alignmentChanged(evt);
		}
	}

	protected void addDerivedProbes(String sourceName) {
		Probe pb = new StatisticsProbe(sourceName, this);
		probes.put(pb.getName(), pb);		
	}

	
	
}
