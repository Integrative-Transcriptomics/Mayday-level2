package mayday.tiala.multi.data.viewmodel;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.structures.maps.TwoKeyHashMap;
import mayday.tiala.multi.data.ProbeMapper;
import mayday.tiala.multi.data.mastertables.DerivedMasterTable;
import mayday.tiala.multi.data.probelists.MirrorProbeList;
import mayday.vis3.model.ManipulationMethod;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

/**
 * @author jaeger
 *
 */
public class ViewModelLinker {

	protected LinkedList<ViewModel> allModels = new LinkedList<ViewModel>();
	
	protected TwoKeyHashMap<DataSet, ProbeList, MirrorProbeList> CACHE = new TwoKeyHashMap<DataSet, ProbeList, MirrorProbeList>();
	
	protected boolean alreadySynchronizing = false;

	
	/**
	 * @param viewModels
	 */
	public ViewModelLinker(List<ViewModel> viewModels) {
		
		for (ViewModel vm : viewModels ) {
			allModels.add(vm);			
		}
		
		for (ViewModel vm : allModels) {
			if (vm.getDataSet().getMasterTable() instanceof DerivedMasterTable) {
				vm.addViewModelListener(outputListener);
			} else { 
				synchronizeProbeListSelection(vm);
				vm.addViewModelListener(inputListener);
			}
		}
	}
	
	/**
	 * @param ds
	 * @return the viewmodel for the specified dataset
	 */
	public ViewModel getViewModel(DataSet ds) {
		for (ViewModel vm : allModels)
			if (vm.getDataSet()==ds)
				return vm;
		return null;
	}
	
	/**
	 * @return all view models
	 */
	public Collection<ViewModel> getModels() {
		return Collections.unmodifiableCollection(allModels);
	}

	protected void addSynchronizedProbeLists(ViewModel sourceVM, ViewModel targetVM) {	
		for (ProbeList source : sourceVM.getProbeLists(false)) {
			
			// no second order mirrors
			if (source instanceof MirrorProbeList)
				continue;
			
			// is this link already present
			MirrorProbeList presentLink = CACHE.get(targetVM.getDataSet(), source);
			if (presentLink!=null)
				continue;
			
			// so we need to add a new link
			MirrorProbeList targetList = new MirrorProbeList(targetVM.getDataSet(), source);
			CACHE.put(targetList.getDataSet(), source, targetList);
			targetVM.addProbeListToSelection(targetList);
			
		}
	}
	
	protected void removeSynchronizedProbeLists(ViewModel sourceVM, ViewModel targetVM) {
		for (MirrorProbeList targetCandidate : CACHE.getAll(targetVM.getDataSet(),null)) {
			ProbeList source = targetCandidate.getSourceProbeList();
			if (source.getDataSet()==sourceVM.getDataSet() && !sourceVM.getProbeLists(false).contains(source)) {
				targetVM.removeProbeListFromSelection(targetCandidate);
				targetCandidate.propagateClosing();
				CACHE.remove(targetCandidate.getDataSet(), targetCandidate.getSourceProbeList());
			}			
		}
	}
	
	
	protected void synchronizeProbeListSelection(ViewModel sourceVM) {
		if (alreadySynchronizing)
			return;
		alreadySynchronizing=true;
		for (ViewModel targetVM : allModels) {
			if (targetVM!=sourceVM) {
				removeSynchronizedProbeLists(sourceVM, targetVM);
				addSynchronizedProbeLists(sourceVM, targetVM);
			}
		}
		alreadySynchronizing=false;
	}
	

	protected void synchronizeProbeSelection(ViewModel sourceVM, ViewModel targetVM) {
		Collection<Probe> sourceSel = sourceVM.getSelectedProbes();
		Collection<Probe> targetSel = new TreeSet<Probe>();
		MasterTable sourceMT = sourceVM.getDataSet().getMasterTable();
		MasterTable targetMT = targetVM.getDataSet().getMasterTable();
		
		for (Probe pb : sourceSel) {
			targetSel.addAll(ProbeMapper.map(sourceMT, pb, targetMT));
		}
		
		targetVM.setProbeSelection(targetSel);
	}
	
	protected void synchronizeProbeSelection(ViewModel sourceVM) {
		if (alreadySynchronizing)
			return;
		alreadySynchronizing=true;
		for (ViewModel targetVM : allModels)
			if (targetVM!=sourceVM)
				synchronizeProbeSelection(sourceVM, targetVM);
		alreadySynchronizing=false;
	}
	
	protected void synchronizeDataManipulation(ViewModel sourceVM) {
		if (alreadySynchronizing)
			return;
		alreadySynchronizing=true;
		ManipulationMethod newDataManipulation = sourceVM.getDataManipulator().getManipulation();
		for (ViewModel targetVM : allModels) 
			if (targetVM!=sourceVM) 
				targetVM.getDataManipulator().setManipulation(newDataManipulation);
		alreadySynchronizing=false;
	}

	protected ViewModelListener inputListener = new ViewModelListener() {		
		public void viewModelChanged(ViewModelEvent vme) {
			if (vme.getChange()==ViewModelEvent.PROBELIST_SELECTION_CHANGED)
				synchronizeProbeListSelection((ViewModel)vme.getSource());
			else
				outputListener.viewModelChanged(vme);		
		}	
	};

	protected ViewModelListener outputListener = new ViewModelListener() {		
		public void viewModelChanged(ViewModelEvent vme) {
			switch (vme.getChange()) {
			case ViewModelEvent.PROBE_SELECTION_CHANGED:
				synchronizeProbeSelection((ViewModel)vme.getSource());
				break;				
			case ViewModelEvent.DATA_MANIPULATION_CHANGED:
				synchronizeDataManipulation((ViewModel)vme.getSource());
				break;
			}			
		}	
	};
}
