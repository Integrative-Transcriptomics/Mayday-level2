package mayday.vis3d.plots.radialprofileplot;

import java.awt.Component;

import mayday.vis3d.Plot3DPlugin;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class RadialProfileplotPlugin extends Plot3DPlugin {

	@Override
	public Component getPlot3DComponent() {
		return new RadialProfileplotPanel();
	}

	@Override
	public void initInfos() {
		pluginClass = "radialprofileplot";
		description = "Radial Profile Plot";
		plotName = "Radial Profile Plot";
		iconPath = "mayday/vis3/radialprofile128.png";
	}
}
