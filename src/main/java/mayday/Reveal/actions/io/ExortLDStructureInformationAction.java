package mayday.Reveal.actions.io;

import java.awt.event.ActionEvent;
import java.io.File;

import mayday.Reveal.actions.RevealAction;
import mayday.Reveal.actions.RevealTask;
import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.io.LDStructureParser;
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
