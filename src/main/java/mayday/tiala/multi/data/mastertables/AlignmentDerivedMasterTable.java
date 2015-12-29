package mayday.tiala.multi.data.mastertables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.MasterTableEvent;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;
import mayday.tiala.multi.data.AlignmentStoreListener;

/**
 * @author jaeger
 *
 */
public abstract class AlignmentDerivedMasterTable extends DerivedMasterTable implements AlignmentStoreListener {

	protected AlignmentStore store;
	
	//for multiple alignment
	protected int alignmentNumberOfExperiments;
	protected ArrayList<String> alignmentExperimentNames = new ArrayList<String>();
	
	/**
	 * @param ID
	 * @param Store
	 * @param dataSet
	 */
	public AlignmentDerivedMasterTable(int ID, AlignmentStore Store, DataSet dataSet) {
		super(ID, dataSet, Store.getMasterTables());
		store = Store;
		initProbes();
		store.addListener(this);
	}
	
	/**
	 * @return store
	 */
	public AlignmentStore getStore() {
		return store;
	}
	
	protected void updateNumberOfExperiments() {
		//for multiple alignment
		alignmentExperimentNames.clear();
		
		if (store.getSettings().showOnlyMatching()) {
			setAlignmentNumberOfExperiments(store.getAlignment().size());
			for (int i = 0; i != store.getAlignment().size(); ++i) {
				setAlignmentExperimentName(i, store.getAlignment().get(i) + "");
			}
		} else {
			setAlignmentNumberOfExperiments(store.getAllExperiments().size());
			for (int i = 0; i != store.getAllExperiments().size(); ++i) {
				setAlignmentExperimentName(i, store.getAllExperiments().get(i) + "");
			}
		}
	}
	
	protected void handleProbesAdded(Set<String> probeNames) {
		super.handleProbesAdded(probeNames);
		refreshProbes(false);
	}

	protected void handleProbeRemoved(Set<String> probeNames) {
		super.handleProbesRemoved(probeNames);
		refreshProbes(false);
	}
	
	
	protected void initProbes() {
		if (store!=null) {
			updateNumberOfExperiments();
			probes.clear();
			for (String n : store.getAlignedDataSets().getCommonProbeNames()) {
				addDerivedProbes(n, store.getAlignedDataSets().getNumberOfDataSets());
			}
		}
	}
	
	protected abstract void refreshProbes(boolean force);
	
	public void alignmentChanged(AlignmentStoreEvent evt) {
		switch(evt.getChange()) {
		case AlignmentStoreEvent.SHIFT_CHANGED:
			updateNumberOfExperiments();
			refreshProbes(true);
			fireMasterTableChanged(MasterTableEvent.OVERALL_CHANGE);
			break;
		case AlignmentStoreEvent.MATCHINGDISPLAY_CHANGED:
			updateNumberOfExperiments();
			refreshProbes(true);
			fireMasterTableChanged(MasterTableEvent.OVERALL_CHANGE);
			break;
		case AlignmentStoreEvent.STORE_CLOSED:
			store.removeListener(this);
			break;
		}
	}
	
	/**
	 * @return number of timepoints in the alignment
	 */
	public int getAlignmentNumberOfExperiments() {
		return alignmentNumberOfExperiments;
	}
	
	/**
	 * @param anoe
	 */
	public void setAlignmentNumberOfExperiments(int anoe) {
		alignmentNumberOfExperiments=anoe;
		trimAlignmentExperimentNames();
	}
	
	/**
	 * @return experiment names for the multiple alignment
	 */
	public List<String> getAlignmentExperimentNames() {
		return Collections.unmodifiableList(alignmentExperimentNames);
	}
	
    protected void trimAlignmentExperimentNames() {
        while (alignmentExperimentNames.size()>alignmentNumberOfExperiments)
        	alignmentExperimentNames.remove(experiments.size()-1);
        while (alignmentExperimentNames.size()<alignmentNumberOfExperiments)
        	alignmentExperimentNames.add("unnamed experiment");
        alignmentExperimentNames.trimToSize();
    }
	
	protected void setAlignmentExperimentNames(Collection<String> experimentNames) {
		if (experimentNames.size()!=getAlignmentNumberOfExperiments())
			throw new RuntimeException("Number of experiment names must match number of experiments!");
		if (experimentNames.size()==0)
			return;
		Collection<String> lli;
		if (experimentNames.iterator().next() instanceof String) {
			lli = (Collection<String>)experimentNames;
		} else {
			lli = new LinkedList<String>();
			for (Object o : experimentNames)
				lli.add(o.toString());
		}
		this.alignmentExperimentNames.clear();
		this.alignmentExperimentNames.addAll(lli);
	}

	protected void setAlignmentExperimentName( int experiment, String name ) {
        if ( experiment >= getAlignmentNumberOfExperiments() )
        {
            throw ( new RuntimeException( "Experiment " +
                experiment +
            " does not exist." ) );
        }
        
        alignmentExperimentNames.set( experiment, name );
    }
}
