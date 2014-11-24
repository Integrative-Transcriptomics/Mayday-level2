package mayday.Reveal.visualizations.graphs.TwoLocusNetwork;

import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;

public class TLNPlugin extends RevealVisualizationPlugin {

	@Override
	public String getType() {
		return "vis.TLN";
	}

	@Override
	public String getDescription() {
		return "Two Locus Network based on PLINK Two Locus Results";
	}

	@Override
	public String getName() {
		return "Two Locus Network";
	}

	@Override
	public String getIconPath() {
		return "mayday/GWAS/icons/plots/tlNetwork.png";
	}

	@Override
	public String getMenuName() {
		return "Two Locus Network";
	}

	@Override
	public boolean usesScrollPane() {
		return true;
	}

	@Override
	public RevealVisualization getComponent() {
		return new TLN(projectHandler);
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
