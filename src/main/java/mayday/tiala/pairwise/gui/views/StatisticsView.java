package mayday.tiala.pairwise.gui.views;

import java.awt.BorderLayout;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.data.AlignmentStoreEvent;
import mayday.tiala.pairwise.data.AlignmentStoreListener;
import mayday.tiala.pairwise.gui.actions.Statistics2DataSetAction;
import mayday.tiala.pairwise.gui.actions.Statistics2MIOAction;
import mayday.tiala.pairwise.gui.controls.StatisticsConfigureButton;
import mayday.tiala.pairwise.gui.controls.StatisticsControl;
import mayday.tiala.pairwise.gui.plots.StatisticsBoxPlotComponent;
import mayday.tiala.pairwise.gui.plots.StatisticsOneDimensionalBoxPlotComponent;
import mayday.tiala.pairwise.gui.plots.StatisticsProfilePlotComponent;
import mayday.vis3.components.DetachablePlot;
import mayday.vis3.plots.histogram.HistogramPlotComponent;

@SuppressWarnings("serial")
public class StatisticsView extends DetachablePlot implements AlignmentStoreListener {

	JTabbedPane moreThanOne_Tabs= new JTabbedPane();
	JTabbedPane onlyOne_Tabs= new JTabbedPane();
	BorderLayout layout;
	AlignmentStore store;	

	public StatisticsView(AlignmentStore Store) {

		super(null, Store.getVisualizerStatistics(), "Combined statistics", true);

		store = Store;

		// more than one experiments case
		StatisticsProfilePlotComponent statppc = new StatisticsProfilePlotComponent();		
		StatisticsBoxPlotComponent boxpc = new StatisticsBoxPlotComponent();
		moreThanOne_Tabs.add("Profile plot", statppc);
		moreThanOne_Tabs.add("Box plots", boxpc);

		// only one experiment case
		StatisticsOneDimensionalBoxPlotComponent boxpc2 = new StatisticsOneDimensionalBoxPlotComponent(Store);
		HistogramPlotComponent histc = new HistogramPlotComponent();   
		onlyOne_Tabs.add("Histogram", histc);
		onlyOne_Tabs.add("Box plot", boxpc2);

		JPanel pnl = new JPanel(layout = new BorderLayout());
		pnl.add(moreThanOne_Tabs, BorderLayout.CENTER);
		pnl.add(onlyOne_Tabs, BorderLayout.CENTER);
		
		setPlot(pnl);
		setPlot();

		Store.addListener(this);
		
		setCollapsible(false);
	}

	public void setPlot() {
		if (store.getProbeStatistic()==null)
			return;
		boolean oneVis = store.getProbeStatistic().getOutputDimension()==1;
		onlyOne_Tabs.setVisible(oneVis);
		moreThanOne_Tabs.setVisible(!oneVis);
		if (oneVis)
			layout.addLayoutComponent(onlyOne_Tabs, BorderLayout.CENTER);
		else
			layout.addLayoutComponent(moreThanOne_Tabs, BorderLayout.CENTER);
		// force update
		store.getVisualizerStatistics().getViewModel().getDataManipulator().setManipulation(
				store.getVisualizerStatistics().getViewModel().getDataManipulator().getManipulation());
		invalidate();
		validate();
	}
	
	public void alignmentChanged(AlignmentStoreEvent evt) {
		if (evt.getChange()==AlignmentStoreEvent.SCORING_CHANGED || evt.getChange()==AlignmentStoreEvent.SHIFT_CHANGED ||
				evt.getChange()==AlignmentStoreEvent.STATISTIC_CHANGED)
			setPlot();
	}

	@Override
	public void buildMenu() {
		StatisticsControl sControl = new StatisticsControl(store);
		StatisticsConfigureButton sButton = new StatisticsConfigureButton(store);
		menubar.add(sControl);
		menubar.add(sButton);
		
		JMenu sub = new JMenu("Export");
		sub.add(new JMenuItem(new Statistics2DataSetAction(store)));
		sub.add(new JMenuItem(new Statistics2MIOAction(store)));
		menubar.add(sub);
		super.buildMenu();
	}

}
