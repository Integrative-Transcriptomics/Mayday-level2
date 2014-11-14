package mayday.tiala.pairwise.gui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.data.AlignmentStoreEvent;
import mayday.tiala.pairwise.data.AlignmentStoreListener;

@SuppressWarnings("serial")
public class ScoreDistanceControl extends JComboBox implements AlignmentStoreListener {
	
	protected AlignmentStore store;
	
	public ScoreDistanceControl(AlignmentStore Store) {
		
		super(DistanceMeasureManager.values().toArray());
		store=Store;

		updateSelection();

	    addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				DistanceMeasurePlugin dm; 
				dm = ((DistanceMeasurePlugin)getSelectedItem());
				if (store!=null)
					store.setScoringFunction(dm);
			}
	    	
	    });
	    
	    setPreferredSize(null);
	}	
	
	public void updateSelection() {
		AlignmentStore hiddenstore = store; 
		store=null;
		for (int i=0; i!=getItemCount(); ++i) {
			DistanceMeasurePlugin dmt = (DistanceMeasurePlugin)(getItemAt(i));
			if (dmt.getClass().equals(hiddenstore.getScoringFunction().getClass())) {
				setSelectedIndex(i);
				break;
			}
		}	
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
