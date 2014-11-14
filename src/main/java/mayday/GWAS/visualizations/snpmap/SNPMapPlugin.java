package mayday.GWAS.visualizations.snpmap;

import mayday.GWAS.utilities.RevealMenuConstants;
import mayday.GWAS.visualizations.RevealVisualization;
import mayday.GWAS.visualizations.RevealVisualizationPlugin;

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
