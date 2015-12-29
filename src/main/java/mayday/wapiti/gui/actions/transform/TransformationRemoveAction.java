package mayday.wapiti.gui.actions.transform;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.transformations.base.Transformation;

@SuppressWarnings("serial")
public class TransformationRemoveAction extends AbstractAction {

	private final Transformation t;
	private final Experiment e;

	public TransformationRemoveAction(Transformation transformation, Experiment experiment) {
		super(experiment==null?"Remove this transformation completely":"Remove this experiment from the transformation");
		t = transformation;
		e = experiment;
	}
	
	public void actionPerformed(ActionEvent evt) {
		if (e!=null)
			t.getTransMatrix().remove(t, e);
		else
			t.getTransMatrix().remove(t);
	}

}