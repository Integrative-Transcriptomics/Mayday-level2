package mayday.Reveal.actions;

import java.util.Collection;

import mayday.Reveal.RevealPlugin;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.utilities.BrowserToolkit;
import mayday.Reveal.utilities.RevealMenuConstants;

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
	public void run(Collection<SNVList> snpLists) {
		BrowserToolkit.openURL("http://it.inf.uni-tuebingen.de/?page_id=179");
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
