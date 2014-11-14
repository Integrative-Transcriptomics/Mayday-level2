package mayday.tiala.pairwise.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.tiala.pairwise.data.AlignmentStore;

@SuppressWarnings("serial")
public class Statistics2MIOAction extends AbstractAction {

	AlignmentStore Store;

	public Statistics2MIOAction() {
		super("...as meta information");
	}

	public Statistics2MIOAction(AlignmentStore store) {
		this();
		setStore(store);
	}

	public void setStore(AlignmentStore store) {
		Store = store;
	}

	public void actionPerformed(ActionEvent e) {
		if (Store==null) 
			return;
		Store.getProbeStatistic().createMIOs();
	}

}
