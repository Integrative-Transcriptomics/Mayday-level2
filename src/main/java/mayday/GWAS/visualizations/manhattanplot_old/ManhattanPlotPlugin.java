//package mayday.GWAS.visualizations.manhattanplot_old;
//
//import java.awt.Component;
//
//import mayday.GWAS.utilities.RevealMenuConstants;
//import mayday.GWAS.visualizations.RevealVisualizationPlugin;
//
//public class ManhattanPlotPlugin extends RevealVisualizationPlugin {
//	
//	@Override
//	public String getType() {
//		return "vis.Manhattan";
//	}
//
//	@Override
//	public String getDescription() {
//		return "Manhattan Plot of SNP p-values";
//	}
//
//	@Override
//	public String getName() {
//		return "Manhattan Plot";
//	}
//
//	@Override
//	public String getIconPath() {
//		return "mayday/GWAS/manhattan.png";
//	}
//
//	@Override
//	public String getMenuName() {
//		return "Manhattan Plot";
//	}
//
//	@Override
//	public boolean usesScrollPane() {
//		return false;
//	}
//
//	@Override
//	public Component getComponent() {
//		return new ManhattanPlot(projectHandler);
//	}
//
//	@Override
//	public Integer getMenu() {
//		return RevealMenuConstants.VIS_MENU;
//	}
//}
