package mayday.tiala.multi.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.DataSetEvent;
import mayday.core.DataSetListener;
import mayday.core.EventFirer;
import mayday.core.MasterTable;
import mayday.core.MasterTableEvent;
import mayday.core.MasterTableListener;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.math.distance.measures.PearsonCorrelationDistance;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringMIO;
import mayday.core.probelistmanager.ProbeListManagerEvent;
import mayday.tiala.multi.data.container.NonClosingVisualizers;
import mayday.tiala.multi.data.container.TimeShifts;
import mayday.tiala.multi.data.container.TimepointDataSets;
import mayday.tiala.multi.data.mastertables.DerivedMasterTable;
import mayday.tiala.multi.data.mastertables.MultiProbesMasterTable;
import mayday.tiala.multi.data.mastertables.StatisticsMasterTable;
import mayday.tiala.multi.data.viewmodel.EnrichedViewModel;
import mayday.tiala.multi.data.viewmodel.NonClosingVisualizer;
import mayday.tiala.multi.data.viewmodel.ViewModelLinker;
import mayday.tiala.multi.settings.AlignmentStoreSettings;
import mayday.tiala.multi.statistics.FoldChange;
import mayday.tiala.multi.statistics.ProbeCombinationStatistic;
import mayday.tiala.multi.suggestion.AlignmentSearchTask;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.Visualizer;

/**
 * Contains everything related to the Alignment GUI, in a way the data model behind everything
 * @author jaeger
 */
public class AlignmentStore implements MasterTableListener, DataSetListener {
	
	protected AlignmentStoreSettings settings;
	
	protected TimepointDataSets timepointDatasets;
	protected TimeShifts timeShifts;
	
	protected AlignedDataSets alignedDataSets;
	protected AlignmentSearchTask possibleAlignments;
	
	protected DistanceMeasurePlugin[] scoringFunctions;
	protected ProbeCombinationStatistic[] statistics;
	
	protected NonClosingVisualizers datasetVisualizers;
	protected NonClosingVisualizers statisticsVisualizers;
	protected NonClosingVisualizer alignmentVisualizer;
	
	protected ViewModelLinker linker;
	
	/**
	 * @param dataSets
	 */
	public void initialize(List<DataSet> dataSets) {
		timepointDatasets = new TimepointDataSets(dataSets);
		timeShifts = new TimeShifts(dataSets.size() - 1);
		alignedDataSets = new AlignedDataSets(this, timepointDatasets, timeShifts);
		
		settings = new AlignmentStoreSettings(this);
		
		//initialize scoring functions
		scoringFunctions = new DistanceMeasurePlugin[dataSets.size() - 1];
		for(int i = 0; i < scoringFunctions.length; i++) {
			scoringFunctions[i] = new PearsonCorrelationDistance();
		}
		
		//initialize statistics
		statistics = new ProbeCombinationStatistic[dataSets.size() - 1];
		for(int i = 0; i < dataSets.size() - 1; i++) {
			statistics[i] = new FoldChange();
			statistics[i].setStore(this, i);
		}
		
		this.datasetVisualizers = new NonClosingVisualizers(dataSets.size());
		this.alignmentVisualizer = new NonClosingVisualizer();
		this.statisticsVisualizers = new NonClosingVisualizers(dataSets.size() - 1);
		
		this.initViewModels(dataSets);
		
		//search all possible time shifts
		possibleAlignments = new AlignmentSearchTask(timepointDatasets);
		possibleAlignments.start();
		
		for (DataSet ds : dataSets) {
			ds.addDataSetListener(this);
		}
	}
	
	/**
	 * @return settings
	 */
	public AlignmentStoreSettings getSettings() {
		return this.settings;
	}
	
	/**
	 * @return alignment time-points
	 */
	public List<Double> getAlignment() {
		return this.alignedDataSets.alignment;
	}
	
	/**
	 * @return all experiment time-points
	 */
	public List<Double> getAllExperiments() {
		return this.alignedDataSets.getAllExperiments();
	}
	
	/**
	 * @return all master tables
	 */
	public MasterTable[] getMasterTables() {
		MasterTable[] mts = new MasterTable[alignedDataSets.dataSets.size()];
		for(int i = 0; i < mts.length; i++) {
			mts[i] = alignedDataSets.get(i).getDataSet().getMasterTable();
		}
		return mts;
	}

	/**
	 * @param index
	 * @return time-point data-set at position index
	 */
	public TimepointDataSet get(int index) {
		return this.alignedDataSets.get(index);
	}
	
	/**
	 * @return array of all time-point data sets
	 */
	public TimepointDataSets getTimepointDatasets() {
		return this.timepointDatasets;
	}

	/**
	 * @return all aligned data-sets
	 */
	public AlignedDataSets getAlignedDataSets() {
		return alignedDataSets;
	}

	/**
	 * @return the alignment search task containing all possible alignments
	 */
	public AlignmentSearchTask getPossibleAlignments() {
		possibleAlignments.waitFor();
		return possibleAlignments;
	}
	
	/**
	 * @param which
	 * @return scoring function at position 'which'
	 */
	public DistanceMeasurePlugin getScoringFunction(int which) {
		return scoringFunctions[which];
	}
	
	/**
	 * @param which
	 * @param scoringFunc
	 */
	public void setScoringFunction(int which, DistanceMeasurePlugin scoringFunc) {
		boolean changed = scoringFunctions[which] != scoringFunc;
		scoringFunctions[which] = scoringFunc;		
		if (changed)
			fireScoringChanged();
	}

	/**
	 * @return all time-shifts
	 */
	public TimeShifts getTimeShifts() {
		return alignedDataSets.getTimeShifts();
	}

	/**
	 * @param newShifts
	 */
	public void setTimeShifts(double[] newShifts) {
		boolean changed = false;
		for(int i = 0; i < newShifts.length; i++) {
			if(timeShifts.get(i) != newShifts[i]) {
				changed = true;
				break;
			}
		}
		if(changed) {
			alignedDataSets.changeShifts(newShifts);
			eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.SHIFT_CHANGED));
		}
	}
	
	/**
	 * @param index
	 * @param value
	 */
	public void setTimeShift(int index, Double value) {
		if(index >= 0 && index < timeShifts.size()) {
			if(timeShifts.get(index) != value) {
				alignedDataSets.changeShift(index, value);
//				eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.SHIFT_CHANGED));
			}
		}
	}
	
	/**
	 * @param l
	 */
	public void addListener(AlignmentStoreListener l) {
		eventFirer.addListener(l);
	}
	
	/**
	 * @param l
	 */
	public void removeListener(AlignmentStoreListener l) {
		eventFirer.removeListener(l);
	}	
	
	/**
	 * 
	 */
	public void fireScoringChanged() {
		eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.SCORING_CHANGED));
	}
	
	/**
	 * 
	 */
	public void fireAlignmentChanged() {
		eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.SHIFT_CHANGED));
	}
	
	/**
	 * 
	 */
	public void fireCenterChanged() {
		eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.CENTER_CHANGED));
	}
	
	/**
	 * 
	 */
	public void fireSelectionColorChanged() {
		eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.SELECTION_COLOR_CHANGED));
	}
	
	/**
	 * @param index
	 * @param stat
	 */
	public void setProbeStatistic(int index, ProbeCombinationStatistic stat) {
		if (statistics[index] != stat) {
			if (statistics[index] != null)
				statistics[index].setStore(null, index);
			statistics[index] = stat;
			if (statistics[index] != null)
				statistics[index].setStore(this, index);
			eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.STATISTIC_CHANGED));
		}
	}
	
	/**
	 * @param index
	 * @return the probe statistic at position index
	 */
	public ProbeCombinationStatistic getProbeStatistic(int index) {
		return statistics[index];
	}
	
	/**
	 * 
	 */
	public void statisticChanged() {
		eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.STATISTIC_CHANGED));
	}

	/**
	 * 
	 */
	public EventFirer<AlignmentStoreEvent, AlignmentStoreListener> eventFirer = 
		new EventFirer<AlignmentStoreEvent, AlignmentStoreListener>() {
			protected void dispatchEvent(AlignmentStoreEvent event,
					AlignmentStoreListener listener) {
				listener.alignmentChanged(event);
			}
		};

	/**
	 * @param datasets
	 */
	public void initViewModels(List<DataSet> datasets) {
		ArrayList<ViewModel> viewModels = new ArrayList<ViewModel>();
		
		String multiProbeDSName = "";
		String multiProbeDSNameStat = "";
		
		for(int j = 0; j < datasets.size(); j++) {
			ViewModel vm = new EnrichedViewModel(datasetVisualizers.get(j), datasets.get(j));
			datasetVisualizers.get(j).setViewModel(vm);
			viewModels.add(vm);
			
			if(j < datasets.size() - 1) {
				multiProbeDSName += datasets.get(j).getName()+" &";
				multiProbeDSNameStat += datasets.get(j).getName() + " *";
			} else {
				multiProbeDSName += datasets.get(j).getName();
				multiProbeDSNameStat += datasets.get(j).getName();
			}
		}
		
		//create combined data set
		DataSet alignmentDataset = new DataSet(multiProbeDSName);
		alignmentDataset.setMasterTable(new MultiProbesMasterTable(this, alignmentDataset));
		copyProbeDisplayNames(datasets.get(0), (MultiProbesMasterTable)alignmentDataset.getMasterTable(), datasets.size());
		alignmentDataset.getMasterTable().addMasterTableListener(this);
		
		ViewModel avm = new EnrichedViewModel(alignmentVisualizer, alignmentDataset);
		alignmentVisualizer.setViewModel(avm);
		viewModels.add(avm);
		
		//create statistics data sets
		for(int i = 0; i < statisticsVisualizers.size(); i++) {
			DataSet statisticsDataset = new DataSet(i + " : " + multiProbeDSNameStat);
			statisticsDataset.setMasterTable(new StatisticsMasterTable(i, this, statisticsDataset));
			statisticsDataset.getMasterTable().addMasterTableListener(this);
			ViewModel svm = new EnrichedViewModel(statisticsVisualizers.get(i), statisticsDataset);
			statisticsVisualizers.get(i).setViewModel(svm);
			viewModels.add(svm);
		}
		
		// bind all together
		linker = new ViewModelLinker(viewModels);
	}

	private void copyProbeDisplayNames(DataSet dataSet, MultiProbesMasterTable table, int numOfMultiProbes) {
		Probe[] dataSetProbes = dataSet.getMasterTable().getProbes().values().toArray(new Probe[0]);
		Map<String, Probe> multiDSProbes = table.getProbes(0);
		MIGroup probeDisplayNames = dataSet.getProbeDisplayNames();
		
		if (probeDisplayNames != null) {
			MIGroup multiProbeDisplayNames = new MIGroup(probeDisplayNames.getMIOPluginInfo(), probeDisplayNames.getName(), probeDisplayNames.getMIManager());
			for(int i = 0; i < dataSetProbes.length; i++) {
				MIType mt = probeDisplayNames.getMIO(dataSetProbes[i]);
				MIType nmt = null;
				if (mt==null || mt.toString().trim().length()==0) {
					if(mt != null) {
						nmt = new StringMIO("");
					}
				} else {
					nmt = new StringMIO(mt.serialize(MIType.SERIAL_TEXT));
				}
				
				Probe pb = multiDSProbes.get(dataSetProbes[i].getName());
				if(pb != null) {
					multiProbeDisplayNames.add(pb, nmt);
				}
			}
			table.getDataSet().setProbeDisplayNames(multiProbeDisplayNames);
		}
	}

	/**
	 * dispose all visualizers
	 */
	public void dispose() {
		alignmentVisualizer.dispose();
		datasetVisualizers.dispose();
		statisticsVisualizers.dispose();
	}
	
	/**
	 * @return the visualizer for the multiple-alignment
	 */
	public Visualizer getVisualizerCombined() {
		return this.alignmentVisualizer;
	}
	
	/**
	 * @param index
	 * @return the visualizer for the input data sets with id = index
	 */
	public Visualizer getVisualizer(int index) {
		return this.datasetVisualizers.get(index);
	}
	
	/**
	 * @param index
	 * @return the visualizer of the statistics plot for the data set with id = index
	 */
	public Visualizer getVisualizerStatistics(int index) {
		return this.statisticsVisualizers.get(index);
	}
	
	/**
	 * @param pl
	 */
	public void removeProbeListFromViewModels(ProbeList pl) {
		DataSet sourceDataSet = pl.getDataSet();
		ViewModel targetModel = linker.getViewModel(sourceDataSet);
		if (targetModel!=null)
			targetModel.removeProbeListFromSelection(pl);
	}

	/**
	 * @param optim
	 * @return all probe lists that are in a viewModel
	 */
	public Set<ProbeList> getProbeListsInViewModels(boolean optim) {
		HashSet<ProbeList> ret = new HashSet<ProbeList>();
		for (ViewModel vm : linker.getModels())
			ret.addAll(vm.getProbeLists(optim));
		return ret;
	}
	
	/**
	 * @param pl
	 */
	public void addProbeListToViewModels(ProbeList pl) {
		DataSet sourceDataSet = pl.getDataSet();
		ViewModel targetModel = linker.getViewModel(sourceDataSet);
		if (targetModel!=null) {
			targetModel.addProbeListToSelection(pl);
		}
	}
	
	public void masterTableChanged(MasterTableEvent event) {
		// translate master table change events to events that are recognized by the viewmodels (only for derived models)
		MasterTable mt = (MasterTable)event.getSource();
//		if(mt instanceof StatisticsMasterTable) {
//			String name = ((StatisticsMasterTable)mt).getDataSet().getName();
//			System.out.println(name);
//		}
		if (mt instanceof DerivedMasterTable) {
			ViewModel targetModel = linker.getViewModel(mt.getDataSet());
			if (targetModel!=null)
				targetModel.probeListManagerChanged(new ProbeListManagerEvent(this, ProbeListManagerEvent.ORDER_CHANGE));
		}
	}

	/**
	 * @param number
	 * @return all possible time-shifts for the "number"-th alignment
	 */
	public Set<Double> getPossibleTimeShifts(int number) {
		return getPossibleAlignments().getAllShifts(number);
	}
	
	@Override
	public void dataSetChanged(DataSetEvent event) {
		eventFirer.fireEvent(new AlignmentStoreEvent(this, AlignmentStoreEvent.STORE_CLOSED));		
	}
}
