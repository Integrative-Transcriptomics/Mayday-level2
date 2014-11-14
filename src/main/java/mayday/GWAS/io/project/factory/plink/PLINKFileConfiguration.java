package mayday.GWAS.io.project.factory.plink;

import mayday.GWAS.io.project.factory.AbstractProjectDefinition;
import mayday.GWAS.io.project.factory.ProjectCreator;
import mayday.GWAS.io.project.factory.finalize.FinalizationProjectDefinition;
import mayday.core.settings.generic.HierarchicalSetting;

public class PLINKFileConfiguration extends AbstractProjectDefinition {
	
	private PLINKProjectSetting setting;
	
	public PLINKFileConfiguration() {
		next = new FinalizationProjectDefinition();
	}
	
	@Override
	public HierarchicalSetting getSetting() {
		if(setting == null)
			setting = new PLINKProjectSetting();
		return setting;
	}

	@Override
	public ProjectCreator getCreator() {
		return new PLINKProjectCreator(setting);
	}
}
