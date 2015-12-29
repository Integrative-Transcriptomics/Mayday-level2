package mayday.wapiti.gui.actions.transform;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.gui.ExperimentPanel;
import mayday.wapiti.gui.layeredpane.ReorderableHorizontalPanel;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.base.Transformation;

@SuppressWarnings("serial")
public class TransformationRemoveSeveralAction extends AbstractAction {

	private final Transformation t;
	private final SelectionModel selection;

	public TransformationRemoveSeveralAction(Transformation transformation, SelectionModel sm) {
		super("Remove selected experiments from this transformation");
		t = transformation;
		selection = sm;
	}
	
	public void actionPerformed(ActionEvent e) {
		List<Experiment> exp = t.getTransMatrix().getExperiments(t);
		for (ReorderableHorizontalPanel rhp : selection.getSelection())
			exp.remove(((ExperimentPanel)rhp).getExperiment());
		t.getTransMatrix().setTransformation(t, exp.toArray(new Experiment[0]));		
	}

}