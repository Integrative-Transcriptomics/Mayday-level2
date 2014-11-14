package mayday.tiala.multi.gui.actions;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.datasetmanager.DataSetManager;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.mio.AlignmentMIO;
import mayday.tiala.multi.suggestion.ScoredAlignment;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class Alignment2DataSetsAction extends AbstractAction {

	AlignmentStore Store;
	protected final int number;

	/**
	 * @param datasetID
	 */
	public Alignment2DataSetsAction(final int datasetID) {
		super("Create aligned DataSets");
		this.number = datasetID;
	}

	/**
	 * @param datasetID
	 * @param store
	 */
	public Alignment2DataSetsAction(final int datasetID, AlignmentStore store) {
		this(datasetID);
		setStore(store);
	}

	/**
	 * @param store
	 */
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
		am.setTimeShifts(Store.getAlignedDataSets().getTimeShifts());

		Collection<DataSet> resultsets = new LinkedList<DataSet>();
		resultsets.addAll( Store.getAlignedDataSets().deriveDataSetsAll() );

		for (DataSet ds : resultsets) {
			sa.addMIOs(ds);
			ds.getMIManager().newGroup(AlignmentMIO.myType, "Alignment Information").add(ds,am);
			DataSetManager.singleInstance.addObjectAtBottom(ds);
		}
	}
}
