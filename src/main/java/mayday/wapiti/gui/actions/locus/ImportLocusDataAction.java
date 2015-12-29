
package mayday.wapiti.gui.actions.locus;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.genetics.importer.LocusImport;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class ImportLocusDataAction extends AbstractAction {

	
//	private final TransMatrix transMatrix;
//	private final SelectionModel selection;

	public ImportLocusDataAction(TransMatrix transMatrix, SelectionModel sm) {
		super("Import/Create Locus Data");
//		this.transMatrix = transMatrix;
//		this.selection = sm;
	}

	public void actionPerformed(ActionEvent e) {
		LocusImport.run();
	}
}