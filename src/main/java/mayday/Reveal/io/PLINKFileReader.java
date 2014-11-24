package mayday.Reveal.io;

import java.io.File;

import mayday.Reveal.actions.RevealTask;
import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.ProjectHandler;
import mayday.core.MasterTable;
import mayday.core.settings.generic.HierarchicalSetting;

/**
 * @author jaeger
 *
 */
public class PLINKFileReader {
	
	MasterTable masterTable;
	DataStorage ds;
	
	private REFParser refParser;
	private LOCParser locParser;
	private MAPParser mapParser;
	private PHENOParser phenoParser;
	private PEDParser pedParser;
	private SLRParser slrParser;
	private TLRParser tlrParser;
	
	/**
	 * @param masterTable
	 */
	public PLINKFileReader(MasterTable masterTable, ProjectHandler projectHandler) {
		this.masterTable = masterTable;
		this.ds = new DataStorage(projectHandler);
		
		this.refParser = new REFParser(ds);
		this.locParser = new LOCParser(ds);
		this.mapParser = new MAPParser(ds);
		this.phenoParser = new PHENOParser(masterTable);
		this.pedParser = new PEDParser();
		this.slrParser = new SLRParser(ds);
		this.tlrParser = new TLRParser(ds);
	}
	
	/**
	 * @param pedFile
	 * @param mapFile
	 * @param phenoFile
	 * @param refFile 
	 * @param locFile 
	 * @param slrFiles 
	 * @param tlrFiles 
	 * @param task 
	 * @return DataStorage
	 */
	public DataStorage readPLINKData(File pedFile, File mapFile, File phenoFile, File refFile, File locFile, File[] slrFiles, File[] tlrFiles, RevealTask task) {
		if(task != null && !task.hasBeenCancelled()) {
			task.reportCurrentFractionalProgressStatus(task.getProgress() + 1./8);
			task.writeLog("Parsing ped file...\n");
		}
		readPED(pedFile);
		if(task != null && !task.hasBeenCancelled()) {
			task.reportCurrentFractionalProgressStatus(task.getProgress() + 1./8);
			task.writeLog("Parsing map file...\n");
		}
		readMAP(mapFile);
		if(phenoFile != null) {
			if(task != null && !task.hasBeenCancelled()) {
				task.reportCurrentFractionalProgressStatus(task.getProgress() + 1./8);
				task.writeLog("Parsing phen file...\n");
			}
			readPHENO(phenoFile);
		}
		if(locFile != null) {
			if(task != null && !task.hasBeenCancelled()) {
				task.reportCurrentFractionalProgressStatus(task.getProgress() + 1./8);
				task.writeLog("Parsing loc file...\n");
			}
			readLOC(locFile);
		}
		if(refFile != null) {
			if(task != null && !task.hasBeenCancelled()) {
				task.reportCurrentFractionalProgressStatus(task.getProgress() + 1./8);
				task.writeLog("Parsing ref file...\n");
			}
			readREF(refFile);
		}
		if(slrFiles != null) {
			if(task != null && !task.hasBeenCancelled()) {
				task.reportCurrentFractionalProgressStatus(task.getProgress() + 1./8);
				task.writeLog("Parsing single locus results...\n");
			}
			readSLR(slrFiles);
		}
		if(tlrFiles != null) {
			if(task != null && !task.hasBeenCancelled()) {
				task.reportCurrentFractionalProgressStatus(task.getProgress() + 1./8);
				task.writeLog("Parsing two locus results...\n");
			}
			readTLR(tlrFiles);
		}
		return ds;
	}
	
	protected void readREF(File refFile) {
		refParser.read(refFile);
	}
	
	protected void readLOC(File locFile) {
		locParser.read(locFile);
	}
	
	protected void readMAP(File mapFile) {
			mapParser.read(mapFile);
			ds.setGlobalSNPList(mapParser.snps);
	}
	
	protected void readPHENO(File phenoFile) {
		phenoParser.read(phenoFile);
		ds.setGenes(phenoParser.getGenes());
	}
	
	protected void readPED(File pedFile) {
		pedParser.read(pedFile);
		ds.setHaplotypes(pedParser.haploList);
		ds.setSubjects(pedParser.persons);
	}
	
	protected void readSLR(File[] slrFiles) {
		for(int i = 0; i < slrFiles.length; i++) {
			slrParser.read(slrFiles[i]);
		}
	}
	
	protected void readTLR(File[] tlrFiles) {
		for(int i = 0; i < tlrFiles.length; i++) {
			tlrParser.read(tlrFiles[i]);
		}
	}

	public HierarchicalSetting getSettings() {
		HierarchicalSetting setting = new HierarchicalSetting("PLINK File Reader Setting");
		setting.addSetting(pedParser.getSetting());
		setting.addSetting(mapParser.getSetting());
		setting.addSetting(phenoParser.getSetting());
//		setting.addSetting(refParser.getSetting());
//		setting.addSetting(locParser.getSetting());
//		setting.addSetting(slrParser.getSetting());
//		setting.addSetting(tlrParser.getSetting());
		return setting;
	}
}
