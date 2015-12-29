package mayday.wapiti.gui.actions.transform;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import mayday.core.settings.generic.PluginInstanceSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.gui.ExperimentPanel;
import mayday.wapiti.gui.layeredpane.ReorderableHorizontalPanel;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;
import mayday.wapiti.transformations.base.Transformation;
import mayday.wapiti.transformations.matrix.SchizophrenicExperiment;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class TransformationInsertAction extends AbstractAction {

	private final Transformation t;
	private final SelectionModel sm;

	public TransformationInsertAction(Transformation transformation, SelectionModel selectionModel) {
		super("Insert another transformation before this one");
		t = transformation;
		sm = selectionModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (sm.size()>0) {
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

//			System.out.println(fake.toString());
			
			LinkedList<Experiment> le = new LinkedList<Experiment>();
			for (ReorderableHorizontalPanel rhp : sm.getSelection()) {
				le.add(((ExperimentPanel)rhp).getExperiment());
			}
			
			PluginInstanceSetting<AbstractTransformationPlugin> pls = 
				AddTransformationAction.showTransformationSettingDialog(le_fake, "Select transformation to insert");
			if (pls!=null)
				t.getTransMatrix().insertTransformation(t, pls.getInstance(), le);
		} else {
			JOptionPane.showMessageDialog(null, 
					"Please select which experiments the transformation should be inserted into.", 
					"Unable to insert transformation", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

}