package mayday.tiala.pairwise.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.datasetmanager.DataSetManager;
import mayday.tiala.pairwise.data.AlignmentStore;

@SuppressWarnings("serial")
public class Statistics2DataSetAction extends AbstractAction {

	AlignmentStore Store;

	public Statistics2DataSetAction() {
		super("...as new DataSet");
	}

	public Statistics2DataSetAction(AlignmentStore store) {
		this();
		setStore(store);
	}

	public void setStore(AlignmentStore store) {
		Store = store;
	}

	public void actionPerformed(ActionEvent e) {
		if (Store==null) 
			return;
		DataSet ds = Store.getVisualizerStatistics().getViewModel().getDataSet();
		DataSet newDS = new DataSet(Store.getProbeStatistic().toString());
		MasterTable newMata = newDS.getMasterTable();
		MasterTable mata = ds.getMasterTable();
		newMata.setNumberOfExperiments(mata.getNumberOfExperiments());
		newMata.setExperimentNames(mata.getExperimentNames());				
		for (Probe pb : mata.getProbes().values()) {
			Probe newPb = new Probe(newMata);
			newPb.setName(pb.getName());					
			newPb.setValues(pb.getValues(), true);
			newMata.addProbe(newPb);
		}
		DataSetManager.singleInstance.addObjectAtBottom(newDS);
	}

}
