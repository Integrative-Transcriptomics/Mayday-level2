package mayday.wapiti.gui.actions.transform;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.gui.ExperimentPanel;
import mayday.wapiti.gui.layeredpane.ReorderableHorizontalPanel;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.base.Transformation;

@SuppressWarnings("serial")
public class TransformationExtendAction extends AbstractAction {

	private final Transformation t;
	private final SelectionModel selection;

	public TransformationExtendAction(Transformation transformation, SelectionModel sm) {
		super("Add selected experiments to this transformation");
		t = transformation;
		selection = sm;
	}
	
	public void actionPerformed(ActionEvent e) {
		List<Experiment> le = new LinkedList<Experiment>();
		for (ReorderableHorizontalPanel rhp : selection.getSelection())
			le.add(((ExperimentPanel)rhp).getExperiment());
		t.getTransMatrix().addTransformation(t, le);
	}

}