package it.genomering.gui;

import it.genomering.optimize.ManualSuperGenomeOptimizer;
import it.genomering.render.RingDimensions;
import it.genomering.structure.SuperGenome;
import it.genomering.visconnect.ConnectionManager;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class OptimizeManuallyAction extends AbstractAction {
	
	ManualSuperGenomeOptimizer optimizer;
	
	/**
	 * @param superGenome
	 * @param ringdim
	 * @param cm
	 */
	public OptimizeManuallyAction(SuperGenome superGenome,
			RingDimensions ringdim, ConnectionManager cm) {	
		super("Order blocks manually");
		this.optimizer = new ManualSuperGenomeOptimizer(superGenome);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.optimizer.start();
	}
}
