package mayday.exportjs.plugins.heatmap;

import java.util.HashMap;


import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.exportjs.exporter.PlotExporter;
import mayday.exportjs.plugins.PlotExportPlugin;

public class HeatmapExporterPlugin extends PlotExportPlugin {

	protected HeatmapExporterSetting setting;
	
	@Override
	public PlotExporter getPlotExporter() {
		return new HeatmapExporter((HeatmapExporterSetting)getSetting());
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				getClass(),
				"PAS.jsexport.heatmap",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Matthias Munz",
				"matthias.munz@gmx.de",
				"Heatmap settings and exporter to convert to Javascript (Protovis)",
				"Heatmap"
		);
		return pli;	
	}
	
	public Setting getSetting() {
		if (setting==null)
			setting = new HeatmapExporterSetting();
		return setting;
	}
	
	
}
