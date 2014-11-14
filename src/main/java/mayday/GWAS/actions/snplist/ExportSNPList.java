package mayday.GWAS.actions.snplist;

import java.io.File;
import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.GWAS.actions.RevealTask;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.io.vcf.VCFParser;
import mayday.GWAS.settings.SubjectListSetting;
import mayday.GWAS.utilities.SNPLists;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.PathSetting;

public class ExportSNPList extends SNPListPlugin {

	@Override
	public String getName() {
		return "Export SNPList";
	}

	@Override
	public String getType() {
		return "data.snplist.export";
	}

	@Override
	public String getDescription() {
		return "Export a specified SNPList to a user defined file format";
	}

	@Override
	public String getMenuName() {
		return "Export";
	}

	@Override
	public void run(Collection<SNPList> snpLists) {
		HierarchicalSetting exportSNPsSetting = new HierarchicalSetting("Export SNPs Setting");
		
		PathSetting filePath = new PathSetting("File", "Select the file path for export", null, false, false, true);
		SubjectListSetting personSetting = new SubjectListSetting(projectHandler.getSelectedProject().getSubjects());
		
		exportSNPsSetting.addSetting(filePath);
		exportSNPsSetting.addSetting(personSetting);
		
		SettingDialog sd = new SettingDialog(null, "Export SNPs ...", exportSNPsSetting);
		sd.showAsInputDialog();
		
		if(sd.closedWithOK()) {
			SNPList allSNPs = SNPLists.createUniqueSNPList(snpLists);
			final VCFParser parser = new VCFParser();
			parser.setProject(projectHandler.getSelectedProject());
			parser.setSNPs(allSNPs);
			parser.setPersons(personSetting.getSelectedSubjects());
			
			//check file ending
			String file = filePath.getStringValue();
			//correct the file ending if necessary
			if(!file.toLowerCase().endsWith(".vcf")) {
				file += ".vcf";
			}
			
			final File output = new File(file);
			boolean doWork = true;
			
			if(output.exists()) {
				int approve = JOptionPane.showConfirmDialog(null, "File does already exist. Override?");
				if(approve != JOptionPane.OK_OPTION) {
					doWork = false;
				}
			}
			
			if(doWork) {
				RevealTask t = new RevealTask("Export SNPs", projectHandler) {
					@Override
					protected void initialize() {}

					@Override
					protected void doWork() throws Exception {
						parser.write(output);
					}
				};
				
				t.start();
			}
		}
	}
}
