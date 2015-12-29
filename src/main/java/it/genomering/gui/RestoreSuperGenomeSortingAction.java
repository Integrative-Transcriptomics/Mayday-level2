package it.genomering.gui;

import it.genomering.render.RingDimensions;
import it.genomering.structure.SuperGenome;
import it.genomering.visconnect.ConnectionManager;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class RestoreSuperGenomeSortingAction extends AbstractAction {

	SuperGenome sg;
	RingDimensions rd;
	ConnectionManager cm;
	
	public RestoreSuperGenomeSortingAction(SuperGenome sg, RingDimensions rd, ConnectionManager cm) {
		super("Restore initial order");
		this.sg =sg;
		this.rd = rd;
		this.cm = cm;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		sg.setBlocks(sg.getInitialBlockOrder());
	}
}
