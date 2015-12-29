package mayday.wapiti;

import java.awt.Frame;
import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.io.gudi.prototypes.DatasetImportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.wapiti.gui.TransMatrixFrame;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class Wapiti extends AbstractPlugin implements DatasetImportPlugin {
	
	@Override
	public void init() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				Constants.MCBASE+"Launcher",
				new String[0],
				mayday.core.pluma.Constants.MC_DATASET_IMPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"This plugin can be used to build datasets from different raw data sources, such as spotted and high-density microarrays and mapped sequencing reads.",
				"DataSet Builder"
		);
		pli.setMenuName("Import raw data (SeaSight)");
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_OTHER);
		return pli;
	}

	public List<DataSet> run() {
		Splash.show();
		startWithMatrix(new TransMatrix());
		return null;
	}
	
	public static void startWithMatrix(final TransMatrix tm) {
		
		final TransMatrixFrame tmf = tm.getFrame();
	
		tmf.setVisible(true);
		tmf.setExtendedState(Frame.MAXIMIZED_BOTH);
	}	

}
