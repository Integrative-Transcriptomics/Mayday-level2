package mayday.vis3.plots.tagcloud;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;

import mayday.vis3.components.BasicPlotPanel;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.legend.SimpleTitle;

public class TagCloudWithLegendAndTitle extends BasicPlotPanel {

	
	protected HashMap<String,Component> legends = new HashMap<String,Component>();
	protected final static String defaultPosition = BorderLayout.SOUTH;
	
	protected Component centerComponent;
	
	public TagCloudWithLegendAndTitle(Component plot, TagCloudViewer viewer) {
		setLayout(new BorderLayout());
		TagCloudLegend l = new TagCloudLegend(viewer);
//		ProbeListLegend l = new ProbeListLegend();
		setLegend(l);
		TagCloudTitle t = new TagCloudTitle(viewer);
		setTitle(t);
		
		setPlot(plot);
		if (plot instanceof PlotComponent) {
			setTitledComponent((PlotComponent)plot);
		}
	}
	
	public void setTitledComponent(PlotComponent c) {
		for (Component l : legends.values()){
			if (l instanceof SimpleTitle)
				((SimpleTitle)l).setTitledComponent(c);
			if (l instanceof TagCloudLegend)
				((TagCloudLegend)l).setTitledComponent(c);
		}		
	}
	
	public void setTitle(Component title) {
		setLegend(title, BorderLayout.NORTH);
	}
	
	public void setLegend(Component legend) {
		setLegend(legend, defaultPosition);
	}
	
	public void setLegend(Component legend, String position) {
		Component previous;
		if ((previous = legends.remove(position))!=null) {
			remove(previous);
		}
		if (legend!=null) {
			legends.put(position,legend);
			add(legend, position);
		} else {
			legends.remove(position);
		}
	}
	
	public void setPlot(Component plot) {
		add(plot, BorderLayout.CENTER);
		centerComponent = plot;
		setName(plot.getName());
	}
	
	public Component getPlot() {
		return centerComponent;
	}
	
	@Override
	public void updatePlot() {
		System.out.println("Legend Update");
	}

	@Override
	public void setup(PlotContainer plotContainer) {}
}
