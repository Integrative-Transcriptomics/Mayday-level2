package it.genomering.gui;

import it.genomering.optimize.SuperGenomeOptimizer;
import it.genomering.render.RingDimensions;
import it.genomering.structure.SuperGenome;
import it.genomering.visconnect.ConnectionManager;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class OptimizeBlockAction extends AbstractAction {

	SuperGenome sg;
	RingDimensions rd;
	ConnectionManager cm;
	SuperGenomeOptimizer sgo;
	
	public OptimizeBlockAction(SuperGenome sg, RingDimensions rd, ConnectionManager cm) {
		super("Optimize number of blocks skipped");
		this.sgo = new SuperGenomeOptimizer();
		this.sg=sg;
		this.rd=rd;
		this.cm=cm;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		sgo.optimize(SuperGenomeOptimizer.SWITCH_FIRST_INSERT_LATER, SuperGenomeOptimizer.OPTIMIZE_BLOCKS, sg,rd,cm);
	}
}
