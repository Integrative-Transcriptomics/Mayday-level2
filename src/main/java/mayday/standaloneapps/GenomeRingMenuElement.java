package mayday.standaloneapps;



import it.genomering.GenomeRing;

import java.util.HashMap;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;

public class GenomeRingMenuElement extends AbstractPlugin implements GenericPlugin {

	public void init() {
	}

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				getClass(),
				"IT.GenomeRing",
				new String[0],
				Constants.MC_SESSION,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"",
				"GenomeRing"
			); 
		return pli;	
	}

	@Override
	public void run() {
		GenomeRing gr = new GenomeRing();
		gr.start();
	}
	
}
