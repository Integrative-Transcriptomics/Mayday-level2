package mayday.tiala.pairwise.gui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.data.AlignmentStoreEvent;
import mayday.tiala.pairwise.data.AlignmentStoreListener;

@SuppressWarnings("serial")
public class ScoreSetControl extends JCheckBox implements AlignmentStoreListener {
	
	protected AlignmentStore store;
	
	public ScoreSetControl(AlignmentStore Store) {
		super("Compute for all probes");
		
		store=Store;

		updateSelection();

	    addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (store!=null)
					store.setScoringForAll(isSelected());
			}
	    	
	    });
	    
	    setPreferredSize(null);
	}	
	
	public void updateSelection() {
		AlignmentStore hiddenstore = store; 
		store=null;
		setSelected(hiddenstore.isScoringForAll());
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
		if (evt.getChange()==AlignmentStoreEvent.SCORING_CHANGED)
			updateSelection();
	}
    
}
