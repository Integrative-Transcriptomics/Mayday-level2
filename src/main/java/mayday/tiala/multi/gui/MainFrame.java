package mayday.tiala.multi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import mayday.core.ProbeList;
import mayday.core.gui.MaydayFrame;
import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;
import mayday.tiala.multi.data.AlignmentStoreListener;
import mayday.tiala.multi.gui.controls.AlignmentOverviewControl;
import mayday.tiala.multi.gui.probelistlist.ProbeListView;
import mayday.tiala.multi.gui.views.Alignment2DProfileView;
import mayday.tiala.multi.gui.views.Alignment3DProfileView;
import mayday.tiala.multi.gui.views.ProfileView;
import mayday.tiala.multi.gui.views.StatisticsView;
import mayday.vis3.components.DetachableComponent;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends MaydayFrame implements AlignmentStoreListener {

	protected AlignmentStore store;
	protected ProfileView[] profileViews;
	protected StatisticsView[] statisticViews;
	protected Alignment2DProfileView apv2;
	protected Alignment3DProfileView apv3;
	protected AlignmentOverviewControl aoc;
	
	/**
	 * @param Store
	 */
	public MainFrame(AlignmentStore Store) {
		super("Mayday Tiala - Timeseries Alignment Analysis");
		
		store = Store;
		store.addListener(this);

		this.setupGUI();
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	private JTabbedPane statisticsViewPane;
	private JTabbedPane alignmentViewsPane;
	private JPanel profileViewsPanel;
	private ProbeListView probeListView;
	
	
	/**
	 * setup the GUI
	 */
	public void setupGUI() {
		setLayout(new BorderLayout());
		
		JSplitPane splitTop_Left_MidRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JPanel(), new JPanel());
		JSplitPane rightColumn = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		// ============= LEFT COLUMN: input datasets and probelist selection
		addProfileViews();

		// ============= RIGHT COLUMN: Combined View & Derived Statistics
		addStatisticViews();
		
		rightColumn = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightColumn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		addAlignmentViews();
		
		DetachableComponent dcTop = new DetachableComponent(alignmentViewsPane, "Alignment Visualizations");
		dcTop.setCollapsible(false);
		
		rightColumn.setTopComponent(dcTop);
		
		DetachableComponent dcBottom = new DetachableComponent(statisticsViewPane, "Combined statistics");
		dcBottom.setCollapsible(false);
		
		rightColumn.setBottomComponent(dcBottom);
		
		rightColumn.setOneTouchExpandable(true);
		
		// ============= General layout
		splitTop_Left_MidRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, profileViewsPanel, rightColumn);
		splitTop_Left_MidRight.setResizeWeight(0.0); // all resizing affects only the center
		splitTop_Left_MidRight.setOneTouchExpandable(true);
		splitTop_Left_MidRight.setContinuousLayout(true);
		
		add(splitTop_Left_MidRight, BorderLayout.CENTER);
		
		// Bottom panel 		
		add(new DetachableComponent(aoc = new AlignmentOverviewControl(store), "Dataset Alignment"), BorderLayout.SOUTH);

		setMinimumSize(new Dimension(960,700));
		
		pack();
		
		// has to be done after the size is determined
		rightColumn.setDividerLocation(.33);
		splitTop_Left_MidRight.setDividerLocation(0.5);
	}
	
	
	public void dispose() {
		store.removeListener(this);
		for(int i = 0; i < profileViews.length; i++)
			profileViews[i].dispose();
		for(int i = 0; i < statisticViews.length; i++)
			statisticViews[i].dispose();
		aoc.dispose();
		apv2.dispose();
		store.dispose();
		super.dispose();
	}

	@Override
	public void alignmentChanged(AlignmentStoreEvent evt) {
		int change = evt.getChange();
		switch(change) {
		case AlignmentStoreEvent.STORE_CLOSED:
			dispose();
			break;
		case AlignmentStoreEvent.CENTER_CHANGED:
			processCenterChanged();
			break;
		}
	}
	
	public void processCenterChanged() {
		
		addStatisticViews();
		statisticsViewPane.revalidate();
		
		addAlignmentViews();
		alignmentViewsPane.revalidate();
		
		List<ProbeList> pls = probeListView.getProbeLists();
		addProfileViews();
		for(ProbeList pl : pls) {
			store.addProbeListToViewModels(pl);
		}
		profileViewsPanel.revalidate();
		
		store.fireScoringChanged();
	}
	
	private void addAlignmentViews() {
		if(alignmentViewsPane == null) {
			alignmentViewsPane = new JTabbedPane();
		} else {
			alignmentViewsPane.removeAll();
		}
		
		alignmentViewsPane.add("2D Profile Plot", apv2 = new Alignment2DProfileView(store.getVisualizerCombined(), store));
		alignmentViewsPane.add("3D Profile Plot", apv3 = new Alignment3DProfileView(store.getVisualizerCombined(), store));
	}


	private void addStatisticViews() {
		int numDataSets = store.getAlignedDataSets().getNumberOfDataSets();
		String firstName = store.getAlignedDataSets().getDataSet(0).getName();
		
		if(statisticsViewPane == null) {
			statisticsViewPane = new JTabbedPane();
		} else {
			statisticsViewPane.removeAll();
		}
		
		if(statisticViews == null) {
			statisticViews = new StatisticsView[numDataSets-1];
		}
		
		for(int i = 1; i < numDataSets; i++) {
			String nextName = store.getAlignedDataSets().getDataSet(i).getName();
			statisticsViewPane.add(firstName + " vs. " + nextName, statisticViews[i-1] = new StatisticsView(i-1, store));
		}
	}
	
	private void addProfileViews() {
		int numDataSets = store.getAlignedDataSets().getNumberOfDataSets();
		
		if(profileViewsPanel == null) {
			profileViewsPanel = new JPanel(new ExcellentBoxLayout(true, 5));
			profileViewsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		} else {
			profileViewsPanel.removeAll();
		}
		
		if(profileViews == null) {
			profileViews = new ProfileView[numDataSets];
		}
		
		for(int i = 0; i < numDataSets; i++) {
			profileViewsPanel.add(profileViews[i] = new ProfileView(i, store, store.getVisualizer(i)));
		}
		
		profileViewsPanel.add(new DetachableComponent(probeListView = new ProbeListView(store), "Probelists"));
	}
}
