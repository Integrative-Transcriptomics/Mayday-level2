
package mayday.wapiti.gui.actions.locus;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.wapiti.containers.loci.filter.LocusFilter;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class FilterLocusDataAction extends AbstractAction {

	
	private final TransMatrix transMatrix;
//	private final SelectionModel selection;

	public FilterLocusDataAction(TransMatrix transMatrix, SelectionModel sm) {
		super("Filter Locus Data");
		this.transMatrix = transMatrix;
//		this.selection = sm;
	}

	public void actionPerformed(ActionEvent e) {
		new LocusFilter().run(transMatrix);
	}
}