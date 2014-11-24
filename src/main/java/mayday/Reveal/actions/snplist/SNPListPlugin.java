package mayday.Reveal.actions.snplist;

import mayday.Reveal.RevealPlugin;
import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.core.pluma.Constants;

public abstract class SNPListPlugin extends RevealPlugin {

	public static final String CATEGORY = "SNPList";
	public static final String MC = Constants.MC_REVEAL + "/" + CATEGORY;
	
	@Override
	public String getMenu() {
		return RevealMenuConstants.SNPLIST_MENU;
	}

	@Override
	public String getCategory() {
		return CATEGORY;
	}
}
