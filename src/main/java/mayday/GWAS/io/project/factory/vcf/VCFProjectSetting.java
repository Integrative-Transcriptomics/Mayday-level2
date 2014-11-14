package mayday.GWAS.io.project.factory.vcf;

import java.util.List;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.FilesSetting;
import mayday.core.settings.typed.StringSetting;

public class VCFProjectSetting extends HierarchicalSetting {

	private StringSetting projectName;
	private FilesSetting vcfFiles;
	
	
	public VCFProjectSetting() {
		super("New VCF Project Setting");
		
		addSetting(projectName = new StringSetting("Project Name", null, "Reveal Project"));
		addSetting(vcfFiles = new FilesSetting("VCF Files", "List of VCF files that should be used for the project", null, false, "vcf"));
	}
	
	public String getProjectName() {
		return this.projectName.getStringValue();
	}
	
	public List<String> getVCFFileNames() {
		return this.vcfFiles.getFileNames();
	}
	
	public VCFProjectSetting clone() {
		VCFProjectSetting s = new VCFProjectSetting();
		s.fromPrefNode(this.toPrefNode());
		return s;
	}
}
