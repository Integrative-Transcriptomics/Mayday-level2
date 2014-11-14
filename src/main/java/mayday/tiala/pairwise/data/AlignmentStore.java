package mayday.tiala.pairwise.data;

import java.util.HashSet;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.DataSetEvent;
import mayday.core.DataSetListener;
import mayday.core.EventFirer;
import mayday.core.MasterTable;
import mayday.core.MasterTableEvent;
import mayday.core.MasterTableListener;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.math.distance.measures.PearsonCorrelationDistance;
import mayday.core.probelistmanager.ProbeListManagerEvent;
import mayday.tiala.pairwise.data.mastertables.DerivedMasterTable;
import mayday.tiala.pairwise.data.mastertables.PairedProbesMasterTable;
import mayday.tiala.pairwise.data.mastertables.StatisticsMasterTable;
import mayday.tiala.pairwise.data.viewmodel.EnrichedNonModifyingViewModel;
import mayday.tiala.pairwise.data.viewmodel.EnrichedViewModel;
import mayday.tiala.pairwise.data.viewmodel.NonClosingVisualizer;
import mayday.tiala.pairwise.data.viewmodel.ViewModelLinker;
import mayday.tiala.pairwise.statistics.FoldChange;
import mayday.tiala.pairwise.statistics.ProbeCombinationStatistic;
import mayday.tiala.pairwise.suggestion.AlignmentSearchTask;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.Visualizer;

/**
 * Contains everything related to the Alignment GUI, in a way the data model behind everything
 * @author battke
 *
 */
public class AlignmentStore implements MasterTableListener, DataSetListener {
	
	protected AlignedDataSets alignedDataSets;	
	protected AlignmentSearchTask possibleAlignments;	
	protected DistanceMeasurePlugin scoringFunction;	
	protected ProbeCombinationStatistic statistic; 	
	protected boolean showOnlyMatching = true;
	protected boolean scoringForAll = true;
	
	protected NonClosingVisualizer[] visualizers = new NonClosingVisualizer[4];
	protected ViewModelLinker linker;
	

	public void initialize(DataSet d1, DataSet d2) {
		alignedDataSets = new AlignedDataSets(this, new TimepointDataSet(d1), new TimepointDataSet(d2), 0);
		scoringFunction = new PearsonCorrelationDistance();
		statistic = new FoldChange();
		statistic.setStore(this);
		initViewModels(d1, d2);
		possibleAlignments = new AlignmentSearchTask(getOne(), getTwo());
		possibleAlignments.start();	
		d1.addDataSetListener(this);
		d2.addDataSetListener(this);
	}

	public TimepointDataSet getOne() {
		return alignedDataSets.getFirst();
	}

	public TimepointDataSet getTwo() {
		return alignedDataSets.getSecond();
	}

	public AlignedDataSets getAlignedDataSets() {
		return alignedDataSets;
	}

	public AlignmentSearchTask getPossibleAlignments() {
		possibleAlignments.waitFor();
		return possibleAlignments;
	}
	
	public DistanceMeasurePlugin getScoringFunction() {
		return scoringFunction;
	}
	
	public void setScoringFunction(DistanceMeasurePlugin scoringFunc) {
		boolean changed = scoringFunction!=scoringFunc;
		scoringFunction = scoringFunc;		
		if (changed)
			fireScoringChanged();
	}

	public double getTimeShift() {
		return alignedDataSets.getTimeShift();
	}
	
	public void setTimeShift(double newShift) {
		boolean changed = newShift != getTimeShift();
		if (changed) {
			alignedDataSets.changeShift(newShift);
			eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.SHIFT_CHANGED));
		}
	}
	
	public boolean getShowOnlyMatching() {
		return showOnlyMatching;
	}
	
	public void setShowOnlyMatching(boolean onlyMatching) {
		showOnlyMatching = onlyMatching;
		eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.MATCHINGDISPLAY_CHANGED));
	}
	
	public void addListener(AlignmentStoreListener l) {
		eventFirer.addListener(l);
	}
	
	public void removeListener(AlignmentStoreListener l) {
		eventFirer.removeListener(l);
	}	
	
	public void fireScoringChanged() {
		eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.SCORING_CHANGED));
	}
	
	
	public void fireAlignmentChanged() {
		eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.SHIFT_CHANGED));
	}
	
	
	public void setProbeStatistic(ProbeCombinationStatistic stat) {
		if (statistic!=stat) {
			if (statistic!=null)
				statistic.setStore(null);
			statistic = stat;
			if (statistic!=null)
				statistic.setStore(this);
			eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.STATISTIC_CHANGED));
		}
	}
	
	public ProbeCombinationStatistic getProbeStatistic() {
		return statistic;
	}
	
	public void statisticChanged() {
		eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.STATISTIC_CHANGED));
	}
	
	
	
	protected EventFirer<AlignmentStoreEvent, AlignmentStoreListener> eventFirer = 
		new EventFirer<AlignmentStoreEvent, AlignmentStoreListener>() {
			protected void dispatchEvent(AlignmentStoreEvent event,
					AlignmentStoreListener listener) {
				listener.alignmentChanged(event);
			}
	};

	
	
	public void initViewModels(DataSet d1, DataSet d2) {
		
		for (int i=0; i!=visualizers.length; ++i)
			visualizers[i] = new NonClosingVisualizer();

		ViewModel[] models = new ViewModel[4];
		
		// create view on first input dataset
		models[0] = new EnrichedViewModel(visualizers[0], d1);
		
		// create view on second input dataset
		models[1] = new EnrichedViewModel(visualizers[1], d2);
		
		// create combined dataset
		DataSet pairedProbeDS = new DataSet(d1.getName()+" & "+d2.getName());
		pairedProbeDS.setMasterTable(new PairedProbesMasterTable(this, pairedProbeDS));
		pairedProbeDS.getMasterTable().addMasterTableListener(this);
		models[2] = new EnrichedViewModel(visualizers[2], pairedProbeDS);
		
		// create statistics dataset
		DataSet statisticsDS = new DataSet(d1.getName()+" * "+d2.getName());
		statisticsDS.setMasterTable(new StatisticsMasterTable(this, statisticsDS));
		statisticsDS.getMasterTable().addMasterTableListener(this);
		models[3] = new EnrichedNonModifyingViewModel(visualizers[3], statisticsDS);
				
		for (int i=0; i!=visualizers.length; ++i)
			visualizers[i].setViewModel(models[i]);
		
		// bind all together
		linker = new ViewModelLinker(models);
				
	}
	
	public void dispose() {
		for (NonClosingVisualizer viz : visualizers)
			viz.dispose();
	}
	
	public Visualizer getVisualizerOne() {
		return visualizers[0];
	}
	
	public Visualizer getVisualizerTwo() {
		return visualizers[1];
	}
	
	public Visualizer getVisualizerPaired() {
		return visualizers[2];
	}
	
	public Visualizer getVisualizerStatistics() {
		return visualizers[3];
	}
	
	public void removeProbeListFromViewModels(ProbeList pl) {
		DataSet sourceDataSet = pl.getDataSet();
		ViewModel targetModel = linker.getViewModel(sourceDataSet);
		if (targetModel!=null)
			targetModel.removeProbeListFromSelection(pl);
	}

	public Set<ProbeList> getProbeListsInViewModels(boolean optim) {
		HashSet<ProbeList> ret = new HashSet<ProbeList>();
		for (ViewModel vm : linker.getModels())
			ret.addAll(vm.getProbeLists(optim));
		return ret;
	}
	
	public void addProbeListToViewModels(ProbeList pl) {
		DataSet sourceDataSet = pl.getDataSet();
		ViewModel targetModel = linker.getViewModel(sourceDataSet);
		if (targetModel!=null) {
			targetModel.addProbeListToSelection(pl);
		}
	}
	
	public void masterTableChanged(MasterTableEvent event) {
		// translate mata change events to events that are recognized by the viewmodels (only for derived models)
		MasterTable mt = (MasterTable)event.getSource();
		if (mt instanceof DerivedMasterTable) {
			ViewModel targetModel = linker.getViewModel(mt.getDataSet());
			if (targetModel!=null)
				targetModel.probeListManagerChanged(new ProbeListManagerEvent(this, ProbeListManagerEvent.ORDER_CHANGE));
		}
	}
	
	public void setScoringForAll(boolean sfa) { 
		if (sfa!=scoringForAll) {
			scoringForAll=sfa;
			fireScoringChanged();
		}
	}
	
	public boolean isScoringForAll() {
		return scoringForAll;
	}

	@Override
	public void dataSetChanged(DataSetEvent event) {
		eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.STORE_CLOSED));		
	}
	
}
