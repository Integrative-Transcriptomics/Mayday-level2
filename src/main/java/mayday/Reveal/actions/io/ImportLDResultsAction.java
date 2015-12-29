package mayday.Reveal.actions.io;

import java.awt.event.ActionEvent;
import java.io.File;

import mayday.Reveal.actions.CalculateLDStructure;
import mayday.Reveal.actions.RevealAction;
import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.io.LDParser;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.tasks.AbstractTask;

@SuppressWarnings("serial")
public class ImportLDResultsAction extends RevealAction {
	
	public ImportLDResultsAction(ProjectHandler projectHandler) {
		super(projectHandler);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final PathSetting sourceFile = new PathSetting("Select LD file", null, null, false, true, false);
		final BooleanSetting calculateLDStructure = new BooleanSetting("Calculate LD Structure", null, true);
		
		HierarchicalSetting fileSettings = new HierarchicalSetting("Select LD file");
		fileSettings.addSetting(sourceFile);
		fileSettings.addSetting(calculateLDStructure);
		
		final CalculateLDStructure calcLD = new CalculateLDStructure(projectHandler);
		Setting parserSetting = calcLD.getSetting();
		
		HierarchicalSetting settings = new HierarchicalSetting("Import LD results Setting");
		settings.addSetting(fileSettings);
		settings.addSetting(parserSetting);
		
		SettingDialog dialog = new SettingDialog(null, "Import LD results", settings);
		dialog.showAsInputDialog();
		
		if(!dialog.closedWithOK()) {
			return;
		}
		
		AbstractTask importTask = new AbstractTask("Import LD results") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				File inputFile = new File(sourceFile.getStringValue());
				boolean calculateLD = calculateLDStructure.getBooleanValue();
				
				DataStorage ds = projectHandler.getSelectedProject();
				LDParser parser = new LDParser(ds);
				parser.read(inputFile);
				
				if(calculateLD) {
					calcLD.disableSetting();
					calcLD.actionPerformed(null);
				}
			}
		};
		importTask.start();
	}
}
