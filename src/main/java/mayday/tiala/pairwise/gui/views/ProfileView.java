package mayday.tiala.pairwise.gui.views;

import java.awt.Color;

import mayday.vis3.components.DetachablePlot;
import mayday.vis3.model.Visualizer;
import mayday.vis3.plots.profile.ProfilePlotComponent;

@SuppressWarnings("serial")
public class ProfileView extends DetachablePlot {

	protected ProfilePlotComponent profiles ;
	protected Color c;
	
	public ProfileView(Color selectionColor, Visualizer viz) {
		
		super(viz, viz.getViewModel().getDataSet().getName());
		
		profiles = new ProfilePlotComponent();
		c  = selectionColor;
		profiles.setPreferredSize(null);
		
		
		setPlot(profiles);

	}
	
	public void addNotify() {
		super.addNotify();
		// has to be done late enough
		profiles.setSelectionColor(c);
	}
	
	
}
