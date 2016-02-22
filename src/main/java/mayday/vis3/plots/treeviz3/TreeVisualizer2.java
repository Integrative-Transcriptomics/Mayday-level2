package mayday.vis3.plots.treeviz3;

import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotScrollPane;
import mayday.vis3.components.PlotWithLegendAndTitle;

/**
 * @author Eugen Netz
 */
public class TreeVisualizer2 extends PlotPlugin {

	@Override
	public Component getComponent() {
		PlotWithLegendAndTitle myComponent;
		PathPainter pathPainter = new PathPainter();
		myComponent = new PlotWithLegendAndTitle(new PlotScrollPane(pathPainter));
		myComponent.setTitledComponent(pathPainter);
		return myComponent;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.clustering.TreeVisualizer3",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Eugen Netz",
				"treeviz3.netz@informatik.uni-tuebingen.de",
				"Visualizer for trees derived from clusterings",
				"Tree Visualizer Advanced"
		);
		pli.setIcon("mayday/vis3/tree128.png");
		return pli;
	}

	@Override
	public void init() {}
}
