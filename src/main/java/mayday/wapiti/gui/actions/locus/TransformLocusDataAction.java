
package mayday.wapiti.gui.actions.locus;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.wapiti.containers.loci.transform.LocusTransform;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class TransformLocusDataAction extends AbstractAction {

	
	private final TransMatrix transMatrix;
//	private final SelectionModel selection;

	public TransformLocusDataAction(TransMatrix transMatrix, SelectionModel sm) {
		super("Transform Locus Data");
		this.transMatrix = transMatrix;
//		this.selection = sm;
	}

	public void actionPerformed(ActionEvent e) {
		LocusTransform.run(transMatrix);
	}
}