package mayday.Reveal.visualizations.LDPlot;

import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;

public class LDPlotPlugin extends RevealVisualizationPlugin {

	@Override
	public String getType() {
		return "vis.LDPlot";
	}

	@Override
	public String getDescription() {
		return "SNV Linkage Disequilibrium Plot";
	}

	@Override
	public String getName() {
		return "LD Plot";
	}

	@Override
	public String getIconPath() {
		return null;
	}

	@Override
	public String getMenuName() {
		return "LD Plot";
	}

	@Override
	public RevealVisualization getComponent() {
		return new LDPlot(getProjectHandler());
	}

	@Override
	public boolean showInToolbar() {
		return false;
	}

	@Override
	public boolean usesScrollPane() {
		return false;
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
