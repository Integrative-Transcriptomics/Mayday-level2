package mayday.expressionmapping.clustering.weightedkmeans;

import java.util.HashMap;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingsDialog;

/**
 * @author jaeger
 * 
 */
public class WeightedKMeansPlugin extends WeightedKMeansPluginBase implements
		ProbelistPlugin {

	@Override
	public PluginInfo register() throws PluginManagerException {
		// System.out.println("PL1: Register");
		PluginInfo pli = new PluginInfo(
				(Class<? extends WeightedKMeansPlugin>) this.getClass(),
				"PAS.clustering.weightedkmeans", null, Constants.MC_PROBELIST,
				(HashMap<String, Object>) null, "G\u00FCnter J\u00E4ger",
				"jaeger@informatik.uni-tuebingen.de",
				"Weighted K-Means Clustering",
				"Partitioning (Weighted K-Means)");
		pli.addCategory(CATEGORY);
		return pli;
	}

	@Override
	public List<ProbeList> run(List<ProbeList> probeLists,
			MasterTable masterTable) {
		ProbeList uniqueProbeList = ProbeList.createUniqueProbeList(probeLists);
		
		WeightedKMeansSetting settings = new WeightedKMeansSetting(masterTable, uniqueProbeList.getNumberOfProbes());
		
		SettingsDialog setupDialog = settings.getDialog();
		setupDialog.setModal(true);
		setupDialog.setVisible(true);
		if (setupDialog.canceled()) {
			return null;
		}
		return runWithSettings(probeLists, masterTable, settings);
	}
}
