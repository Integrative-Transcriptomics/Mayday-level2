package mayday.GWAS.visualizations.SLProfilePlot;

import mayday.GWAS.utilities.RevealMenuConstants;
import mayday.GWAS.visualizations.RevealVisualization;
import mayday.GWAS.visualizations.RevealVisualizationPlugin;

public class SLProfilePlotPlugin extends RevealVisualizationPlugin {

	@Override
	public String getType() {
		return "vis.SLProfilePlot";
	}

	@Override
	public String getDescription() {
		return "Multi Manhattan Plot for Single Locus Results";
	}

	@Override
	public String getName() {
		return "Single Locus Profile Plot";
	}

	@Override
	public String getIconPath() {
		return "mayday/GWAS/slmultimanhattanplot.png";
	}

	@Override
	public String getMenuName() {
		return "Single Locus Manhattan Plot";
	}

	@Override
	public RevealVisualization getComponent() {
		return new SLProfilePlot(projectHandler);
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
		return false;
	}

	@Override
	public boolean usesViewSetting() {
		return true;
	}
}
