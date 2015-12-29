package mayday.Reveal.io.project.factory.vcf;

import mayday.Reveal.io.project.factory.AbstractProjectDefinition;
import mayday.Reveal.io.project.factory.ProjectCreator;
import mayday.Reveal.io.project.factory.finalize.FinalizationProjectDefinition;
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
