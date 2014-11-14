package mayday.GWAS.actions.io;

import java.io.File;
import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.GWAS.RevealPlugin;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.data.meta.Genome;
import mayday.GWAS.utilities.RevealMenuConstants;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.tasks.AbstractTask;

public class LoadGenomeAction extends RevealPlugin {

	@Override
	public String getName() {
		return "Load Genome";
	}

	@Override
	public String getType() {
		return "project.loadGenome";
	}

	@Override
	public String getDescription() {
		return "Create a genome index from a fasta file";
	}

	@Override
	public String getMenuName() {
		return "Load Genome";
	}

	@Override
	public void run(Collection<SNPList> snpLists) {
		if(projectHandler.getSelectedProject() == null) {
			JOptionPane.showMessageDialog(null, "No project has been selected!");
			return;
		}
		
		HierarchicalSetting loadGenomeSetting = new HierarchicalSetting("Load Genome Settings");
		
		PathSetting genomeFilePath = new PathSetting("Genome File", "A FastA file representing the genome", null, false, true, false);
//		BooleanSetting updateSNPsSetting = new BooleanSetting("Update SNPs", "Update SNP reference nucleotides according to the new genome", false);
		
		loadGenomeSetting.addSetting(genomeFilePath);
//		loadGenomeSetting.addSetting(updateSNPsSetting);
		
		SettingDialog sd = new SettingDialog(null, "Load Genome ...", loadGenomeSetting);
		sd.showAsInputDialog();
		
		if(sd.closedWithOK()) {
			final File input = new File(genomeFilePath.getStringValue());
//			final boolean updateSNPs = updateSNPsSetting.getBooleanValue();
			
			if(input.canRead()) {
				AbstractTask task = new AbstractTask("Loading Genome") {
					@Override
					protected void initialize() {}

					@Override
					protected void doWork() throws Exception {
						Genome index = new Genome(input.getAbsolutePath());
						writeLog("Loading genome from file:\n\t" + input.getAbsolutePath() + "\n");
						
						boolean success = index.createIndex(this);
						if(success) {
							writeLog("Finished index creation!\n");
							
//							if(updateSNPs) {
//								index.setDataStorage(projectHandler.getSelectedProject());
//								setProgress(0, "Updating SNP reference nucleotides ...");
//								
//								writeLog("Depending on the number of SNPs in your project,\n\t" +
//										"the update can take several minutes.\n\t" +
//										"Please be patient ...\n");
//								//always update all snps in the project
//								index.updateReferenceNucleotides();
//							}
							
							projectHandler.addGenome(index);
							projectHandler.getSelectedProject().setGenome(index);
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
