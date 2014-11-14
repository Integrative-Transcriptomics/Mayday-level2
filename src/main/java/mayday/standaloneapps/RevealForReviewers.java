package mayday.standaloneapps;



import mayday.GWAS.RevealStandalonePlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;

public class RevealForReviewers extends AbstractPlugin implements GenericPlugin {

	public void init() {
	}

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"IT.Reveal.ShowMenuForReviewers",
				null,
				Constants.MC_SUPPORT,
				null,
				"Günter Jäger",
				"guenter.jaeger@uni-tuebingen.de",
				"",
				"Reveal reviewer access"
		);
		return pli;	
	}

	@Override
	public void run() {
		try {
			PluginManager.getInstance().addLatePlugin(RevealStandalonePlugin.createPluginInfo());
		} catch (PluginManagerException e) {
			// schwuups
			e.printStackTrace();
		}
	}

}
