package mayday.GWAS.io.project.factory.vcf;

import mayday.GWAS.io.project.factory.AbstractProjectDefinition;
import mayday.GWAS.io.project.factory.ProjectCreator;
import mayday.GWAS.io.project.factory.finalize.FinalizationProjectDefinition;
import mayday.core.settings.generic.HierarchicalSetting;

public class VCFFileConfiguration extends AbstractProjectDefinition{

	private VCFProjectSetting setting;
	
	public VCFFileConfiguration() {
		this.next = new FinalizationProjectDefinition();
	}
	
	@Override
	public HierarchicalSetting getSetting() {
		if(setting == null)
			setting = new VCFProjectSetting();
		return setting;
	}

	@Override
	public ProjectCreator getCreator() {
		return new VCFProjectCreator(setting);
	}
}
