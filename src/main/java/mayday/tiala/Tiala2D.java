package mayday.tiala;

import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.gui.MainFrame;

public class Tiala2D extends AbstractPlugin implements DatasetPlugin {
	
	@Override
	public void init() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.crossDS.TiAlA",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Computes an alignment of different time-series datasets and starts the timeseries alignment gui",
				"Timeseries Alignment Analysis"
		);
		return null;
	}

	public List<DataSet> run(List<DataSet> datasets) {		
		
		if (datasets.size()!=2)
			throw new RuntimeException("Please select two datasets for this plugin");
		
		AlignmentStore store = new AlignmentStore();
		store.initialize(datasets.get(0), datasets.get(1));

		MainFrame mf = new MainFrame(store);
		mf.setVisible(true);

		return null;
	}
}
