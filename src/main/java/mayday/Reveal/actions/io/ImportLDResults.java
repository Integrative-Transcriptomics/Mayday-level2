package mayday.Reveal.actions.io;

import java.io.File;
import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.Reveal.RevealPlugin;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.io.LDParser;
import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.tasks.AbstractTask;

public class ImportLDResults extends RevealPlugin {

	@Override
	public String getName() {
		return "Import LD Results";
	}

	@Override
	public String getType() {
		return "project.loadLD";
	}

	@Override
	public String getDescription() {
		return "Import precalculated LD results";
	}

	@Override
	public String getMenuName() {
		return "Load LD results";
	}

	@Override
	public void run(Collection<SNPList> snpLists) {
		if(projectHandler.getSelectedProject() == null) {
			JOptionPane.showMessageDialog(null, "No project has been selected!");
			return;
		}
		
		HierarchicalSetting loadLDSetting = new HierarchicalSetting("Load LD results setting");
		
		PathSetting ldFileLocation = new PathSetting("LD file", "A file containing precalculated LD results in PLINK LD file format", null, false, true, false);
		loadLDSetting.addSetting(ldFileLocation);
		
		SettingDialog sd = new SettingDialog(null, "Load LD results", loadLDSetting);
		sd.showAsInputDialog();
		
		//canceled by the user
		if(!sd.closedWithOK()) {
			return;
		}
		
		//not canceled!
		
		final File input = new File(ldFileLocation.getStringValue());
		
		if(input.canRead()) {
			AbstractTask task = new AbstractTask("Import LD results") {
				@Override
				protected void initialize() {}

				@Override
				protected void doWork() throws Exception {
					LDParser parser = new LDParser(projectHandler.getSelectedProject());
					parser.read(input, this);
				}
			};
			
			task.start();
		}
	}

	@Override
	public String getMenu() {
		return RevealMenuConstants.META_INFORMATION+"/Linkage disequilibrium";
	}

	@Override
	public String getCategory() {
		return "Project/Meta-Information";
	}
}
