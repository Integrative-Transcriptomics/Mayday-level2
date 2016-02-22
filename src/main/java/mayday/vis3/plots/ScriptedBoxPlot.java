package mayday.vis3.plots;



import java.awt.Component;
import java.util.HashMap;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.vis3.components.PlotWithLegendAndTitle;
import mayday.vis3.components.ScriptablePlotContainer;
import mayday.vis3.export.ExportPlugin;
import mayday.vis3.export.RasterExportSetting;
import mayday.vis3.export.filters.ExportPNG;
import mayday.vis3.model.Visualizer;
import mayday.vis3.plots.boxplot.BoxPlotComponent;

import javax.swing.*;

public class ScriptedBoxPlot extends AbstractPlugin implements ProbelistPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.scriptableplot.BoxPlot",
				new String[0],
				Constants.MC_PROBELIST,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Creates a boxplot from the selected probelists and writes it to /tmp/bp.png",
				"Box Plot (Scripted)"
		);
		pli.setIcon("mayday/vis3/boxplot128.png");
		return pli;	
	}

	public Component getComponent() {
		Component myComponent;
		myComponent = new PlotWithLegendAndTitle(new BoxPlotComponent());
		return myComponent;
	}

	@Override
	public List<ProbeList> run(List<ProbeList> probeLists,
			MasterTable masterTable) {
		
		// Create the visualizer
		Visualizer vis = new Visualizer(masterTable.getDataSet(), probeLists);
		// Create the plot
		Component bp  = getComponent();
		// Set the plots internal size
		ScriptablePlotContainer spc = new ScriptablePlotContainer(bp, vis, 600, 450);
	
		// Change some of the plot's settings
		HierarchicalSetting hsc = spc.getPlotSettings();

		BooleanSetting bs;
		//bs = (BooleanSetting)hsc.getChild("Overlay ProbeList boxes",true);
		//bs.setBooleanValue(true);
		bs = (BooleanSetting)hsc.getChild("Show Legend",true);
		bs.setBooleanValue(true);
		bs = (BooleanSetting)hsc.getChild("Show caption",true);
		bs.setBooleanValue(true);
		StringSetting ss = (StringSetting)hsc.getChild("Caption text", true);
		ss.setStringValue("A Box Plot");
		HierarchicalSetting hs = (HierarchicalSetting)hsc.getChild("Grid", true);
		bs = (BooleanSetting)hs.getChild("visible");
		bs.setBooleanValue(false);

		System.out.println(hsc.toPrefNode().toDebugString());

		// select one probe as an example
		vis.getViewModel().setProbeSelection(probeLists.get(0).getProbe(0));
		
		// create the export filter
		ExportPlugin svgPlug = new ExportPNG();
		// disable PNG graphics Antialiasing, doesn't work well with boxplots
		RasterExportSetting res = (RasterExportSetting)svgPlug.getSetting();
		res.setAntialiasing(true, false);
		
		// export the file
		try {
			spc.exportToFile(svgPlug, "/tmp/bp.png");
		} catch (Exception e) {
			e.printStackTrace();
		}

		JOptionPane.showMessageDialog(null, "Scripted boxplot exported to:" +
				"/tmp/bp.png", "success", JOptionPane.INFORMATION_MESSAGE);
		return null;
	}

}
