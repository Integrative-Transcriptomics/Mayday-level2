package mayday.clustering.extras.quality.daviesbouldinindex;

import java.util.HashMap;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.tasks.AbstractTask;

/**
 * @author Jennifer Lange
 *
 */
public class DaviesBouldinIndexPlugin extends AbstractPlugin implements ProbelistPlugin {
	
	private DaviesBouldinIndexSetting dbiSetting;
	private double daviesBouldinIndex = 0;
	
	public List<ProbeList> run(final List<ProbeList> probeLists, MasterTable masterTable) {
		
		dbiSetting = new DaviesBouldinIndexSetting();
		SettingDialog sd = new SettingDialog(null, "Davies Bouldin Index", dbiSetting);
		sd.showAsInputDialog();	
		
		if(sd.closedWithOK()) {
			AbstractTask cTask = new AbstractTask("Davies Bouldin Index") {
	
				protected void initialize() {}
	
				protected void doWork() throws Exception {
					DaviesBouldinIndex dbi = new DaviesBouldinIndex();
					daviesBouldinIndex = dbi.calculateDBI(probeLists, dbiSetting);
				}
			};
			cTask.start();
			cTask.waitFor();
			
			MIManager mim = masterTable.getDataSet().getMIManager();
			MIGroup dbiGroup = mim.newGroup(DoubleMIO.myType, "Davies Bouldin Index (Clustering Quality)");
			dbiGroup.add(probeLists, new DoubleMIO(daviesBouldinIndex));
		}
		
		return null;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"IT.clustering.quality.daviesbouldinindex",
				null, 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Jennifer Lange",
				"no e-mail provided",
				"Coumputes the Davies-Bouldin-Index for a set of probe lists resulting from a preliminary clustering",
				"Davies-Bouldin-Index");
				pli.addCategory(MaydayDefaults.Plugins.CATEGORY_CLUSTERING+"/"+MaydayDefaults.Plugins.SUBCATEGORY_CLUSTERINGEXTRAS);
		return pli;
	}

	@Override
	public void init() {}
}
