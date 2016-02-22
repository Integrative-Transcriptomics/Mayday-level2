package mayday.vis3.plots.volcano;

/**
 * @author Alexander Stoppel
 *
 */

import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotWithLegendAndTitle;

public class VolcanoPlot extends PlotPlugin{
	
	public void init(){
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		@SuppressWarnings("rawtypes") // necessary? 
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(), 
				"IT.vis3.VolcanoPlot",
				new String[0], 
				MaydayDefaults.Plugins.CATEGORY_PLOT, 
				new HashMap<String, Object>(), "Alexander Stoppel", 
				"stoppel@informatik.uni-tuebingen.de", 
				"A 2D scatter plot of -log p-values and log fold change (Volcano Plot)",
				"Volcano Plot"
		);
		
		pli.setIcon("mayday/vis3/scatter128.png");
		pli.addCategory("Scatter plots");
//		pli.setMenuName("\0Volcano Plot"); //? necessary ? <- NO, you already set that in the pli constructor, if not set plugin name will be used! (GJ)
//		setIsMajorPlot(pli);
		return pli;
	}

	public Component getComponent() {
		Component myComponent = new PlotWithLegendAndTitle(new VolcanoPlotComponent());
		return myComponent;
	}
	
}

