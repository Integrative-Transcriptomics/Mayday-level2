package mayday.exportjs;

import java.awt.Component;

import java.util.HashMap;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.tasks.AbstractTask;
import mayday.exportjs.plugins.PlotExportPlugin;
import mayday.exportjs.plugins.PlotExportSetting;
import mayday.vis3.ColorProvider;
import mayday.vis3.PlotMenuElementPlugin;
import mayday.vis3.model.ViewModel;

public class StandaloneExport extends AbstractPlugin implements PlotMenuElementPlugin {
	
	ViewModel viewModel;
	ColorProvider coloring;
	Distributor distributor;
		
	@Override
	public void init() {	
	}

	@Override
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.vis3.exporthtml",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Matthias Munz",
				"munz@informatik.uni-tuebingen.de",
				"Creates a stand-alone html file containing various interactive visualizations",
				"Create Stand-Alone HTML file"
		);
		return pli;	
	}

	@Override
	public void run(ViewModel vm, Component pc) {
		
		viewModel = vm;
		distributor = new Distributor(this.viewModel);
		
		// Plot Settings
		HierarchicalSetting plotSettings = new HierarchicalSetting("Plots", HierarchicalSetting.LayoutStyle.TABBED, false);
		
		for (PlotExportPlugin pes : distributor.getAvailablePlots())
			plotSettings.addSetting(pes.getSetting());		
		
		// Main Settings
		HierarchicalSetting mainSettings = new HierarchicalSetting("Export Js").setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_HORIZONTAL);
		HierarchicalSetting settings = new HierarchicalSetting("").setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL)
				.addSetting(distributor.getOutputWriter().getOutputWriterSetting())
			    .addSetting(distributor.getHtmlExportSettings());
		mainSettings.addSetting(settings).addSetting(plotSettings);
		
		SettingDialog d = new SettingDialog(null, "Export", mainSettings);
		d.showAsInputDialog();
		
		if(d.closedWithOK()) {
			
			// Set OutputWriter
			distributor.getOutputWriter().getOutputWriterSetting().updateOutputWriter();
			
			// Set Plot
			for (PlotExportPlugin pes : distributor.getAvailablePlots()){
				PlotExportSetting s = (PlotExportSetting)pes.getSetting();
				if (s.getBooleanValue()){
					
					// Set probesTableInteraction
					s.setProbesTableInteraction(this.distributor.getHtmlExportSettings().isProbesTableInteraction());
					
					// Set metaInfoTableInteraction
					s.setMetaTableInteraction(this.distributor.getHtmlExportSettings().isMetaTableInteraction());
					
					distributor.addExporter(pes.getPlotExporter());
				}
			}
			
			new AbstractTask("Exporting Standalone Visualization") {
				
				@Override
				protected void initialize() {
				}
				
				@Override
				protected void doWork() throws Exception {
					distributor.export();
				}
			}.start();
			
			
		}
	}

}
