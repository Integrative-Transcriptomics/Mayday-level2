package mayday.tiala.multi.gui.controls;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;
import mayday.tiala.multi.data.AlignmentStoreListener;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class OnlyMatchingControl extends JPanel implements AlignmentStoreListener, ActionListener {

	JRadioButton all = new JRadioButton("Use all experiments");
	JRadioButton mat = new JRadioButton("Use matched experiments");
	
	AlignmentStore store;
	
	/**
	 * @param store
	 */
	public OnlyMatchingControl( AlignmentStore store ) {
		setLayout(new GridLayout(0,1));
		this.store = store;
		add(all);
		add(mat);
		ButtonGroup bg = new ButtonGroup();
		bg.add(all);
		bg.add(mat);
		update();
		mat.addActionListener(this);
		all.addActionListener(this);
	}
	
	/**
	 * 
	 */
	public void update() {
		mat.setSelected(store.getSettings().showOnlyMatching());
		all.setSelected(!mat.isSelected());
	}
	
	final JCheckBox showMatching = new JCheckBox("Use only matched experiments");

	public void alignmentChanged(AlignmentStoreEvent evt) {
		switch(evt.getChange()) {
		case AlignmentStoreEvent.STORE_CLOSED:
			store.removeListener(this);
			break;
		default:
				update();
		}
	}

	public void actionPerformed(ActionEvent e) {
		store.getSettings().setShowOnlyMatching(mat.isSelected());
	}
}
