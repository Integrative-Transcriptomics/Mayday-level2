
package mayday.wapiti.gui.actions.locus;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.tasks.AbstractTask;
import mayday.wapiti.containers.loci.peakfind.PeakFinder;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class LocusDataFromReadsAction extends AbstractAction {

	
	private final TransMatrix transMatrix;
//	private final SelectionModel selection;

	public LocusDataFromReadsAction(TransMatrix transMatrix, SelectionModel sm) {
		super("Peak finder");
		this.transMatrix = transMatrix;
//		this.selection = sm;
	}

	public void actionPerformed(ActionEvent e) {
		new AbstractTask("Finding peaks...") { 
			public void doWork() {
				PeakFinder.run(transMatrix, this);	
			}

			protected void initialize() {
			}
		}.start();
	}
}