package mayday.tiala.multi.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.probes.MultiProbe;
import mayday.tiala.multi.gui.plots.AlignmentProfilePlotComponent;

/**
 * 
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class MultiProbesAsProbeListsAction2D extends AbstractAction {
	
	protected AlignmentProfilePlotComponent profiles;
	protected int numMultiProbes;
	protected AlignmentStore store;
	
	public MultiProbesAsProbeListsAction2D(AlignmentProfilePlotComponent profiles, AlignmentStore store) {
		super("Create ProbeLists from MultiProbes");
		this.profiles = profiles;
		this.store = store;
		this.numMultiProbes = store.getTimepointDatasets().size();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(profiles.getViewModel() == null)
			return;
		
		DataSet[] dss = store.getAlignedDataSets().deriveDataSetsAll().toArray(new DataSet[0]);
		
		if(dss == null) {
			return;
		} else if(dss.length == 0) {
			return;
		}
		
		DataSet ds = dss[0];
		ds.setName("MP2PL - " + store.getTimepointDatasets().get(0).getDataSet().getName());
		
		MultiProbe[] allProbes = profiles.getViewModel().getProbes().toArray(new MultiProbe[0]);
		ProbeList[] probeLists = new ProbeList[numMultiProbes];
		
		for(int i = 0; i < probeLists.length; i++) {
			probeLists[i] = new ProbeList(ds, true);
			probeLists[i].setName("Probes from " + store.getTimepointDatasets().get(i).getDataSet().getName());
			ds.getProbeListManager().addObject(probeLists[i]);
		}
		
		for(int i = 0; i < allProbes.length; i++) {
			probeLists[i % numMultiProbes].addProbe(allProbes[i]);
		}

		DataSetManager.singleInstance.addObjectAtBottom(ds);
	}
}
