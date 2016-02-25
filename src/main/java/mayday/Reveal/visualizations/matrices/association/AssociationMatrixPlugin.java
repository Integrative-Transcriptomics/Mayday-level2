package mayday.Reveal.visualizations.matrices.association;

import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;

public class AssociationMatrixPlugin extends RevealVisualizationPlugin {

	@Override
	public String getType() {
		return "vis.am";
	}

	@Override
	public String getDescription() {
		return "eQTL Association Matrix";
	}

	@Override
	public String getName() {
		return "eQTL Association Matrix";
	}

	@Override
	public String getIconPath() {
		return "mayday/GWAS/icons/plots/eQTL-AM.png";
	}

	@Override
	public String getMenuName() {
		return "eQTL Association Matrix";
	}

	@Override
	public RevealVisualization getComponent() {
		return new AssociationMatrix(this.projectHandler.getSelectedProject());
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
