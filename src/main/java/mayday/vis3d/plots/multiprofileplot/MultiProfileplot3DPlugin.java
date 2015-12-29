package mayday.vis3d.plots.multiprofileplot;

import java.awt.Component;

import mayday.vis3d.Plot3DPlugin;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class MultiProfileplot3DPlugin extends Plot3DPlugin {

	@Override
	public Component getPlot3DComponent() {
		return new MultiProfileplot3DPanel();
	}

	@Override
	public void initInfos() {
		pluginClass = "multiprofileplot3d";
		description = "Multi Profile Plot representing three dimensional profile data";
		plotName = "3D Multi Profile Plot";
		iconPath = "mayday/vis3/multiprofile3d128.png";
	}
}
