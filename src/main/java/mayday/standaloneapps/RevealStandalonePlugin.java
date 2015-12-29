package mayday.standaloneapps;

import java.util.HashMap;

import mayday.Reveal.RevealMain;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;

/**
 * @author jaeger
 *
 */

public class RevealStandalonePlugin extends AbstractPlugin implements GenericPlugin {

	@Override
	public void run() {
		new RevealMain();
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return createPluginInfo();
	}
	
	/**
	 * @return plugin info object for registration
	 * @throws PluginManagerException
	 */
	public static PluginInfo createPluginInfo() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				RevealStandalonePlugin.class,
				"mayday.Reveal",
				new String[0],
				Constants.MC_SESSION,
				new HashMap<String, Object>(),
				"G&uuml;nter J&auml;ger",
				"jaeger@informatik.uni-tuebingen.de",
				"",
				"Reveal - Visual eQTL Analytics"
		);
		return pli;	
	}

	@Override
	public void init() {}
}
