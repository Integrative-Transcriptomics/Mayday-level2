package mayday.wapiti.experiments.generic.mapping;

import java.util.Arrays;
import java.util.List;

import mayday.core.gui.PreferencePane;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.FilesSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.importer.ExperimentImportPlugin;
import mayday.wapiti.transformations.matrix.TransMatrix;

public abstract class AbstractMappingImportPlugin extends ExperimentImportPlugin {

	protected Setting fileSetting;
	protected boolean multiSelection=false, directories=false;

	public AbstractMappingImportPlugin(boolean multipleFiles, boolean directory) {
		multiSelection = multipleFiles;
		directories = directory;
	}

	public Setting getSetting() {
		if (fileSetting==null) {			
			fileSetting = multiSelection ? new FilesSetting("Input Files",null,null,false,null) : 
				(new PathSetting("Input "+(directories?"directory":"file"),null,null,directories,true,false));
		}
		return fileSetting;
	}

	public abstract List<Experiment> getMappingExperiments(List<String> files, TransMatrix transMatrix);

	public PreferencePane getPreferencesPanel() {
		return null;
	}
	
	@Override
	public void importInto(TransMatrix transMatrix) {
		List<String> files;
		if (multiSelection)
			files = ((FilesSetting)fileSetting).getFileNames();
		else
			files = Arrays.asList(new String[]{((PathSetting)fileSetting).getStringValue()});
		
		if (files.size()==0)
			return;
		
		List<Experiment> result = getMappingExperiments(files, transMatrix);
		if (result.size()>0)
			addExperiments(result, transMatrix);
	}


}
