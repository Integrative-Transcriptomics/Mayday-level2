package mayday.tiala;

import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.gui.MainFrame;

/**
 * @author jaeger
 *
 */
public class Tiala extends AbstractPlugin implements DatasetPlugin {
	
	@Override
	public void init() {}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public PluginInfo register() throws PluginManagerException {
	PluginInfo pli = new PluginInfo(
			(Class)this.getClass(),
			"PAS.crossDS.Tiala",
			new String[0],
			Constants.MC_DATASET,
			new HashMap<String, Object>(),
			"Günter Jäger",
			"jaeger@informatik.uni-tuebingen.de",
			"Computes an alignment of different time-series datasets",
			"Tiala - Timeseries Alignment Analysis"
	);
		return pli;
	}

	public List<DataSet> run(List<DataSet> datasets) {
		if(datasets.size() < 2) 
			throw new RuntimeException("Please select at least two datasets for this plugin");
				
		AlignmentStore store = new AlignmentStore();
		store.initialize(datasets);
		
		MainFrame mf = new MainFrame(store);
		mf.setVisible(true);
		
		return null;
	}
}
