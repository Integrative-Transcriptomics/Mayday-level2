package mayday.vis3.plots.chromogram;

import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotScrollPane;

public class ChromogramPlugin  extends PlotPlugin
{

	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.canvas.Chromogram",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Chromograms show ordered textual information as color bars.",
				"Chromogram"
		);
		pli.setIcon("mayday/vis3/chromogram128.png");
		pli.addCategory("Meta Information");
		return pli;	
	}
	
	public Component getComponent() 
	{
		Component myComponent;
		myComponent = new PlotScrollPane(new ChromgramPlot());	
		return myComponent;
	}

	@Override
	public void init() 
	{
		// do nothing. 		
	}
	
}
