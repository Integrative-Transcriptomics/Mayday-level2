package mayday.Reveal.visualizations.SNVSummaryPlot;

import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;

public class SNVSummaryPlotPlugin extends RevealVisualizationPlugin {

	@Override
	public String getType() {
		return "vis.SNVSummaryPlot";
	}

	@Override
	public String getDescription() {
		return "Single Nucleotide Variation Summary Plot";
	}

	@Override
	public String getName() {
		return "SNV Summary Plot";
	}

	@Override
	public String getIconPath() {
		return "mayday/GWAS/snpSummary.png";
	}

	@Override
	public String getMenuName() {
		return "SNV Summary Plot";
	}

	@Override
	public RevealVisualization getComponent() {
		return new SNVSummaryPlot(getProjectHandler());
	}

	@Override
	public boolean showInToolbar() {
		return true;
	}

	@Override
	public boolean usesScrollPane() {
		return true;
	}

	@Override
	public boolean usesViewSetting() {
		return true;
	}

	@Override
	public String getMenu() {
		return RevealMenuConstants.VIS_MENU;
	}
}
