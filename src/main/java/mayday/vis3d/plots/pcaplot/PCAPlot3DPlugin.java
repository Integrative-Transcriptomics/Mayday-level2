package mayday.vis3d.plots.pcaplot;

import java.awt.Component;

import mayday.vis3d.Plot3DPlugin;
/**
 * 
 * @author G\u00FCnter J\u00E4ger
 * @date June 10, 2010
 */
public class PCAPlot3DPlugin extends Plot3DPlugin {

	@Override
	public Component getPlot3DComponent() {
		return new PCAPlot3DPanel();
	}

	@Override
	public void initInfos() {
		pluginClass = "pcaplot3d";
		description = "Three dimensional Principal Component Analysis Plot";
		plotName = "3D PCA Plot";
		iconPath = "mayday/vis3/pca3d128.png";
	}
}
