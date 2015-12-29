package mayday.tiala.pairwise.gui;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import mayday.core.gui.MaydayFrame;
import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.tiala.pairwise.data.AlignmentStoreEvent;
import mayday.tiala.pairwise.data.AlignmentStoreListener;
import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.gui.controls.AlignmentOverviewControl;
import mayday.tiala.pairwise.gui.probelistlist.ProbeListView;
import mayday.tiala.pairwise.gui.views.AlignmentProfileView;
import mayday.tiala.pairwise.gui.views.ProfileView;
import mayday.tiala.pairwise.gui.views.StatisticsView;
import mayday.vis3.components.DetachableComponent;

@SuppressWarnings("serial")
public class MainFrame extends MaydayFrame implements AlignmentStoreListener{
	
	public static final Color c1 = new Color(65,105,225); // RoyalBlue
	public static final Color c2 = new Color(240,128,128); // LightCoral
	
	public static final Color sc1 = Color.blue;//new Color(0,0,128); // Navy
	public static final Color sc2 = Color.red; //new Color(139,0,0); // DarkRed
	
	public AlignmentStore store;
	
	public MainFrame(AlignmentStore Store) {
		super("Timeseries Alignment Analysis - Mayday Tiala");

		store = Store;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		
		// ============= LEFT COLUMN: input datasets and probelist selection
		JPanel leftColumn = new JPanel(new ExcellentBoxLayout(true, 5));
		leftColumn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));		
		leftColumn.add(new ProfileView(sc1,store.getVisualizerOne()));
		leftColumn.add(new ProfileView(sc2,store.getVisualizerTwo()));
		leftColumn.add(new DetachableComponent(new ProbeListView(store), "Probelists"));


		// ============= RIGHT COLUMN: Combined View & Derived Statistics
		JSplitPane rightColumn = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightColumn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		rightColumn.setTopComponent(new AlignmentProfileView(new Color[]{c1,c2}, new Color[]{sc1,sc2}, store.getVisualizerPaired(), store));
		rightColumn.setBottomComponent(new StatisticsView(store));
		rightColumn.setOneTouchExpandable(true);
		
		
		// ============= General layout
		JSplitPane splitTop_Left_MidRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftColumn, rightColumn);
		splitTop_Left_MidRight.setResizeWeight(0.0); // all resizing affects only the center
		splitTop_Left_MidRight.setDividerLocation(0.33);
		splitTop_Left_MidRight.setOneTouchExpandable(true);
		splitTop_Left_MidRight.setContinuousLayout(true);
		
		add(splitTop_Left_MidRight, BorderLayout.CENTER);
		
		
		// Bottom panel 		
		add(new DetachableComponent(new AlignmentOverviewControl(store),"Dataset Alignment"), BorderLayout.SOUTH);

		setMinimumSize(new Dimension(960,700));
		
		pack();
		
		// has to be done after the size is determined
		rightColumn.setDividerLocation(.5);
		splitTop_Left_MidRight.setDividerLocation(0.33); 
		
		store.addListener(this);
		
	}
	
	public void dispose() {
		super.dispose();
		store.dispose();
	}
	
	public void alignmentChanged(AlignmentStoreEvent evt) {
		if (evt.getChange()==AlignmentStoreEvent.STORE_CLOSED) {
			dispose();
		}
	}

}
