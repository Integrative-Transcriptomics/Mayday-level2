package mayday.tiala.multi.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.datasetmanager.DataSetManager;
import mayday.tiala.multi.data.AlignmentStore;

@SuppressWarnings("serial")
public class Statistics2DataSetAction extends AbstractAction {

	AlignmentStore Store;
	protected int ID;

	public Statistics2DataSetAction(int ID) {
		super("...as new DataSet");
		this.ID = ID;
	}

	public Statistics2DataSetAction(final int number, AlignmentStore store) {
		this(number);
		setStore(store);
	}

	public void setStore(AlignmentStore store) {
		Store = store;
	}

	public void actionPerformed(ActionEvent e) {
		if (Store==null) 
			return;
		DataSet ds = Store.getVisualizerStatistics(ID).getViewModel().getDataSet();
		DataSet newDS = new DataSet(Store.getProbeStatistic(ID).toString());
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
