package mayday.Reveal.visualizations.SNPSummaryPlot;

import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;

public class SNPPlotPlugin extends RevealVisualizationPlugin {

	@Override
	public String getType() {
		return "vis.SNPPlot";
	}

	@Override
	public String getDescription() {
		return "SNP Summary Plot";
	}

	@Override
	public String getName() {
		return "SNP Summary Plot";
	}

	@Override
	public String getIconPath() {
		return "mayday/GWAS/snpSummary.png";
	}

	@Override
	public String getMenuName() {
		return "SNP Summary Plot";
	}

	@Override
	public RevealVisualization getComponent() {
		return new SNPSummaryPlot(projectHandler);
	}

	@Override
	public boolean usesScrollPane() {
		return false;
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
