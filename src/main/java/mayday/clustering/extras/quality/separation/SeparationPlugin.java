package mayday.clustering.extras.quality.separation;

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
public class SeparationPlugin extends AbstractPlugin implements ProbelistPlugin{

		private SeparationSetting sSetting;
		private double separation = 0;
		
		public List<ProbeList> run(final List<ProbeList> probeLists,	MasterTable masterTable) {
			
			sSetting = new SeparationSetting();
			SettingDialog sd = new SettingDialog(null, "separation", sSetting);
			sd.showAsInputDialog();	
			
			if(sd.closedWithOK()) {
				AbstractTask cTask = new AbstractTask("separation") {
		
					protected void initialize() {}
		
					protected void doWork() throws Exception {
						Separation sepa = new Separation();
						separation = sepa.calculateSeparation(probeLists, sSetting);
					}
					
				};
				cTask.start();
				cTask.waitFor();
				
				MIManager mim = masterTable.getDataSet().getMIManager();
				MIGroup sGroup = mim.newGroup(DoubleMIO.myType, "Separation (Clustering Quality)");
				sGroup.add(probeLists, new DoubleMIO(separation));
			}
			
			return null;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public PluginInfo register() throws PluginManagerException {
			PluginInfo pli= new PluginInfo(
					(Class)this.getClass(),
					"IT.clustering.quality.separation",
					null, 
					Constants.MC_PROBELIST,
					(HashMap<String,Object>)null,
					"Jennifer Lange",
					"no e-mail provided",
					"Calculates the separation of a set of probe lists from a preliminary clustering",
					"Separation");
					pli.addCategory(MaydayDefaults.Plugins.CATEGORY_CLUSTERING+"/"+MaydayDefaults.Plugins.SUBCATEGORY_CLUSTERINGEXTRAS);
			return pli;
		}

		@Override
		public void init() {}
}