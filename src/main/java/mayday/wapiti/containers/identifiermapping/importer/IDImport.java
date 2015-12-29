package mayday.wapiti.containers.identifiermapping.importer;

import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.wapiti.containers.identifiermapping.IdentifierMap;
import mayday.wapiti.containers.identifiermapping.IdentifierMapContainer;
import mayday.wapiti.containers.identifiermapping.importer.csv.IdentifierImporter;

public class IDImport {
	
	public static IdentifierMap run() {
		
		PluginTypeSetting<AbstractIDImportPlugin> s = new PluginTypeSetting<AbstractIDImportPlugin>(
				"Import Plugin",
				"Select how to import an id mapping", 
				new IdentifierImporter(), 
				AbstractIDImportPlugin.MC
		);
		
		SettingDialog sd = new SettingDialog(null, "Import Identifier Mapping", s).showAsInputDialog();
		
		if (!sd.canceled()) {
			AbstractIDImportPlugin ap = s.getInstance();
			IdentifierMap lm = ap.run();
			if (lm!=null)
				IdentifierMapContainer.INSTANCE.add(lm);
			return lm;
		}
		return null;		
	}

}
