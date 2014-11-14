package mayday.tiala.multi.gui.views;

import javax.swing.JLabel;

import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;
import mayday.tiala.multi.data.AlignmentStoreListener;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class AlignmentMatchesLabel extends JLabel implements AlignmentStoreListener {

	AlignmentStore store;

	/**
	 * @param store
	 * @param index
	 */
	public AlignmentMatchesLabel(AlignmentStore store) {
		this.store = store;
		update();
		store.addListener(this);
	}

	protected void update() {
		setText("Currently matched: " + store.getAlignment().size() + " experiments");
		invalidate();
		validate();
	}

	public void alignmentChanged(AlignmentStoreEvent evt) {
		switch(evt.getChange()) {
		case AlignmentStoreEvent.SHIFT_CHANGED:
			update();
			break;
		case AlignmentStoreEvent.STORE_CLOSED:
			store.removeListener(this);
			break;
		}
	}
}
