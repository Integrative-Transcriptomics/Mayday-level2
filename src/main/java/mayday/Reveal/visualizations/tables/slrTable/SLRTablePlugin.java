package mayday.Reveal.visualizations.tables.slrTable;

import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;

public class SLRTablePlugin extends RevealVisualizationPlugin {

	@Override
	public String getType() {
		return "vis.table.slr";
	}

	@Override
	public String getDescription() {
		return "Single Locus Results Table";
	}

	@Override
	public String getName() {
		return "SLR Table";
	}

	@Override
	public String getIconPath() {
		return null;
	}

	@Override
	public String getMenuName() {
		return "SLR Table";
	}

	@Override
	public RevealVisualization getComponent() {
		return new SLRTableVisualization(projectHandler);
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
