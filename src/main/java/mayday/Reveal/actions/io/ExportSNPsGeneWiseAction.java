package mayday.Reveal.actions.io;

import java.awt.event.ActionEvent;
import java.io.File;

import mayday.Reveal.actions.RevealAction;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.io.SNPExporter;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.PathSetting;

@SuppressWarnings("serial")
public class ExportSNPsGeneWiseAction extends RevealAction {
	
	public ExportSNPsGeneWiseAction(ProjectHandler projectHandler) {
		super(projectHandler);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		PathSetting destinationFolder = new PathSetting("Destination Folder", null, null, true, true, true);
		BooleanSetting overwriteExisting = new BooleanSetting("Overwrite existing files", null, true);
		
		HierarchicalSetting settings = new HierarchicalSetting("Export SNPs gene wise ...");
		settings.addSetting(destinationFolder);
		settings.addSetting(overwriteExisting);
		
		SettingDialog dialog = new SettingDialog(null, "Export SNPs gene wise", settings);
		dialog.showAsInputDialog();
		
		if(!dialog.closedWithOK()) {
			return;
		}
		
		File folder = new File(destinationFolder.getStringValue());
		boolean overwrite = overwriteExisting.getBooleanValue();
		
		SNPExporter exporter = new SNPExporter(projectHandler.getSelectedProject());
		exporter.exportSNPsGeneWise(folder, overwrite);
	}
}
