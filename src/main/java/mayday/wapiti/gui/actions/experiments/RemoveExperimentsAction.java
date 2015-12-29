
package mayday.wapiti.gui.actions.experiments;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.gui.ExperimentPanel;
import mayday.wapiti.gui.layeredpane.ReorderableHorizontalPanel;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class RemoveExperimentsAction extends AbstractAction {

	private final TransMatrix transMatrix;
	private final SelectionModel selection;

	public RemoveExperimentsAction(TransMatrix transMatrix, SelectionModel sm) {
		super("Remove Experiments");
		this.transMatrix = transMatrix;
		this.selection = sm;
	}

	public void actionPerformed(ActionEvent e) {
		List<Experiment> le = new LinkedList<Experiment>();
		for (ReorderableHorizontalPanel rhp : selection.getSelection())
			le.add(((ExperimentPanel)rhp).getExperiment());
		if (le.size()>0) {
			if (JOptionPane.showConfirmDialog(
					transMatrix.getFrame(), 
					"Remove "+le.size()+" selected experiment(s)?",
					"Confirm removal",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null)
					== JOptionPane.YES_OPTION) {
				transMatrix.remove(le);
			}
		} else {
			JOptionPane.showMessageDialog(null, 
					"Please select experiments to remove.", 
					"Selection required", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
}