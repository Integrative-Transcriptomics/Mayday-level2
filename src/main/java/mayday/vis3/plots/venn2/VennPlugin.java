package mayday.vis3.plots.venn2;

import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotWithLegendAndTitle;

public class VennPlugin  extends PlotPlugin
{
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.canvas.Venn2",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Venn Diagram (up to three probe lists)",
				"Venn Diagram"
		);
		pli.setIcon("mayday/vis3/venn.png");
		return pli;	
	}
	
	public Component getComponent() 
	{
		Component myComponent;
		myComponent = new PlotWithLegendAndTitle(new VennPlot());	
		return myComponent;
	}

	@Override
	public void init() 
	{
		// do nothing. 		
	}
}
