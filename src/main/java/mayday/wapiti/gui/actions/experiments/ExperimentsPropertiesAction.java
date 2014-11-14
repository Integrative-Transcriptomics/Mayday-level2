
package mayday.wapiti.gui.actions.experiments;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentSetting;
import mayday.wapiti.gui.ExperimentPanel;
import mayday.wapiti.gui.layeredpane.ReorderableHorizontalPanel;
import mayday.wapiti.gui.layeredpane.SelectionModel;

@SuppressWarnings("serial")
public class ExperimentsPropertiesAction extends AbstractAction {

	private final SelectionModel selection;

	public ExperimentsPropertiesAction(SelectionModel sm) {
		super("Properties");
		this.selection = sm;
	}

	public void actionPerformed(ActionEvent e) {
		LinkedList<Experiment> exps = new LinkedList<Experiment>();
		for (ReorderableHorizontalPanel rhp : selection.getSelection())
			exps.add(((ExperimentPanel)rhp).getExperiment());
		showDialog(exps);
	}
	
	public static void showDialog( List<Experiment> experiments) {
		showDialog(experiments, true);
	}
	
	public static void showDialog( List<Experiment> experiments, boolean verbose) {
		
		if (experiments.size()>1) {
			
			MultiHashMap<Class<? extends Setting>, Setting> byClass = new MultiHashMap<Class<? extends Setting>, Setting>();
			for (Experiment e : experiments) {
				Setting s = e.getSetting();
				if (s!=null) {
					byClass.put(s.getClass(), s);				
				}
			}
			
			HierarchicalSetting combined = new HierarchicalSetting("Experiment settings")
			.setLayoutStyle(HierarchicalSetting.LayoutStyle.TABBED)
			.setTopMost(true);
			
			for (Class<? extends Setting> c : byClass.keySet()) {
				List<Setting> sameClass = byClass.get(c);
				Setting s = sameClass.get(0).clone();
				if (s instanceof ExperimentSetting) {
					((ExperimentSetting)s).reduce();					
				}
				if (!((ExperimentSetting)s).nothingLeft())
					combined.addSetting(s);
			}
				
			if (combined.getChildren().size()>0)
				new SettingDialog(null, "Common settings for "+experiments.size()+" experiments", combined).showAsInputDialog();
			else if (verbose)
				JOptionPane.showMessageDialog(null, 
						"Selected experiments have no common properties. ", 
						"No properties to modify.", 
						JOptionPane.ERROR_MESSAGE);
			
			// now copy the settings to all experiments of the same type
			for (Setting s : combined.getChildren()) {
				for (Setting s2 : byClass.get(s.getClass())) {
					s2.fromPrefNode(s.toPrefNode());
				}
			}
		} else if (experiments.size()==1) {
			new SettingDialog(null, "Settings for "+experiments.get(0).getName(), experiments.get(0).getSetting()).showAsInputDialog();
		} else if (verbose) {
			JOptionPane.showMessageDialog(null, 
					"Please select experiments.", 
					"Selection required", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		

	}
}