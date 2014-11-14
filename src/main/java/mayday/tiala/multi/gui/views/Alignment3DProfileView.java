package mayday.tiala.multi.gui.views;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.gui.actions.Alignment2DataSetsAction;
import mayday.tiala.multi.gui.actions.MultiProbesAsProbeListsAction3D;
import mayday.tiala.multi.gui.plots.AlignmentMultiProfileplotComponent;
import mayday.vis3.components.DetachablePlot;
import mayday.vis3.components.MultiPlotPanel;
import mayday.vis3.components.PlotWithLegendAndTitle;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.model.Visualizer;

/**
 * 
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class Alignment3DProfileView extends DetachablePlot {
	
	protected Alignment2DataSetsAction exportAction;
	protected AlignmentMultiProfileplotComponent profiles;
	protected MultiProbesAsProbeListsAction3D toProbeListsAction;
	protected AlignmentStore store;
	
	/**
	 * @param viz
	 * @param store
	 */
	public Alignment3DProfileView(Visualizer viz, AlignmentStore store) {
		super(viz, viz.getViewModel().getDataSet().getName());
		
		this.store = store;
		
		exportAction = new Alignment2DataSetsAction(0, store);
		toProbeListsAction = new MultiProbesAsProbeListsAction3D(profiles, store);
		
		profiles = new AlignmentMultiProfileplotComponent(store);
		
		setPlot(profiles);
		
		setCollapsible(false);
	}
	
	public void addNotify() {
		super.addNotify();
		profiles.setSelectionColors(store.getSettings().getSelectionColors());
	}
	
//	private Color[] brighter(Color[] selectionColors) {
//		Color[] brighter = new Color[selectionColors.length];
//		for(int i = 0; i < selectionColors.length; i++) {
//			brighter[i] = selectionColors[i].brighter().brighter();
//		}
//		return brighter;
//	}
	
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
}
