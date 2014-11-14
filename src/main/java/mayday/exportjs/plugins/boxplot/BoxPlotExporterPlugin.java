package mayday.exportjs.plugins.boxplot;

import java.util.HashMap;


import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.exportjs.exporter.PlotExporter;
import mayday.exportjs.plugins.PlotExportPlugin;

public class BoxPlotExporterPlugin extends PlotExportPlugin {

	protected BoxPlotExporterSetting setting;
	
	@Override
	public PlotExporter getPlotExporter() {
		return new BoxPlotExporter((BoxPlotExporterSetting)getSetting());
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				getClass(),
				"PAS.jsexport.boxplot",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Matthias Munz",
				"matthias.munz@gmx.de",
				"Box plot settings and exporter to convert to Javascript (Protovis)",
				"Box Plot"
		);
		return pli;	
	}
	
	public Setting getSetting() {
		if (setting==null)
			setting = new BoxPlotExporterSetting();
		return setting;
	}
	
	
}
