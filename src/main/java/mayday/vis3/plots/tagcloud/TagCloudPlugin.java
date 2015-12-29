package mayday.vis3.plots.tagcloud;

import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotScrollPane;

public class TagCloudPlugin extends PlotPlugin
{
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.canvas.TagCloud",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Tag Clouds show sets of annotations as clouds of text bubbles, scaled to the frequency of the text",
				"Tag Cloud"
		);
		pli.setIcon("mayday/vis3/tags128.png");
		pli.addCategory("Meta Information");
		return pli;	
	}
	
	public Component getComponent() 
	{
		Component myComponent;
		TagCloudViewer viewer = new TagCloudViewer();
		myComponent = new TagCloudWithLegendAndTitle(new PlotScrollPane(viewer), viewer);	
		return myComponent;
	}

	@Override
	public void init() 
	{
		// do nothing. 		
	}
}
