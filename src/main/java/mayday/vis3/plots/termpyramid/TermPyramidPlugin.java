package mayday.vis3.plots.termpyramid;

import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotScrollPane;

public class TermPyramidPlugin  extends PlotPlugin
{
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.canvas.TermPyramid",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"MIO Comparison plot",
				"Term Pyramid"
		);
		pli.setIcon("mayday/vis3/termpyramid.png");
		pli.addCategory("Meta Information");
		return pli;	
	}
	
	public Component getComponent() 
	{
		Component myComponent;
		myComponent = new PlotScrollPane(new TermPyramidPlot());	
		return myComponent;
	}

	@Override
	public void init() 
	{
		// do nothing. 		
	}
}
