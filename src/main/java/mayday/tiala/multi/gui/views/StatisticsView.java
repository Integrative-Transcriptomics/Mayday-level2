package mayday.tiala.multi.gui.views;

import java.awt.BorderLayout;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;
import mayday.tiala.multi.data.AlignmentStoreListener;
import mayday.tiala.multi.gui.actions.Statistics2DataSetAction;
import mayday.tiala.multi.gui.actions.Statistics2MIOAction;
import mayday.tiala.multi.gui.controls.StatisticsConfigureButton;
import mayday.tiala.multi.gui.controls.StatisticsControl;
import mayday.tiala.multi.gui.plots.StatisticsBoxPlotComponent;
import mayday.tiala.multi.gui.plots.StatisticsOneDimensionalBoxPlotComponent;
import mayday.tiala.multi.gui.plots.StatisticsProfilePlotComponent;
import mayday.vis3.components.DetachablePlot;
import mayday.vis3.plots.histogram.HistogramPlotComponent;

@SuppressWarnings("serial")
public class StatisticsView extends DetachablePlot implements AlignmentStoreListener {

	JTabbedPane moreThanOne_Tabs= new JTabbedPane();
	JTabbedPane onlyOne_Tabs= new JTabbedPane();
	BorderLayout layout;
	AlignmentStore store;	
	
	protected int ID;

	public StatisticsView(int ID, AlignmentStore Store) {
		super(null, Store.getVisualizerStatistics(ID), "Statistics", true);
		setCollapsible(false);

		store = Store;
		this.ID = ID;

		// more than one experiments case
		StatisticsProfilePlotComponent statppc = new StatisticsProfilePlotComponent();
		StatisticsBoxPlotComponent boxpc = new StatisticsBoxPlotComponent();
		moreThanOne_Tabs.add("Profile plot", statppc);
		moreThanOne_Tabs.add("Box plot", boxpc);

		// only one experiment case
		StatisticsOneDimensionalBoxPlotComponent boxpc2 = new StatisticsOneDimensionalBoxPlotComponent(ID, Store);
		HistogramPlotComponent histc = new HistogramPlotComponent();   
		onlyOne_Tabs.add("Histogram", histc);
		onlyOne_Tabs.add("Box plot", boxpc2);

		JPanel pnl = new JPanel(layout = new BorderLayout());
		pnl.add(moreThanOne_Tabs, BorderLayout.CENTER);
		pnl.add(onlyOne_Tabs, BorderLayout.CENTER);
		
		setPlot(pnl);
		setPlot();

		Store.addListener(this);
	}

	public void setPlot() {
		if (store.getProbeStatistic(ID) == null)
			return;
		
		boolean oneVis = store.getProbeStatistic(ID).getOutputDimension() == 1;
		onlyOne_Tabs.setVisible(oneVis);
		moreThanOne_Tabs.setVisible(!oneVis);
		
		if (oneVis)
			layout.addLayoutComponent(onlyOne_Tabs, BorderLayout.CENTER);
		else
			layout.addLayoutComponent(moreThanOne_Tabs, BorderLayout.CENTER);
		
		// force update
		store.getVisualizerStatistics(ID).getViewModel().getDataManipulator().setManipulation(
				store.getVisualizerStatistics(ID).getViewModel().getDataManipulator().getManipulation());
		
		invalidate();
		validate();
	}
	
	public void alignmentChanged(AlignmentStoreEvent evt) {
		switch(evt.getChange()) {
		case AlignmentStoreEvent.SCORING_CHANGED:
			setPlot();
			break;
		case AlignmentStoreEvent.SHIFT_CHANGED:
			setPlot();
			break;
		case AlignmentStoreEvent.STATISTIC_CHANGED:
			setPlot();
			break;
		}
	}

	@Override
	public void buildMenu() {
		StatisticsControl sControl = new StatisticsControl(ID, store);
		StatisticsConfigureButton sButton = new StatisticsConfigureButton(ID, store);
		menubar.add(sControl);
		menubar.add(sButton);
		
		JMenu sub = new JMenu("Export");
		sub.add(new JMenuItem(new Statistics2DataSetAction(ID, store)));
		sub.add(new JMenuItem(new Statistics2MIOAction(ID, store)));
		menubar.add(sub);
		super.buildMenu();
	}
	
	public void dispose() {
		store.removeListener(this);
	}
}
