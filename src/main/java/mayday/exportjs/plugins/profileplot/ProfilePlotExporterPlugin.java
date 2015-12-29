package mayday.exportjs.plugins.profileplot;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.exportjs.exporter.PlotExporter;
import mayday.exportjs.plugins.PlotExportPlugin;

public class ProfilePlotExporterPlugin extends PlotExportPlugin {

	protected ProfilePlotExporterSetting setting;
	
	@Override
	public PlotExporter getPlotExporter() {
		return new ProfilePlotExporter((ProfilePlotExporterSetting)getSetting());
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				getClass(),
				"PAS.jsexport.profileplot",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Matthias Munz",
				"matthias.munz@gmx.de",
				"Profile plot settings and exporter to convert to Javascript (Protovis)",
				"Profile Plot"
		);
		return pli;	
	}
	
	public Setting getSetting() {
		if (setting==null)
			setting = new ProfilePlotExporterSetting();
		return setting;
	}
	
	
}
