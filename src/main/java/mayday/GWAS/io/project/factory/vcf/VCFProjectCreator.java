package mayday.GWAS.io.project.factory.vcf;

import java.io.File;
import java.util.List;

import mayday.GWAS.actions.RevealTask;
import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.ProjectHandler;
import mayday.GWAS.io.project.factory.ProjectCreator;
import mayday.GWAS.io.vcf.VCFJoiner;
import mayday.GWAS.io.vcf.VCFParser;
import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;

public class VCFProjectCreator implements ProjectCreator {

	private VCFProjectSetting setting;
	
	public VCFProjectCreator(VCFProjectSetting setting) {
		this.setting = setting;
	}
	
	@Override
	public void createNewProject(ProjectHandler projectHandler) {
		RevealTask newProjectTask = new RevealTask("Create a new project from VCF", projectHandler) {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				if(!hasBeenCancelled())
					reportCurrentFractionalProgressStatus(0.0);
				
				List<String> vcfFilesNames = setting.getVCFFileNames();
				
				//TODO implement VCF File join
				
				VCFJoiner joiner = new VCFJoiner();
				joiner.join(vcfFilesNames);
				System.out.println(joiner.toString());
				
				File vcf = joiner.getJoinedFile();
				
				writeLog("Initializing data structures\n");
				
				DataSet ds = new DataSet(setting.getProjectName());
				
				DataStorage dataStorage = new DataStorage(projectHandler);
				dataStorage.getAttribute().setName(setting.getProjectName());
				dataStorage.setDataSet(ds);
				
				writeLog("Reading input files\n");
				VCFParser parser = new VCFParser();
				parser.setProjectHandler(projectHandler);
				parser.setProject(dataStorage);
				parser.read(vcf);
				
				if(!hasBeenCancelled()) {
					reportCurrentFractionalProgressStatus(1.0);
					writeLog("Finished reading input files\n");
				}
				
				//add project to project handler
				projectHandler.add(dataStorage);
				
				MasterTable masterTable = ds.getMasterTable();
				ProbeList global = masterTable.createGlobalProbeList(true);
				ds.getProbeListManager().addObject(global);
				DataSetManager.singleInstance.addObject(ds);
				
				projectHandler.setupViewModel(dataStorage);
				
				writeLog("Done");
			}
		};
		
		newProjectTask.start();
	}
}
