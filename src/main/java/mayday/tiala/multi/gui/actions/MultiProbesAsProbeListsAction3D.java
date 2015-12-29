package mayday.tiala.multi.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.probes.MultiProbe;
import mayday.tiala.multi.gui.plots.AlignmentMultiProfileplotComponent;

/**
 * @author jaeger
 *
 */
public class MultiProbesAsProbeListsAction3D extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected AlignmentMultiProfileplotComponent profiles;
	protected int numMultiProbes;
	protected AlignmentStore store;
	
	/**
	 * @param profiles
	 * @param store 
	 * @param numMultiProbes 
	 */
	public MultiProbesAsProbeListsAction3D(AlignmentMultiProfileplotComponent profiles, AlignmentStore store) {
		super("Create ProbeLists from MultiProbes");
		this.profiles = profiles;
		this.store = store;
		this.numMultiProbes = store.getTimepointDatasets().size();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(profiles.viewModel == null)
			return;
		
		DataSet[] dss = store.getAlignedDataSets().deriveDataSetsAll().toArray(new DataSet[0]);
		
		if(dss == null) {
			return;
		} else if(dss.length == 0) {
			return;
		}
		
		DataSet ds = dss[0];
		ds.setName("MP2PL - " + store.getTimepointDatasets().get(0).getDataSet().getName());
		
		MultiProbe[] allProbes = profiles.viewModel.getProbes().toArray(new MultiProbe[0]);
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
