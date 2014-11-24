package mayday.Reveal.visualizations.snpmap;

import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;

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
		return true;
	}

	@Override
	public boolean usesViewSetting() {
		return true;
	}
}
