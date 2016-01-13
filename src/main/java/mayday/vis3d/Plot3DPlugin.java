package mayday.vis3d;

import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotWithLegendAndTitle;

public abstract class Plot3DPlugin extends PlotPlugin {

	public String author = "G\u00FCnter J\u00E4ger";
	public String email = "jaeger@informatik.uni-tuebingen.de";
	public String description = "";
	public String plotName = "";
	public String iconPath = "";
	public String category = "Three Dimensional";
	public String pluginClass = "";
	
	
	@Override
	public Component getComponent() {
		Component c = this.getPlot3DComponent();
		if(c == null)
			return null;
		return new PlotWithLegendAndTitle(c);
	}
	
	public void init(){};
	
	public abstract Component getPlot3DComponent();
	public abstract void initInfos();

	@Override
	public PluginInfo register() throws PluginManagerException {
		this.initInfos();
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.vis3d." + pluginClass,
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				author, email, description,	plotName
		);
		
		if(!iconPath.equals("")) {
			pli.setIcon(iconPath);
		}
		
		pli.addCategory(category);
		pli.setMenuName(plotName);
		return pli;
	}
}
