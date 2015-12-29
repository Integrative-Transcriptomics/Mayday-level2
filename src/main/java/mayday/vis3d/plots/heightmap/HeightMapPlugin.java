package mayday.vis3d.plots.heightmap;

import java.awt.Component;

import mayday.vis3d.Plot3DPlugin;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class HeightMapPlugin extends Plot3DPlugin {

	@Override
	public Component getPlot3DComponent() {
		return new HeightMapPanel();
	}

	@Override
	public void initInfos() {
		pluginClass = "heightmap";
		description = "Three dimensional representation of profile expression values";
		plotName = "Height Map";
		iconPath = "mayday/vis3/heightmap128.png";
	}
}
