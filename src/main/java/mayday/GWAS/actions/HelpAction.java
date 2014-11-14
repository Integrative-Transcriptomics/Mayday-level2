package mayday.GWAS.actions;

import java.util.Collection;

import mayday.GWAS.RevealPlugin;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.utilities.BrowserToolkit;
import mayday.GWAS.utilities.RevealMenuConstants;

/**
 * @author jaeger
 *
 */
public class HelpAction extends RevealPlugin {

	@Override
	public String getName() {
		return "Help";
	}

	@Override
	public String getType() {
		return "help.showHelp";
	}

	@Override
	public String getDescription() {
		return "Go to Reveal website to get help";
	}

	@Override
	public String getMenuName() {
		return "Help";
	}

	@Override
	public void run(Collection<SNPList> snpLists) {
		BrowserToolkit.openURL("http://www-ps.informatik.uni-tuebingen.de/it/software/reveal/");
	}

	@Override
	public String getMenu() {
		return RevealMenuConstants.HELP_MENU;
	}

	@Override
	public String getCategory() {
		return "Help/Help";
	}
}
