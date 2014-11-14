package mayday.GWAS.io.project.factory.plink;

import java.io.File;
import java.util.List;

import mayday.GWAS.actions.RevealTask;
import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.Gene;
import mayday.GWAS.data.GeneList;
import mayday.GWAS.data.ProjectHandler;
import mayday.GWAS.data.Subject;
import mayday.GWAS.data.SubjectList;
import mayday.GWAS.io.PLINKFileReader;
import mayday.GWAS.io.project.factory.ProjectCreator;
import mayday.core.DataSet;
import mayday.core.Experiment;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.BooleanMIO;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.genetics.Locus;
import mayday.genetics.LocusMIO;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.GeneticCoordinate;

public class PLINKProjectCreator implements ProjectCreator {
	
	PLINKProjectSetting setting;
	
	public PLINKProjectCreator(PLINKProjectSetting setting) {
		this.setting = setting;
	}
	
	@Override
	public void createNewProject(ProjectHandler projectHandler) {
		RevealTask newProjectTask = new RevealTask("Create a new project from PLINK", projectHandler) {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				if(!hasBeenCancelled())
					reportCurrentFractionalProgressStatus(0.0);
		
				File pedFile = new File(setting.getHaplotypesFile().getStringValue());
				File mapFile = new File(setting.getMapFileSetting().getStringValue());
				File refFile = new File(setting.getReferencesFile().getStringValue());
				File geneLocFile = new File(setting.getGeneLocationsFile().getStringValue());
				File phenoFile = new File(setting.getPhenotypesFile().getStringValue());
				
				List<String> slrFileNames = setting.getSingleLocusFiles().getFileNames();
				File[] slrFiles = new File[slrFileNames.size()];
				for(int i = 0; i < slrFiles.length; i++) {
					slrFiles[i] = new File(slrFileNames.get(i));
				}
				
				List<String> tlrFileNames = setting.getTwoLocusFiles().getFileNames();
				File[] tlrFiles = new File[tlrFileNames.size()];
				for(int i = 0; i < tlrFiles.length; i++) {
					tlrFiles[i] = new File(tlrFileNames.get(i));
				}
				
				DataSet ds = new DataSet(setting.getProjectName().getStringValue());
				MasterTable masterTable = ds.getMasterTable();
				
				PLINKFileReader plinkfr = new PLINKFileReader(masterTable, projectHandler);
				HierarchicalSetting fileFormatSettings = plinkfr.getSettings();
				
				SettingDialog fd = new SettingDialog(null, "Set file formats ...", fileFormatSettings);
				fd.showAsInputDialog();
				
				if(!fd.closedWithOK()) {
					return;
				}
				
				DataStorage data = plinkfr.readPLINKData(pedFile, mapFile, phenoFile, refFile, geneLocFile, slrFiles, tlrFiles, null);
				data.getAttribute().setName(setting.getProjectName().getStringValue());
				data.setDataSet(ds);
				
				SubjectList persons = data.getSubjects();
				
				//Creating gene location MIO objects
				
				MIGroup affection = ds.getMIManager().newGroup(BooleanMIO.myType, "Affection");
				MIGroup geneLocation = ds.getMIManager().newGroup(LocusMIO.myType, "Gene Location");
				
				for(Subject p : persons) {
					Experiment ex = new Experiment(masterTable, p.getID()+"");
					masterTable.addExperiment(ex);
					BooleanMIO affected = (BooleanMIO)affection.add(ex);
					affected.setValue(p.affected());
				}
				
				final ChromosomeSetContainer csc = ChromosomeSetContainer.getDefault();
		
				GeneList genes = data.getGenes();
				for(Probe gene : genes) {
					Gene g = (Gene)gene;
					masterTable.addProbe(g);
					
					String chromosomeName = g.getChromosome();
					Chromosome chromosome = csc.getChromosome(SpeciesContainer.getSpecies("Homo sapiens"), chromosomeName);
					
					if(chromosomeName != null) {
						LocusMIO lm = (LocusMIO)geneLocation.add(gene);
						lm.setValue(new Locus(new GeneticCoordinate(chromosome, Strand.UNSPECIFIED, g.getStartPosition(), g.getStopPosition())));
					}
				}
				
				//remove group if it is empty! no gene location information is provided
				if(geneLocation.size() == 0) {
					ds.getMIManager().removeGroup(geneLocation);
				}
				
				ProbeList global = masterTable.createGlobalProbeList(true);
				ds.getProbeListManager().addObject(global);
				DataSetManager.singleInstance.addObject(ds);
				
				projectHandler.add(data);
				projectHandler.setupViewModel(data);
			}
		};
		
		newProjectTask.start();
	}
}
