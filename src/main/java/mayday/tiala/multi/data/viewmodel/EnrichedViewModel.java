package mayday.tiala.multi.data.viewmodel;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.tiala.multi.data.mastertables.DerivedMasterTable;
import mayday.tiala.multi.data.probes.DerivedProbe;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.Visualizer;

public class EnrichedViewModel extends ViewModel {	

	protected boolean growSelections;
	
	public EnrichedViewModel(Visualizer viz, DataSet ds) {
		super(viz, ds, null);
		growSelections = (ds.getMasterTable() instanceof DerivedMasterTable);
	}

	protected LinkedList<ProbeList> getSelectedProbeListOrdering() {
		LinkedList<ProbeList> ret = super.getSelectedProbeListOrdering();
		// now add all the probelists that are not part of my own dataset
		for (ProbeList npl : naturalProbeListSelection) {
			if (!dataSet.getProbeListManager().contains(npl)) {
				ret.add(npl);
			}
		}
		return ret;
	}
	
	protected Collection<Probe> extendProbeSelection(Collection<Probe> selection) {
		if (selection.size()==0)
			return selection;
		
		if (growSelections && !(selection.iterator().next() instanceof DerivedProbe))
			growSelections = false; // stays false for ever
		
		if (!growSelections)
			return selection;
		
		TreeSet<Probe> ret = new TreeSet<Probe>();
		for (Probe pb : selection) {
			ret.addAll(extendProbeSelection(pb));
		}
		return ret;
	}
	
	protected Collection<Probe> extendProbeSelection(Probe pb) {
		String name = ((DerivedProbe)pb).getSourceName();
		return ((DerivedMasterTable)getDataSet().getMasterTable()).getProbes(name);
	}
	
	public void setProbeSelection(Collection<Probe> newSelection) {
		// find all corresponding probes in the mastertable
		
		newSelection = extendProbeSelection(newSelection);
		
		if (newSelection.size()==probeSelection.size()) {
			TreeSet<Probe> tmp = new TreeSet<Probe>(probeSelection);
			tmp.removeAll(newSelection);
			if (tmp.size()==0)
				return;  // identical, no change
		}
		probeSelection.clear();
		
		probeSelection.addAll(newSelection);
		eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.PROBE_SELECTION_CHANGED));
	}

	public void selectProbe(Probe pb) {
		if (probeSelection.addAll(extendProbeSelection(pb))) {
			eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.PROBE_SELECTION_CHANGED));
		}
	}
	
	public void unselectProbe(Probe pb) {
		if (probeSelection.removeAll(extendProbeSelection(pb))) {
			eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.PROBE_SELECTION_CHANGED));
		}
	}
	
	public void toggleProbeSelected(Probe pb) {
		if (probeSelection.contains(pb))
			unselectProbe(pb);
		else
			selectProbe(pb);
	}
	
	public void toggleProbesSelected(Set<Probe> probes) {
		for (Probe pb : probes)			
			if (probeSelection.contains(pb))
				probeSelection.removeAll(extendProbeSelection(pb));
			else
				probeSelection.addAll(extendProbeSelection(pb));
		eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.PROBE_SELECTION_CHANGED));
	}	
}
