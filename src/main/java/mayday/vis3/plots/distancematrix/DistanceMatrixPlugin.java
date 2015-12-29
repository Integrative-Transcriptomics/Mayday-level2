package mayday.vis3.plots.distancematrix;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.gui.AbstractTableWindow;
import mayday.vis3.model.Visualizer;
import mayday.vis3.tables.TablePlugin;

/**
 * 
 * @author Jennifer Lange
 *
 */
public class DistanceMatrixPlugin extends TablePlugin {

	public void init() {}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"IT.vis3.ProbeListDistanceMatrix",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Jennifer Lange",
				"langej@informatik.uni-tuebingen.de",
				"Distance Matrix displaying pairwise distances between probes from the selected probe lists",
				"Distance Matrix"
				);
		pli.setIcon("mayday/vis3/DistanceMatrix128.png");
		pli.addCategory("Tables");
		return pli;
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	public AbstractTableWindow getTableWindow(Visualizer viz) {
		DistanceMatrixWindow win = new DistanceMatrixWindow(viz);
		return win;	
	}
}
