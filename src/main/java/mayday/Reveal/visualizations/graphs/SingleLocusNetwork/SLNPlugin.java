//package mayday.Reveal.visualizations.graphs.SingleLocusNetwork;
//
//import mayday.Reveal.utilities.RevealMenuConstants;
//import mayday.Reveal.visualizations.RevealVisualization;
//import mayday.Reveal.visualizations.RevealVisualizationPlugin;
//
//public class SLNPlugin extends RevealVisualizationPlugin {
//
//	@Override
//	public String getType() {
//		return "SLN";
//	}
//
//	@Override
//	public String getDescription() {
//		return "SL eQTL Association Network";
//	}
//
//	@Override
//	public String getName() {
//		return "SL eQTL Association Network";
//	}
//
//	@Override
//	public String getIconPath() {
//		return null;
//	}
//
//	@Override
//	public String getMenuName() {
//		return "SL eQTL Association Network";
//	}
//
//	@Override
//	public boolean usesScrollPane() {
//		return true;
//	}
//
//	@Override
//	public RevealVisualization getComponent() {
//		return new SLN(projectHandler);
//	}
//
//	@Override
//	public boolean showInToolbar() {
//		return true;
//	}
//
//	@Override
//	public boolean usesViewSetting() {
//		return true;
//	}
//
//	@Override
//	public String getMenu() {
//		return RevealMenuConstants.VIS_MENU_NETWORK_SUBMENU;
//	}
//}
