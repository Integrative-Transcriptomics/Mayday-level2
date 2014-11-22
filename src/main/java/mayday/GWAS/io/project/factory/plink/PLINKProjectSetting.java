package mayday.GWAS.io.project.factory.plink;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.FilesSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.settings.typed.StringSetting;

public class PLINKProjectSetting extends HierarchicalSetting {

	private StringSetting projectName;
	private PathSetting haplotypesFile;
	private PathSetting mapFileSetting;
	private PathSetting phenotypesFile;
	
	private PathSetting referencesFile;
	private PathSetting geneLocationsFile;
	
	private FilesSetting singleLocusFiles;
	private FilesSetting twoLocusFiles;
	
	public PLINKProjectSetting() {
		super("New PLINK Project settings");
		
		projectName = new StringSetting("Project name" , "Name of the project", "Reveal - Project");
		haplotypesFile =  new PathSetting("PED file", "File containing haplotype information", null, false, true, false);
		mapFileSetting = new PathSetting("MAP file", "File containing snp identifier and locations", null, false, true, false);
		phenotypesFile = new PathSetting("Phenotype file", "File containing phenotype information", null, false, true, true);
		
		referencesFile = new PathSetting("SNP Reference file", "File containing reference information for each SNP", null, false, true, true);
		geneLocationsFile = new PathSetting("Gene Location file", "File containing the gene locations for each gene", null, false, true, true);
		
		singleLocusFiles = new FilesSetting("Single Locus Results", null, null);
		twoLocusFiles = new FilesSetting("Two Locus Results", null, null);
		
		//add the settings
		this.addSetting(projectName);
		this.addSetting(haplotypesFile);
		this.addSetting(mapFileSetting);
		this.addSetting(phenotypesFile);
		this.addSetting(referencesFile);
		this.addSetting(geneLocationsFile);
		this.addSetting(singleLocusFiles);
		this.addSetting(twoLocusFiles);
	}
	
	public PathSetting getHaplotypesFile() {
		return this.haplotypesFile;
	}
	
	public PLINKProjectSetting clone() {
		PLINKProjectSetting s = new PLINKProjectSetting();
		s.fromPrefNode(this.toPrefNode());
		return s;
	}

	public StringSetting getMapFileSetting() {
		return this.mapFileSetting;
	}

	public StringSetting getReferencesFile() {
		return this.referencesFile;
	}

	public StringSetting getGeneLocationsFile() {
		return this.geneLocationsFile;
	}

	public StringSetting getPhenotypesFile() {
		return this.phenotypesFile;
	}

	public FilesSetting getSingleLocusFiles() {
		return this.singleLocusFiles;
	}

	public FilesSetting getTwoLocusFiles() {
		return this.twoLocusFiles;
	}

	public StringSetting getProjectName() {
		return this.projectName;
	}
}
