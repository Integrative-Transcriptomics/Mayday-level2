package mayday.GWAS.visualizations.manhattanplot;

import mayday.GWAS.utilities.RevealMenuConstants;
import mayday.GWAS.visualizations.RevealVisualization;
import mayday.GWAS.visualizations.RevealVisualizationPlugin;

public class ManhattanPlotPlugin extends RevealVisualizationPlugin {

	@Override
	public String getType() {
		return "vis.Manhattan";
	}

	@Override
	public String getDescription() {
		return "Manhattan Plot for SNPs";
	}

	@Override
	public String getName() {
		return "Manhattan Plot";
	}

	@Override
	public String getIconPath() {
		return "mayday/GWAS/icons/plots/manhattan.png";
	}

	@Override
	public String getMenuName() {
		return "Manhattan Plot";
	}

	@Override
	public RevealVisualization getComponent() {
		return new ManhattanPlot(projectHandler);
	}

	@Override
	public boolean usesScrollPane() {
		return true;
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
