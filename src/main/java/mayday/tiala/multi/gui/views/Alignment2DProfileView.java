package mayday.tiala.multi.gui.views;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;
import mayday.tiala.multi.data.AlignmentStoreListener;
import mayday.tiala.multi.gui.actions.Alignment2DataSetsAction;
import mayday.tiala.multi.gui.actions.MultiProbesAsProbeListsAction2D;
import mayday.tiala.multi.gui.plots.AlignmentProfilePlotComponent;
import mayday.vis3.components.DetachablePlot;
import mayday.vis3.components.MultiPlotPanel;
import mayday.vis3.components.PlotWithLegendAndTitle;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.model.Visualizer;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class Alignment2DProfileView extends DetachablePlot implements AlignmentStoreListener {

	protected Alignment2DataSetsAction exportAction;
	protected AlignmentProfilePlotComponent profiles;
	protected MultiProbesAsProbeListsAction2D toProbeListsAction;
	protected AlignmentStore store;
	
	/**
	 * @param viz
	 * @param store
	 */
	public Alignment2DProfileView(Visualizer viz, AlignmentStore store) {
		super(viz, viz.getViewModel().getDataSet().getName());
		
		this.store = store;
		
		exportAction = new Alignment2DataSetsAction(0, store);
		toProbeListsAction = new MultiProbesAsProbeListsAction2D(profiles, store);
		
		profiles = new AlignmentProfilePlotComponent(store);
		
		setPlot(profiles);
		
		setCollapsible(false);
		
		store.addListener(this);
	}
	
	public void addNotify() {
		super.addNotify();
		profiles.setSelectionColors(store.getSettings().getSelectionColors());
		profiles.setColors(transparent(store.getSettings().getSelectionColors(), 75));
	}
	
	private Color[] transparent(Color[] selectionColors, int value) {
		Color[] newColors = new Color[selectionColors.length];
		for(int i = 0; i < selectionColors.length; i++) {
			int red = selectionColors[i].getRed();
			int green = selectionColors[i].getGreen();
			int blue = selectionColors[i].getBlue();
			newColors[i] = new Color(red, green, blue, value);
		}
		return newColors;
	}
	
	@Override
	public void buildMenu() {
		JMenu sub = new JMenu("Export");
		sub.add(new JMenuItem(exportAction));
		sub.add(new JMenuItem(toProbeListsAction));
		menubar.add(sub);
		super.buildMenu();
	}
	
	protected Component createDetachableComponent(Component plot) {
		Component comp;
		if (multiPlot) {
			comp = new MultiPlotPanel(plot);
		} else {
			comp = new PlotWithLegendAndTitle(plot);
			if (plot instanceof PlotComponent)
				((PlotWithLegendAndTitle)comp).setTitledComponent((PlotComponent)plot);
		}
		return comp;
	}

	/**
	 * 
	 */
	public void updateColors() {
		profiles.updatePlot();
	}

	@Override
	public void alignmentChanged(AlignmentStoreEvent evt) {
		switch(evt.getChange()) {
		case AlignmentStoreEvent.SELECTION_COLOR_CHANGED:
			Color[] newSelColors = store.getSettings().getSelectionColors();
			profiles.setSelectionColors(newSelColors);
			profiles.setColors(transparent(newSelColors, 75));
			profiles.updatePlot();
			break;
		}
	}
	
	public void dispose() {
		store.removeListener(this);
	}
}
