package mayday.tiala.multi.gui.controls;

import java.awt.Dimension;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;
import mayday.tiala.multi.data.AlignmentStoreListener;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class TimeshiftControl extends JSpinner implements AlignmentStoreListener {
	
	protected AlignmentStore store;
	protected final int index;
	
	/**
	 * @param Store
	 * @param index
	 */
	public TimeshiftControl(AlignmentStore Store, final int index) {
		super(new AlignmentSpinnerModel(Store, index));
		
		this.index = index;
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
				if (store!=null) {
					store.setTimeShift(index, (Double)getValue());
				}
			}
		});
	}
	
	/**
	 * 
	 */
	public void updateSelection() {
		AlignmentStore hiddenstore = store; 
		store=null;
		setValue(hiddenstore.getTimeShifts().get(index));
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
		case AlignmentStoreEvent.SHIFT_CHANGED:
			updateSelection();
			break;
		case AlignmentStoreEvent.STORE_CLOSED:
			store.removeListener(this);
			break;
		}
	}    
}
