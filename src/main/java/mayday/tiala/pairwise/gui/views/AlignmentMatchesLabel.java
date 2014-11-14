package mayday.tiala.pairwise.gui.views;

import javax.swing.JLabel;

import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.data.AlignmentStoreEvent;
import mayday.tiala.pairwise.data.AlignmentStoreListener;

@SuppressWarnings("serial")
public class AlignmentMatchesLabel extends JLabel implements AlignmentStoreListener {

	AlignmentStore store;

	public AlignmentMatchesLabel(AlignmentStore store) {
		this.store=store;
		update();
		store.addListener(this);
	}

	protected void update() {
		setText("Currently matched: "+store.getAlignedDataSets().getCommonCount()+" experiments");
		invalidate();
		validate();
	}

	public void alignmentChanged(AlignmentStoreEvent event) {
		if (event.getChange()==AlignmentStoreEvent.SHIFT_CHANGED) {
			update();
		}
	}

}
