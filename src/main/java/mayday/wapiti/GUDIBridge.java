package mayday.wapiti;

import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.io.gudi.prototypes.DatasetFileImportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class GUDIBridge extends AbstractPlugin implements DatasetFileImportPlugin {
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				mayday.wapiti.Constants.MCBASE+"GUDIBridge",
				new String[0],
				Constants.MC_DATASET_IMPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Loads a SeaSight matrix for manual configuration",
				"SeaSight matrix import"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.ONEFILE);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"SeaSight");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"Mayday SeaSight Matrix");		
		return pli;
	}

	public void init() {}


	
	public List<DataSet> importFrom(final List<String> files) {
		Splash.show();
		TransMatrix tm = new TransMatrix();
		tm.loadFromFile(files.get(0));
		Wapiti.startWithMatrix(tm);
		
		return null;
	}
		
}
