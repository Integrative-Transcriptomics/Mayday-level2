package mayday.wapiti.gui.actions.transform;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.core.settings.generic.PluginInstanceSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;
import mayday.wapiti.transformations.base.Transformation;
import mayday.wapiti.transformations.matrix.SchizophrenicExperiment;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class TransformationReplaceAction extends AbstractAction {

	private final Transformation t;

	public TransformationReplaceAction(Transformation transformation) {
		super("Replace transformation");
		t = transformation;
	}
	
	public void actionPerformed(ActionEvent e) {
		TransMatrix real = t.getTransMatrix();
		TransMatrix fake = t.getTransMatrix().happy_clone();
		
		int insertPosition = real.getMinimumIndex(t);
		
		for (Experiment ex : real.getExperiments()) {
			for (Transformation trans : real.getTransformations(ex)) {
				if (fake.getTransformations().contains(trans)) {
					int thisPosition = real.getMinimumIndex(trans);
					if (insertPosition<=thisPosition) {
						fake.remove_noUte(trans);
					}
				}
			}
		}
		
		List<Experiment> le_real = real.getExperiments(t);
		List<Experiment> le_fake = new LinkedList<Experiment>();
		for (Experiment ex : le_real) {
			le_fake.add(new SchizophrenicExperiment(ex, fake));
		}

//		System.out.println(fake.toString());
		
		PluginInstanceSetting<AbstractTransformationPlugin> pls = 
			AddTransformationAction.showTransformationSettingDialog(le_fake, "Select replacement transformation");
		if (pls!=null)
			t.getTransMatrix().replaceTransformation(t, pls.getInstance());
	}

}