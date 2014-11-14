package mayday.tiala.multi.data.mastertables;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.MasterTableEvent;
import mayday.core.MasterTableListener;
import mayday.core.Probe;

public abstract class DerivedMasterTable extends MasterTable {

	protected MasterTableListener parentListener;
	protected MasterTable[] masterTables;
	
	public int ID;

	public DerivedMasterTable(int ID, DataSet dataSet, MasterTable[] mts) {
		super(dataSet);
		this.ID = ID;
		this.masterTables = mts;
		
		parentListener = new MasterTableListener() {
			public void masterTableChanged(MasterTableEvent event) {
				switch (event.getChange()) {
				case MasterTableEvent.SYSTEM_PROBE_ADDED_CHANGE: //fallthrough
				case MasterTableEvent.USER_PROBE_ADDED_CHANGE:
					handleProbeAdded((MasterTable)event.getSource());
					break;
				case MasterTableEvent.SYSTEM_PROBE_REMOVED_CHANGE: //fallthrough
				case MasterTableEvent.USER_PROBE_REMOVED_CHANGE:
					handleProbeRemoved((MasterTable)event.getSource());
					break;
				}				
			}			
		};
		initProbes();
		
		for(MasterTable mt : masterTables) {
			mt.addMasterTableListener(parentListener);
		}
	}
	
	protected abstract void initProbes();
	
	protected void handleProbeAdded(MasterTable sender) {
		HashSet<String> addedProbes = new HashSet<String>(sender.getProbes().keySet());
		for (String n : getProbeNames()) 
			addedProbes.remove(n);
		handleProbesAdded(addedProbes);
	}
	
	protected void handleProbesAdded(Set<String> probeNames) {
		for (String n: probeNames) {
			addDerivedProbes(n, probeNames.size());
			fireMasterTableChanged(MasterTableEvent.SYSTEM_PROBE_ADDED_CHANGE);
		}
	}

	protected void handleProbesRemoved(Set<String> probeNames) {
		for (String n : probeNames){
			removeDerivedProbes(n);
		}
	}

	protected void handleProbeRemoved(MasterTable sender) {
		HashSet<String> removedProbes = new HashSet<String>(getProbeNames());
		for (String n : sender.getProbes().keySet()){
			removedProbes.remove(n);
		}
		handleProbesRemoved(removedProbes);
	}
	
	protected abstract void addDerivedProbes(String sourceName, int numOfProbes);
	
	protected void removeDerivedProbes(String sourceName) {
		this.removeProbe(getProbe(sourceName), true);
	}
	
	public List<Probe> getProbes(String basename){
		Probe pb = getProbe(basename);
		if (pb==null) {
			return Collections.emptyList();
		} else {
			LinkedList<Probe> llP = new LinkedList<Probe>();
			llP.add(pb);
			return llP;
		}
	}
	
	protected Set<String> getProbeNames(){
		return probes.keySet();
	}
	
	public int read(String fileName){
		return 0;
	}

	public void addProbe(Probe probe){
		throw new RuntimeException("Unable to add a probe to a derived mastertable!");		
	}

	public void removeProbe(String name){
		throw new RuntimeException("Unable to remove a probe from a derived mastertable!");
	}

	public void renameProbe(String oldName, String newName, boolean suppressWarnings) {
		throw new RuntimeException("Unable to rename a probe in a derived mastertable!");		
	}

	public void setNumberOfExperiments(int numberOfExperiments) {
		throw new RuntimeException("Unable to change the number of experiments for a derived mastertable!");
	}
	
    protected void setNumberOfExperiments(int numberOfExperiments, boolean override) {
    	super.setNumberOfExperiments(numberOfExperiments);
    }

	public void setExperimentName(int experiment, String name) {
		throw new RuntimeException("Unable to change the names of experiments for a derived mastertable!");
	}

	public String toString() {
		return "Derived MasterTable "+super.toString();
	}
	
	public MasterTable getMasterTable(int index) {
		return this.masterTables[index];
	}
}
