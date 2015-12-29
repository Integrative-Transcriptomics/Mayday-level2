package mayday.exportjs.plugins;

import mayday.core.gui.PreferencePane;
import mayday.core.pluma.AbstractPlugin;
import mayday.exportjs.exporter.PlotExporter;


public abstract class PlotExportPlugin extends AbstractPlugin {

	public static final String MC = "Visualization/Export/Standalone";
	
	@Override
	public PreferencePane getPreferencesPanel() {
		return null;
	}
	
	public abstract PlotExporter getPlotExporter();
	
}
