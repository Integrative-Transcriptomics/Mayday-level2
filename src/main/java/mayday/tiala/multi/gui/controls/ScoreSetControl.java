package mayday.tiala.multi.gui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;
import mayday.tiala.multi.data.AlignmentStoreListener;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class ScoreSetControl extends JCheckBox implements AlignmentStoreListener {
	
	protected AlignmentStore store;
	
	/**
	 * @param Store
	 */
	public ScoreSetControl(AlignmentStore Store) {
		super("Compute for all probes");
		
		store=Store;

		updateSelection();

	    addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (store!=null)
					store.getSettings().setScoringForAll(isSelected());
			}
	    });
	    
	    setPreferredSize(null);
	}	
	
	/**
	 * 
	 */
	public void updateSelection() {
		AlignmentStore hiddenstore = store; 
		store=null;
		setSelected(hiddenstore.getSettings().isScoringForAll());
		store = hiddenstore;
	}
	
	public void removeNotify() {
		super.removeNotify();
		store.removeListener(this);
	}
	
	public void addNotify() {
		super.addNotify();
	    store.addListener(this);
	}

	public void alignmentChanged(AlignmentStoreEvent evt) {
		switch(evt.getChange()) {
		case AlignmentStoreEvent.SCORING_CHANGED:
			updateSelection();
			break;
		case AlignmentStoreEvent.STORE_CLOSED:
			store.removeListener(this);
			break;
		}
	}
}
