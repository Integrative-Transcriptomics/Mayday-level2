package mayday.exportjs;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.exportjs.exporter.DataExporterSettings;
import mayday.exportjs.exporter.HTMLExporterSetting;
import mayday.exportjs.exporter.PlotExporter;
import mayday.exportjs.plugins.PlotExportPlugin;
import mayday.vis3.model.ViewModel;

public class Distributor {

	private ViewModel viewModel;
	
	private List<PlotExporter> exporters;
	private List<PlotExportPlugin> availablePlots;
	private DataExporterSettings dataExporterSettings;
	private HTMLExporterSetting htmlExportSettings;
	private OutputWriter outputWriter;
	

	public Distributor(ViewModel viewModel){

		this.viewModel = viewModel;
		this.exporters = new ArrayList<PlotExporter>();
		
		Set<PluginInfo> plis= PluginManager.getInstance().getPluginsFor(PlotExportPlugin.MC);
		this.availablePlots = new ArrayList<PlotExportPlugin>();
		
		for(PluginInfo pli:plis)
			availablePlots.add((PlotExportPlugin)pli.getInstance());

		this.dataExporterSettings = new DataExporterSettings(this.viewModel);
		this.htmlExportSettings = new HTMLExporterSetting();
		this.outputWriter = new OutputWriter();
		
	}
	
	public void export(){
		this.outputWriter.export(this.dataExporterSettings, this.htmlExportSettings, this.exporters);
	}

	public List<PlotExportPlugin> getAvailablePlots() {
		return availablePlots;
	}

	public void setAvailablePlots(List<PlotExportPlugin> availablePlots) {
		this.availablePlots = availablePlots;
	}

	public void addExporter(PlotExporter exporter) {
		this.exporters.add(exporter);
	}

	public OutputWriter getOutputWriter() {
		return outputWriter;
	}

	public HTMLExporterSetting getHtmlExportSettings() {
		return htmlExportSettings;
	}
}
