package mayday.tiala.pairwise.gui.controls;

import java.awt.Dimension;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.data.AlignmentStoreEvent;
import mayday.tiala.pairwise.data.AlignmentStoreListener;

@SuppressWarnings("serial")
public class TimeshiftControl extends JSpinner implements AlignmentStoreListener {
	
	protected AlignmentStore store;
	
	public TimeshiftControl(AlignmentStore Store) {	

		super(new AlignmentSpinnerModel(Store));
		
		store=Store;

		((JFormattedTextField)((JSpinner.DefaultEditor)getEditor()).getTextField()).setEditable(true);
		Dimension dim = getPreferredSize();
		dim.width=40;
		setPreferredSize(dim);

		Dimension dim2 = new Dimension(dim);
		dim2.width=Integer.MAX_VALUE;
		setMaximumSize(dim2); // restrict height
		
		addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				if (store!=null)
					store.setTimeShift((Double)getValue());
			}
			
		});
	}
	
	public void updateSelection() {
		AlignmentStore hiddenstore = store; 
		store=null;
		setValue(hiddenstore.getTimeShift());
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

	public void alignmentChanged(AlignmentStoreEvent event) {
		if (event.getChange()==AlignmentStoreEvent.SHIFT_CHANGED) {
			updateSelection();			
		}
	}    
}
