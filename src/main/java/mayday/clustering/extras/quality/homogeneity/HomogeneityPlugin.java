package mayday.clustering.extras.quality.homogeneity;

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
public class HomogeneityPlugin extends AbstractPlugin implements ProbelistPlugin{

		private HomogeneitySetting hSetting;
		private double homogeneity = 0d;
		
		public List<ProbeList> run(final List<ProbeList> probeLists, MasterTable masterTable) {
			hSetting = new HomogeneitySetting();
			SettingDialog sd = new SettingDialog(null, "Homogeneity Setting", hSetting);
			sd.showAsInputDialog();	
			
			if(sd.closedWithOK()) {
				AbstractTask cTask = new AbstractTask("Homogeneity") {
		
					protected void initialize() {}
		
					protected void doWork() throws Exception {
						Homogeneity homogeneityFunction = new Homogeneity();
						homogeneity = homogeneityFunction.calculateHomogeneity(probeLists, hSetting);
					}
				};
				
				cTask.start();
				cTask.waitFor();
				
				MIManager mim = masterTable.getDataSet().getMIManager();
				MIGroup hGroup = mim.newGroup(DoubleMIO.myType, "Homogeneity (Clustering Quality)");
				hGroup.add(probeLists, new DoubleMIO(homogeneity));
			}
			
			return null;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public PluginInfo register() throws PluginManagerException {
			PluginInfo pli= new PluginInfo(
					(Class)this.getClass(),
					"IT.clustering.quality.homogeneity",
					null, 
					Constants.MC_PROBELIST,
					(HashMap<String,Object>)null,
					"Jennifer Lange",
					"no e-mail provided",
					"Calculates the average homogeneity of a set of probe lists from a preliminary clustering",
					"Homogeneity");
					pli.addCategory(MaydayDefaults.Plugins.CATEGORY_CLUSTERING+"/"+MaydayDefaults.Plugins.SUBCATEGORY_CLUSTERINGEXTRAS);
			return pli;
		}

		@Override
		public void init() {}
}