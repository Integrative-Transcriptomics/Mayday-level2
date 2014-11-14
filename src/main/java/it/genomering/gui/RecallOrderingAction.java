package it.genomering.gui;

import it.genomering.gui.RememberOrderingAction.RememberedOrdering;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class RecallOrderingAction extends AbstractAction {

	RememberedOrdering ro;
	
	/**
	 * @param sg
	 * @param ordering
	 */
	public RecallOrderingAction(RememberedOrdering ro) {
		super("Restore  "+ro.getName());
		this.ro=ro;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ro.restore();
		System.out.println("Ordering restored");
	}
}
