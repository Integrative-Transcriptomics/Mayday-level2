package mayday.visualizations.multiboxscatterplot;



import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotScrollPane;
import mayday.vis3.components.PlotWithLegendAndTitle;

public class MultiBoxScatterPlot extends PlotPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.incubator.MultiBoxScatterPlot",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Alicia Owen",
				"alicia.owen@student.uni-tuebingen.de",
				"A box-scatterplot for each probe in the probelist",
				"Multi box scatter Plot"
		);
		pli.setIcon("mayday/vis3/multiprofile128.png");
		pli.addCategory("Distributions");
		return pli;	
	}

	public Component getComponent() {
		PlotWithLegendAndTitle myComponent;
		MultiBoxScatterplotComponent mpp = new MultiBoxScatterplotComponent();
		myComponent = new PlotWithLegendAndTitle(new PlotScrollPane(mpp));
		myComponent.setTitledComponent(null);
		return myComponent;
	}

}
