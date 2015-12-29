package mayday.Reveal.io.project.factory;

import mayday.Reveal.io.project.ProjectDefinition;
import mayday.Reveal.io.project.factory.plink.PLINKFileConfiguration;
import mayday.Reveal.io.project.factory.vcf.VCFFileConfiguration;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class InputFormatSelector extends AbstractProjectDefinition {
	
	private InputFormatSelectionSetting setting;
	
	public InputFormatSelector() {
		setting = new InputFormatSelectionSetting();
	}
	
	@Override
	public HierarchicalSetting getSetting() {
		if(setting == null)
			setting = new InputFormatSelectionSetting();
		return setting;
	}
	
	public ProjectDefinition getNext() {
		if(setting != null) {
			return (this.next = this.setting.getInputFormatSpecificProjectConfiguration());
		}
		return null;
	}
	
	private class InputFormatSelectionSetting extends HierarchicalSetting {

		private RestrictedStringSetting inputFormats;
		
		public InputFormatSelectionSetting() {
			super("Select input format settings");
			
			String[] supportedFormats = {"PLINK", "VCF"};
			
			inputFormats = new RestrictedStringSetting("Select your input format", 
					"Only a couple of formats are supported, please selected one of the supported formats.", 
					0, supportedFormats); 
			addSetting(inputFormats);
		}
		
		public ProjectDefinition getInputFormatSpecificProjectConfiguration() {
			switch(inputFormats.getSelectedIndex()) {
			case 0:
				return new PLINKFileConfiguration();
			case 1: 
				return new VCFFileConfiguration();
			default: 
				return null;
			}
		}
		
		public InputFormatSelectionSetting clone() {
			InputFormatSelectionSetting s = new InputFormatSelectionSetting();
			s.fromPrefNode(this.toPrefNode());
			return s;
		}
	}

	@Override
	public ProjectCreator getCreator() {
		return null;
	}
}
