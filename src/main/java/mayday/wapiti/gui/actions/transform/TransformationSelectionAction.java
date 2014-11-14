package mayday.wapiti.gui.actions.transform;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.base.Transformation;

@SuppressWarnings("serial")
public class TransformationSelectionAction extends AbstractAction {

	private final Transformation t;
	private final SelectionModel selection;

	public TransformationSelectionAction(Transformation transformation, SelectionModel sm) {
		super("Select all experiments in this transformation");
		t = transformation;
		selection = sm;
	}
	
	public void actionPerformed(ActionEvent e) {
		selection.clearSelection();
		for (Experiment ex : t.getTransMatrix().getExperiments(t))
			selection.setSelected(ex.getGUIElement(), true);
	}

}