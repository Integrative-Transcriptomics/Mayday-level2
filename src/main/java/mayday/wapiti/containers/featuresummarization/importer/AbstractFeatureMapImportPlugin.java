package mayday.wapiti.containers.featuresummarization.importer;

import java.util.Arrays;
import java.util.List;

import mayday.core.gui.PreferencePane;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.FilesSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.wapiti.containers.featuresummarization.FeatureSummarizationMap;

public abstract class AbstractFeatureMapImportPlugin extends AbstractPlugin {

	public final static String MC = mayday.wapiti.Constants.MCBASE+"FeatureMappingImport";
		
	protected Setting mySetting;
	protected boolean multiSelection=false, directories=false;

	public Setting getSetting() {
		if (mySetting==null) {			
			if (this instanceof FeatureMapFileImportPlugin) {
				System.out.println(this.getClass());	
				PluginInfo pli = PluginManager.getInstance().getPluginFromClass(getClass());
				Integer type = (Integer)(pli.getProperties().get(GUDIConstants.FILESYSTEM_IMPORTER_TYPE));
				if (type==null) {
					System.err.println("GUDI: "+pli.getIdentifier()+" has no valid FILESYSTEM_IMPORTER_TYPE");
				} else {
					switch (type) {
					case GUDIConstants.ONEFILE:
						multiSelection = false;
						directories = false;
						break;
					case GUDIConstants.MANYFILES:
						multiSelection = true;
						directories = false;
						break;
					case GUDIConstants.DIRECTORY:
						multiSelection = false;
						directories = true;
						break;			        
					} 
				}
				mySetting = multiSelection ? new FilesSetting("Input Files",null,null,false,null) :
					(new PathSetting("Input "+(directories?"directory":"file"),null,null,directories,true,false));
			}
		}
		return mySetting;
	}

	public FeatureSummarizationMap run() {
		if (this instanceof FeatureMapFileImportPlugin) {
			FeatureMapFileImportPlugin dsip = (FeatureMapFileImportPlugin)this;
			List<String> files;
			if (multiSelection)
				files = ((FilesSetting)mySetting).getFileNames();
			else
				files = Arrays.asList(new String[]{((PathSetting)mySetting).getStringValue()});			
			return dsip.importFrom(files);			
		} else if (this instanceof FeatureMapImportPlugin){
			FeatureMapImportPlugin dsip = (FeatureMapImportPlugin)this;
			return dsip.importFrom();
		}
		return null;
	}
	
	public PreferencePane getPreferencesPanel() {
        return null;
    }

	
}
