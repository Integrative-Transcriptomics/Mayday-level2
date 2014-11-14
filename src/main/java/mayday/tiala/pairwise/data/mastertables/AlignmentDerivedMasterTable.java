package mayday.tiala.pairwise.data.mastertables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.MasterTableEvent;
import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.data.AlignmentStoreEvent;
import mayday.tiala.pairwise.data.AlignmentStoreListener;

public abstract class AlignmentDerivedMasterTable extends DerivedMasterTable implements AlignmentStoreListener {

	protected AlignmentStore store;
	
	protected int alignmentNumberOfExperiments;
	protected ArrayList<String> alignmentExperimentNames = new ArrayList<String>();

	public AlignmentDerivedMasterTable(AlignmentStore Store, DataSet dataSet) {
		super(dataSet, Store.getOne().getDataSet().getMasterTable(), Store.getTwo().getDataSet().getMasterTable());
		store = Store;
		initProbes();
		store.addListener(this);		
	}

//	public String getExperimentName(int experiment) {
//		if (store.getShowOnlyMatching())
//			return store.getAlignedDataSets().getMatching().get(experiment).getTime()+"";
//		else 
//			return store.getAlignedDataSets().getAll().get(experiment).getTime()+"";
//	}
	
	public AlignmentStore getStore() {
		return store;
	}
	
	protected void updateNumberOfExperiments() {
		alignmentExperimentNames.clear();
		if (store.getShowOnlyMatching()) {
			setAlignmentNumberOfExperiments(store.getAlignedDataSets().getMatching().size());
			for (int i=0; i!=store.getAlignedDataSets().getMatching().size(); ++i) {
				setAlignmentExperimentName(i, store.getAlignedDataSets().getMatching().get(i).getTime()+"");
			}
		} else {
			setAlignmentNumberOfExperiments(store.getAlignedDataSets().getAll().size());
			for (int i=0; i!=store.getAlignedDataSets().getAll().size(); ++i) {
				setAlignmentExperimentName(i, store.getAlignedDataSets().getAll().get(i).getTime()+"");
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
				addDerivedProbes(n);
			}	
		}			
	}

	
	protected abstract void refreshProbes(boolean force);
	
	public void alignmentChanged(AlignmentStoreEvent evt) {
		if (evt.getChange()==AlignmentStoreEvent.SHIFT_CHANGED || evt.getChange()==AlignmentStoreEvent.MATCHINGDISPLAY_CHANGED) {
			updateNumberOfExperiments();
			refreshProbes(true);
			fireMasterTableChanged(MasterTableEvent.OVERALL_CHANGE);
		}
	}
	
	public int getAlignmentNumberOfExperiments() {
		return alignmentNumberOfExperiments;
	}
	
	public void setAlignmentNumberOfExperiments(int anoe) {
		alignmentNumberOfExperiments=anoe;
		trimAlignmentExperimentNames();
	}
	
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
	
	@SuppressWarnings("unchecked")
	public void setAlignmentExperimentNames( Collection experimentNames ) {
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

	public void setAlignmentExperimentName( int experiment, String name ) {
        if ( experiment >= getAlignmentNumberOfExperiments() )
        {
            throw ( new RuntimeException( "Experiment " +
                experiment +
            " does not exist." ) );
        }
        
        alignmentExperimentNames.set( experiment, name );
    }
}
