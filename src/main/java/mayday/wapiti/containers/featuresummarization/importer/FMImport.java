package mayday.wapiti.containers.featuresummarization.importer;

import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.wapiti.containers.featuresummarization.FeatureSummarizationMap;
import mayday.wapiti.containers.featuresummarization.FeatureSummarizationMapContainer;
import mayday.wapiti.containers.featuresummarization.importer.csvsingle.FeatureMapImporter;

public class FMImport {
	
	public static FeatureSummarizationMap run() {
		
		PluginTypeSetting<AbstractFeatureMapImportPlugin> s = new PluginTypeSetting<AbstractFeatureMapImportPlugin>(
				"Import Plugin",
				"Select how to import a feature mapping", 
				new FeatureMapImporter(), 
				AbstractFeatureMapImportPlugin.MC
		);
		
		SettingDialog sd = new SettingDialog(null, "Import Identifier Mapping", s).showAsInputDialog();
		
		if (!sd.canceled()) {
			AbstractFeatureMapImportPlugin ap = s.getInstance();
			FeatureSummarizationMap lm = ap.run();
			if (lm!=null)
				FeatureSummarizationMapContainer.INSTANCE.add(lm);
			return lm;
		}
		return null;		
	}

}
