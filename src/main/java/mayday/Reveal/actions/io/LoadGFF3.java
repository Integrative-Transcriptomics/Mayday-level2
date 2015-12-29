package mayday.Reveal.actions.io;

import java.io.File;
import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.Reveal.RevealPlugin;
import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.io.gff3.GFFTree;
import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.tasks.AbstractTask;

public class LoadGFF3 extends RevealPlugin {

	@Override
	public String getName() {
		return "Load GFF3 files";
	}

	@Override
	public String getType() {
		return "project.loadGFF3";
	}

	@Override
	public String getDescription() {
		return "Import genome annotations in GFF3 format";
	}

	@Override
	public String getMenuName() {
		return "Load GFF3";
	}

	@Override
	public void run(Collection<SNVList> snpLists) {
	
		if(projectHandler.getSelectedProject() == null) {
			JOptionPane.showMessageDialog(null, "No project has been selected!");
			return;
		}
		
		if(projectHandler.getSelectedProject().getGenome() == null) {
			JOptionPane.showMessageDialog(null, "No genome has been loaded!\nPlease load genome first.");
			return;
		}
		
		HierarchicalSetting loadGFF3Setting = new HierarchicalSetting("GFF3 Setting");
		
		PathSetting gff3FilePath = new PathSetting("GFF3 File", "A file in GFF3 format containing genome annotations", null, false, true, false);
		loadGFF3Setting.addSetting(gff3FilePath);
		
		SettingDialog sd = new SettingDialog(null, "Import genome annotations ...", loadGFF3Setting);
		sd.showAsInputDialog();
		
		if(sd.closedWithOK()) {
			final File input = new File(gff3FilePath.getStringValue());
			if(input.canRead()) {
				final String filePath = gff3FilePath.getStringValue();
				
				AbstractTask task = new AbstractTask("Import GFF3") {
					@Override
					protected void initialize() {}

					@Override
					protected void doWork() throws Exception {
						GFFTree tree = new GFFTree();
						tree.setDataStorage(projectHandler.getSelectedProject());
						writeLog("Starting GFF tree contstruction ...\n");
						tree.buildTree(filePath);
						
						writeLog("Adding gff3 information to project ...\n");
						
						if(!hasBeenCancelled()) {
							DataStorage ds = projectHandler.getSelectedProject();
							ds.getMetaInformationManager().add(GFFTree.MYTYPE, tree);
							
							writeLog("Done!\n");
						}
					}
				};
				
				task.start();
			}
		}
	}

	@Override
	public String getMenu() {
		return RevealMenuConstants.META_INFORMATION+"/Genome";
	}

	@Override
	public String getCategory() {
		return "Project/Meta-Information";
	}
}
