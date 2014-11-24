package mayday.Reveal.visualizations.snpcharacterization;

import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;

public class SNPCharInformationTablePlugin extends RevealVisualizationPlugin {

	@Override
	public String getType() {
		return "vis.SNPCharInfoTable";
	}

	@Override
	public String getDescription() {
		return "SNP Characterization Information Table";
	}

	@Override
	public String getName() {
		return "SNP Characterization Information Table";
	}

	@Override
	public String getIconPath() {
		return "mayday/GWAS/effectPredictionTable.png";
	}

	@Override
	public String getMenuName() {
		return "SNP Characterization Information Table";
	}

	@Override
	public RevealVisualization getComponent() {
		return new SNPCharInformationTable(projectHandler);
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
		return false;
	}
}
