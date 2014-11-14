package mayday.tiala.multi.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.tiala.multi.data.AlignmentStore;

@SuppressWarnings("serial")
public class Statistics2MIOAction extends AbstractAction {

	AlignmentStore Store;
	protected int ID;

	public Statistics2MIOAction(int ID) {
		super("...as meta information");
		this.ID = ID;
	}

	public Statistics2MIOAction(int ID, AlignmentStore store) {
		this(ID);
		setStore(store);
	}

	public void setStore(AlignmentStore store) {
		Store = store;
	}

	public void actionPerformed(ActionEvent e) {
		if (Store==null) 
			return;
		Store.getProbeStatistic(ID).createMIOs();
	}
}
