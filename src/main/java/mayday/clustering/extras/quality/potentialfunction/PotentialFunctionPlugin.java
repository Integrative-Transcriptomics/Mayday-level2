package mayday.clustering.extras.quality.potentialfunction;

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
public class PotentialFunctionPlugin extends AbstractPlugin implements ProbelistPlugin{

	private MasterTable masta;
	private PotentialFunctionSetting pfSetting;
	private double potential = 0;
	
	public List<ProbeList> run(final List<ProbeList> probeLists,	MasterTable masterTable) {
		masta = masterTable;
		pfSetting = new PotentialFunctionSetting();
		SettingDialog sd = new SettingDialog(null, "Potential Function Setting", pfSetting);
		sd.showAsInputDialog();
		
		if(sd.closedWithOK()) {
			AbstractTask cTask = new AbstractTask("Potential Function") {
	
				protected void initialize() {}
				
				protected void doWork() throws Exception {
					PotentialFunction pf = new PotentialFunction();
					potential = pf.calculatePotentialFunction(masta, probeLists, pfSetting);	
				}
			};
			
			cTask.start();
			cTask.waitFor();
			
			MIManager mim = masterTable.getDataSet().getMIManager();
			MIGroup pGroup = mim.newGroup(DoubleMIO.myType, "Potential Function (Clustering Quality)");
			pGroup.add(probeLists, new DoubleMIO(potential));
		}
		
		return null;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
			(Class)this.getClass(),
			"IT.clustering.quality.potentialfunction",
			null, 
			Constants.MC_PROBELIST,
			(HashMap<String,Object>)null,
			"Jennifer Lange",
			"no e-mail provided",
			"Calculates the potential function of a preliminary clustering",
			"Potential Function");
			pli.addCategory(MaydayDefaults.Plugins.CATEGORY_CLUSTERING+"/"+MaydayDefaults.Plugins.SUBCATEGORY_CLUSTERINGEXTRAS);
		return pli;
	}

	@Override
	public void init() {}
}
