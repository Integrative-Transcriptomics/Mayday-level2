package mayday.wapiti.gui.actions.experiments;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.wapiti.experiments.importer.ExperimentImport;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class AddExperimentsAction extends AbstractAction {

	private final TransMatrix transMatrix;

	public AddExperimentsAction(TransMatrix transMatrix) {
		super("Add Experiments");
		this.transMatrix = transMatrix;
	}

	public void actionPerformed(ActionEvent evt) {
		
		try {
			ExperimentImport.run(transMatrix);
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
//		
//		PluginTypeSetting<ExperimentImportPlugin> pls = new PluginTypeSetting<ExperimentImportPlugin>(
//				"Parser",
//				"Select which plugin should parse the new experiments",
//				new LegacyParser(), 
//				ExperimentImportPlugin.MC
//		);
//		
//		HierarchicalSetting hs = new HierarchicalSetting("Experiment parser")
//		.addSetting(pls)
//		.setTopMost(true);
//		
//		SettingDialog sd = new SettingDialog(null, "Add experiment", hs);
//		sd.getApplyButton().setVisible(false);
//		sd.setModal(true);
//		sd.setVisible(true);
//		
//		if (!sd.canceled()) {
//			ExperimentImportPlugin app = pls.getInstance();
//			app.importInto(transMatrix);
//		}

	}
	
}