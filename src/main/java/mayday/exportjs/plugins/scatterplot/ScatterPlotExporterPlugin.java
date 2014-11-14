package mayday.exportjs.plugins.scatterplot;

import java.util.HashMap;


import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.exportjs.exporter.PlotExporter;
import mayday.exportjs.plugins.PlotExportPlugin;

public class ScatterPlotExporterPlugin extends PlotExportPlugin {

	protected ScatterPlotExporterSetting setting;
	
	@Override
	public PlotExporter getPlotExporter() {
		return new ScatterPlotExporter((ScatterPlotExporterSetting)getSetting());
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				getClass(),
				"PAS.jsexport.scatterplot",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Matthias Munz",
				"matthias.munz@gmx.de",
				"Scatter plot settings and exporter to convert to Javascript (Protovis)",
				"Scatter Plot"
		);
		return pli;	
	}
	
	public Setting getSetting() {
		if (setting==null)
			setting = new ScatterPlotExporterSetting();
		return setting;
	}
	
	
}
