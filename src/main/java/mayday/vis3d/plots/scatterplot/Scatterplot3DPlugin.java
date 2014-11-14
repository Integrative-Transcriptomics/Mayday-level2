package mayday.vis3d.plots.scatterplot;

import java.awt.Component;

import mayday.vis3d.Plot3DPlugin;

/**
 * @author G\u00FCnter J\u00E4ger
 * @date Jun 10, 2010
 */
public class Scatterplot3DPlugin extends Plot3DPlugin {

	@Override
	public Component getPlot3DComponent() {
		return new Scatterplot3DPanel();
	}
	
	public void initInfos() {
		pluginClass = "scatterplot3d";
		description = "Scatter Plot representing three dimensional data";
		plotName = "3D Scatter Plot";
		iconPath = "mayday/vis3/scatter3d128.png";
	}
}
