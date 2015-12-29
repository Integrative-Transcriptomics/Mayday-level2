
package mayday.wapiti.gui.actions.locus;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.wapiti.containers.loci.merging.LocusMerging;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class MergeLocusDataAction extends AbstractAction {

	
	private final TransMatrix transMatrix;
//	private final SelectionModel selection;

	public MergeLocusDataAction(TransMatrix transMatrix, SelectionModel sm) {
		super("Merge Locus Data");
		this.transMatrix = transMatrix;
//		this.selection = sm;
	}

	public void actionPerformed(ActionEvent e) {
		LocusMerging.run(transMatrix);
	}
}