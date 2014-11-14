package mayday.GWAS.visualizations.matrices.twolocus;

import mayday.GWAS.utilities.RevealMenuConstants;
import mayday.GWAS.visualizations.RevealVisualization;
import mayday.GWAS.visualizations.RevealVisualizationPlugin;

public class AssociationMatrixPlugin extends RevealVisualizationPlugin {

	@Override
	public String getType() {
		return "vis.TLAssociationMatrix";
	}

	@Override
	public String getDescription() {
		return "Two Locus Association Matrix based on PLINK Two Locus Results";
	}

	@Override
	public String getName() {
		return "Two Locus Association Matrix";
	}

	@Override
	public String getIconPath() {
		return "mayday/GWAS/associationMatrix.png";
	}

	@Override
	public String getMenuName() {
		return "Two Locus Association Matrix";
	}

	@Override
	public boolean usesScrollPane() {
		return true;
	}

	@Override
	public RevealVisualization getComponent() {
		return new AssociationMatrix(projectHandler);
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
