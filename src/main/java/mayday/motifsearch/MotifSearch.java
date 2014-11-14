package mayday.motifsearch;

import java.util.*;
import java.util.regex.*;

import mayday.core.MasterTable;
import mayday.core.ProbeList;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.motifsearch.gui.MotifSearchSettingDialog;

public class MotifSearch
extends AbstractPlugin
implements ProbelistPlugin {

	@Override
	public void init() {
	}

	@Override
	public PluginInfo register()
			throws PluginManagerException {
				PluginInfo pli = new PluginInfo(this.getClass(),
				"PAS.motifsearch.MotifSearch",
				new String[0],
				Constants.MC_PROBELIST,
				// Constants.MC_SESSION,
				new HashMap<String, Object>(), "Frederik Weber",
				"frederik.weber@student.uni-tuebingen.de",
				"de novo motif serch on predefined sequences", "Motif Search");
		pli.setMenuName("Motif Search");
		// pli.setIcon(iconPath);
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probelists,
			MasterTable masterTable) {
		new MotifSearchSettingDialog(null, "Motif Search", probelists, PluginManager.getInstance().getPluginFromID("PAS.motifsearch.MotifSearch"));
		return new LinkedList<ProbeList>();
	}

	public static final boolean isValidSynonymName(String testString) {
		Pattern p = Pattern.compile("[A-Z0-9]{6,9}(-[A-Z0-9]{1,3})?+");
		Matcher m = p.matcher(testString);
		return m.matches();
	}
}
