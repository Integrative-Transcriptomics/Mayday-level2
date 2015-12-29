/*
 * modified by Guenter Jaeger
 * on May 19th, 2010
 */
package mayday.expressionmapping.core;

import java.util.HashMap;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingsDialog;
import mayday.expressionmapping.controller.MainController;
import mayday.expressionmapping.view.ui.EMSettings;


/**
 * @author   Stephan Gade
 */
public class ExpressMapping extends AbstractPlugin implements ProbelistPlugin {

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.expressioneapping",
				new String[0],
				Constants.MC_PROBELIST,
				new HashMap<String, Object>(),
				"Stephan Gade",
				"s.gade@dkfz.de",
				"Expression Mapping",
		"Expression Mapping");
		pli.addCategory("Data Mining");
		pli.setMenuName("Expression Mapping");
		return pli;
	}

	@Override
	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
		ProbeList probes = ProbeList.createUniqueProbeList(probeLists);
		EMSettings ems = new EMSettings(masterTable, probes.getNumberOfProbes());
		SettingsDialog sd = new SettingsDialog(null, "Expression Mapping", ems);
		
		if(sd.showAsInputDialog().closedWithOK()) {
			new MainController(probes, masterTable, ems).run();
		}
		
		return null;
	}
	
	public void init()  {}
}
