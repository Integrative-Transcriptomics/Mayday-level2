package mayday.tiala.pairwise.gui.views;

import java.awt.Color;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.gui.actions.Alignment2DataSetsAction;
import mayday.tiala.pairwise.gui.plots.AlignmentProfilePlotComponent;
import mayday.vis3.components.DetachablePlot;
import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public class AlignmentProfileView extends DetachablePlot {

	protected Alignment2DataSetsAction exportAction;
	protected AlignmentProfilePlotComponent profiles;
	protected Color[] selectionColors;
	
	public AlignmentProfileView(Color[] colors, Color[] selectionColors, Visualizer viz, AlignmentStore store) {
		
		super(viz, viz.getViewModel().getDataSet().getName());
		
		this.selectionColors = selectionColors;
		profiles = new AlignmentProfilePlotComponent();		
		profiles.setColors(colors[0],colors[1]);
		profiles.setPreferredSize(null);
		
		setPlot(profiles);
		exportAction = new Alignment2DataSetsAction(store);
		
		setCollapsible(false);
	}
	
	public void addNotify() {
		super.addNotify();
		// has to be done late enough
		profiles.setSelectionColors(selectionColors[0], selectionColors[1]);
		profiles.setInferMissing(true);
		
	}
	
	@Override
	public void buildMenu() {
		JMenu sub = new JMenu("Export");
		sub.add(new JMenuItem(exportAction));
		menubar.add(sub);
		super.buildMenu();
	}

	
}
