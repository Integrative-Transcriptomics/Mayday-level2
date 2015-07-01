package mayday.Reveal.visualizations.snpcharacterization;

import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;

public class SNPCharInformationTablePlugin extends RevealVisualizationPlugin {

	@Override
	public String getType() {
		return "vis.SNVEffectTable";
	}

	@Override
	public String getDescription() {
		return "SNV Effect Table";
	}

	@Override
	public String getName() {
		return "SNV Effect Table";
	}

	@Override
	public String getIconPath() {
		return "mayday/GWAS/effectPredictionTable.png";
	}

	@Override
	public String getMenuName() {
		return "SNP Effect Table";
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
		return RevealMenuConstants.VIS_MENU_TABLE_SUBMENU;
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
