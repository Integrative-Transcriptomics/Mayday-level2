package mayday.tiala.multi.gui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;
import mayday.tiala.multi.data.AlignmentStoreListener;

/**
 * 
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class ScoreDistanceControl extends JComboBox implements AlignmentStoreListener {
	
	protected AlignmentStore store;
	protected final int number;
	
	public ScoreDistanceControl(final int number, AlignmentStore Store) {
		super(DistanceMeasureManager.values().toArray());
		
		store=Store;
		this.number = number;

		updateSelection();

	    addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DistanceMeasurePlugin dm; 
				dm = ((DistanceMeasurePlugin)getSelectedItem());
				if (store!=null)
					store.setScoringFunction(number, dm);
			}
	    });
	    
	    setPreferredSize(null);
	}
	
	public void updateSelection() {
		AlignmentStore hiddenstore = store; 
		store=null;
		for (int i=0; i!=getItemCount(); ++i) {
			DistanceMeasurePlugin dmt = (DistanceMeasurePlugin)(getItemAt(i));
			if (dmt.getClass().equals(hiddenstore.getScoringFunction(number).getClass())) {
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
