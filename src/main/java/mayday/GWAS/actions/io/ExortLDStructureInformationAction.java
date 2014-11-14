package mayday.GWAS.actions.io;

import java.awt.event.ActionEvent;
import java.io.File;

import mayday.GWAS.actions.RevealAction;
import mayday.GWAS.actions.RevealTask;
import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.ProjectHandler;
import mayday.GWAS.io.LDStructureParser;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.PathSetting;

@SuppressWarnings("serial")
public class ExortLDStructureInformationAction extends RevealAction {
	
	public ExortLDStructureInformationAction(ProjectHandler projectHandler) {
		super(projectHandler);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		PathSetting path = new PathSetting("LDS file", null, null, false, false, true);
		
		SettingDialog sd = new SettingDialog(null, "Select output file", path);
		sd.showAsInputDialog();
		
		if(!sd.closedWithOK()) {
			return;
		}
		
		final String filePath = path.getStringValue();
		
		RevealTask t = new RevealTask("Export LDS Information", projectHandler) {

			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				DataStorage ds = projectHandler.getSelectedProject();
				LDStructureParser p = new LDStructureParser(ds);
				writeLog("Writing information to file...\n");
				p.write(new File(filePath), this);
				
				if(!hasBeenCancelled()) {
					reportCurrentFractionalProgressStatus(1.0);
				}
				writeLog("Done");
			}
		};
		
		t.start();
	}
}
