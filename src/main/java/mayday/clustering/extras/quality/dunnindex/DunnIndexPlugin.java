package mayday.clustering.extras.quality.dunnindex;

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
 * 
 * @author Jennifer Lange
 *
 */
public class DunnIndexPlugin extends AbstractPlugin implements ProbelistPlugin {

		private DunnIndexSetting diSetting;
		private double dunnIndex = 0d;
		
		public List<ProbeList> run(final List<ProbeList> probeLists, 	MasterTable masterTable) {
			diSetting = new DunnIndexSetting();
			SettingDialog sd = new SettingDialog(null, "Dunn Index Setting", diSetting);
			sd.showAsInputDialog();	
			
			if(sd.closedWithOK()){
				AbstractTask cTask = new AbstractTask("dunn index") {

					protected void initialize() {}
		
					protected void doWork() throws Exception {
						DunnIndex di = new DunnIndex();
						dunnIndex = di.calculateDunnIndex(probeLists, diSetting);					
					}
				};
				cTask.start();
				cTask.waitFor();
				
				MIManager mim = masterTable.getDataSet().getMIManager();
				MIGroup diGroup = mim.newGroup(DoubleMIO.myType, "Dunn Index (Clustering Quality)");
				diGroup.add(probeLists, new DoubleMIO(dunnIndex));
			}
			
			return null;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public PluginInfo register() throws PluginManagerException {
			PluginInfo pli= new PluginInfo(
					(Class)this.getClass(),
					"IT.clustering.quality.dunnindex",
					null, 
					Constants.MC_PROBELIST,
					(HashMap<String,Object>)null,
					"Jennifer Lange",
					"no e-mail provided",
					"Coumputes the Dunn Index for a set of probe lists from a preliminary clustering",
					"Dunn Index");
					pli.addCategory(MaydayDefaults.Plugins.CATEGORY_CLUSTERING+"/"+MaydayDefaults.Plugins.SUBCATEGORY_CLUSTERINGEXTRAS);
			return pli;
		}

		@Override
		public void init() {}	
}

