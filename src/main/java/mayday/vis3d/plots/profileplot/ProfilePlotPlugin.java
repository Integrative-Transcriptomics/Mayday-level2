package mayday.vis3d.plots.profileplot;

import java.awt.Component;

import mayday.vis3d.Plot3DPlugin;

/**
 * @author jaeger
 *
 */
public class ProfilePlotPlugin extends Plot3DPlugin {

	@Override
	public Component getPlot3DComponent() {
		return new ProfilePlotPanel();
	}

	@Override
	public void initInfos() {
		pluginClass = "profileplot2d";
		description = "Profile Plot with Label Histogram";
		plotName = "Profile Plot with Label Histogram";
		iconPath = "mayday/vis3/profile3d128.png";
	}
}
