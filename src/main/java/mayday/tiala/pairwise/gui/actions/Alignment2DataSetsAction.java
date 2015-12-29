package mayday.tiala.pairwise.gui.actions;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.datasetmanager.DataSetManager;
import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.mio.AlignmentMIO;
import mayday.tiala.pairwise.suggestion.ScoredAlignment;

@SuppressWarnings("serial")
public class Alignment2DataSetsAction extends AbstractAction {

	AlignmentStore Store;

	public Alignment2DataSetsAction() {
		super("Create aligned DataSets");
	}

	public Alignment2DataSetsAction(AlignmentStore store) {
		this();
		setStore(store);
	}

	public void setStore(AlignmentStore store) {
		Store = store;
	}

	public void actionPerformed(ActionEvent e) {
		if (Store==null) 
			return;

		AlignmentMIO am = new AlignmentMIO();

		ScoredAlignment sa = new ScoredAlignment(Store.getAlignedDataSets(), true);
		am.setScore(sa.getScore());
		am.setQuantiles(Arrays.toString(sa.getQuantiles()));
		am.setTimeShift(Store.getAlignedDataSets().getTimeShift());

		Collection<DataSet> resultsets = new LinkedList<DataSet>();
		resultsets.addAll( Store.getAlignedDataSets().deriveDataSets() );

		for (DataSet ds : resultsets) {
			sa.addMIOs(ds);
			ds.getMIManager().newGroup(AlignmentMIO.myType, "Alignment Information").add(ds,am);
			DataSetManager.singleInstance.addObjectAtBottom(ds);
		}

	}

}
