package mayday.Reveal.visualizations.matrices.epistasis;

import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;

public class EpistasisMatrixPlugin extends RevealVisualizationPlugin {

	@Override
	public String getType() {
		return "vis.TLAssociationMatrix";
	}

	@Override
	public String getDescription() {
		return "Epistasis Matrix";
	}

	@Override
	public String getName() {
		return "Epistasis Matrix";
	}

	@Override
	public String getIconPath() {
		return "mayday/GWAS/associationMatrix.png";
	}

	@Override
	public String getMenuName() {
		return "Epistasis Matrix";
	}

	@Override
	public boolean usesScrollPane() {
		return true;
	}

	@Override
	public RevealVisualization getComponent() {
		return new EpistasisMatrix(projectHandler);
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
