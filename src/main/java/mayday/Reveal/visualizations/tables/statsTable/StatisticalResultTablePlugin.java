package mayday.Reveal.visualizations.tables.statsTable;

import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;

public class StatisticalResultTablePlugin extends RevealVisualizationPlugin {

	@Override
	public String getType() {
		return "vis.table.stats";
	}

	@Override
	public String getDescription() {
		return "Statistical Results Meta Information Table";
	}

	@Override
	public String getName() {
		return "Statistics Table";
	}

	@Override
	public String getIconPath() {
		return null;
	}

	@Override
	public String getMenuName() {
		return "Statistics Table";
	}

	@Override
	public RevealVisualization getComponent() {
		return new StatisticalResultTableVisualization(this.projectHandler);
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
