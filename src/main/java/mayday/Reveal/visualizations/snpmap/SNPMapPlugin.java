package mayday.Reveal.visualizations.snpmap;

import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class SNPMapPlugin extends RevealVisualizationPlugin {

	@Override
	public String getType() {
		return "vis.SNPMap";
	}

	@Override
	public String getDescription() {
		return "SNP Map";
	}

	@Override
	public String getName() {
		return "SNP Map";
	}

	@Override
	public String getIconPath() {
		return "mayday/GWAS/snpMap.png";
	}

	@Override
	public String getMenuName() {
		return "SNP Map";
	}

	@Override
	public boolean usesScrollPane() {
		return true;
	}

	@Override
	public RevealVisualization getComponent() {
		return new SNPMap(projectHandler);
	}

	@Override
	public String getMenu() {
		return RevealMenuConstants.VIS_MENU;
	}

	@Override
	public boolean showInToolbar() {
		return false;
	}

	@Override
	public boolean usesViewSetting() {
		return true;
	}
	
	/*
	 * do not register this plugin
	 * (non-Javadoc)
	 * @see mayday.Reveal.visualizations.RevealVisualizationPlugin#register()
	 */
	public PluginInfo register() throws PluginManagerException {
		return null;
	}
}
