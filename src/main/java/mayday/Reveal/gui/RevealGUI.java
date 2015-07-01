package mayday.Reveal.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;

import mayday.Reveal.RevealPlugin;
import mayday.Reveal.actions.ExitReveal;
import mayday.Reveal.actions.snplist.SNVListPlugin;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.meta.manipulation.MIManipulationPlugin;
import mayday.Reveal.functions.prerequisite.PrerequisiteChecker;
import mayday.Reveal.gui.menu.MetaInformationPopupMenu;
import mayday.Reveal.gui.menu.RevealMenuBar;
import mayday.Reveal.gui.menu.SNPListPopupMenu;
import mayday.Reveal.settings.SettingPanelCreator;
import mayday.Reveal.utilities.SNVLists;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;
import mayday.core.gui.MaydayFrame;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.components.DetachablePlot;
import mayday.vis3.components.PlotScrollPane;
import mayday.vis3.model.Visualizer;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class RevealGUI extends MaydayFrame {

	private RevealMenuBar menuBar;
	private JTree dataPanel;
	private JTree metaDataPanel;
	private JTabbedPane plotsPanel;
//	private JPanel overviewPanel;
//	
//	private JSplitPane centerSplitPane;
	
	private RevealToolBar toolBar;
	
	private SNPListPopupMenu snplistPopupMenu;
	private MetaInformationPopupMenu metaInformationPopupMenu;
	
	private ProjectHandler projectHandler;
	
	/**
	 * @param projectHandler 
	 * @param data
	 */
	public RevealGUI(ProjectHandler projectHandler) {
		super("Reveal - Visual eQTL Analytics");
		
		this.projectHandler = projectHandler;
		
		this.setLayout(new BorderLayout());
		
		menuBar = new RevealMenuBar();
		this.setJMenuBar(menuBar);
		
		toolBar = new RevealToolBar(this);
		this.add(toolBar, BorderLayout.NORTH);
		
		dataPanel = new RevealDataPanel(this);
		JScrollPane dataScroller = new JScrollPane(dataPanel);
		metaDataPanel = new RevealMetaDataPanel(this);
		JScrollPane metaDataScroller = new JScrollPane(metaDataPanel);
		
		plotsPanel = new RevealTabbedPane();
		
		snplistPopupMenu = new SNPListPopupMenu(projectHandler);
		metaInformationPopupMenu = new MetaInformationPopupMenu(projectHandler);
		
//		overviewPanel = new RevealOverviewPanel(this);
		
//		centerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
//                overviewPanel, plotsPanel);
//		centerSplitPane.setDividerLocation(0.15);
//		centerSplitPane.setResizeWeight(0.15);
//		centerSplitPane.setContinuousLayout(true);
//		centerSplitPane.setOneTouchExpandable(true);
		
		JSplitPane projectPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dataScroller, metaDataScroller);
		projectPane.setDividerLocation(0.5);
		projectPane.setResizeWeight(0.5);
		projectPane.setContinuousLayout(true);
		projectPane.setOneTouchExpandable(true);
		
		dataScroller.setBorder(BorderFactory.createTitledBorder("Projects"));
		metaDataScroller.setBorder(BorderFactory.createTitledBorder("Meta-Information"));
		
		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
				projectPane, plotsPanel);
		leftSplitPane.setDividerLocation(0.15);
		leftSplitPane.setResizeWeight(0.15);
		leftSplitPane.setContinuousLayout(true);
		leftSplitPane.setOneTouchExpandable(true);
		
		this.add(leftSplitPane, BorderLayout.CENTER);
		
//		this.setMinimumSize(new Dimension(800, 600));
		this.setPreferredSize(new Dimension(1024, 768));
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ExitReveal a = new ExitReveal();
				a.setProjectHandler(RevealGUI.this.projectHandler);
				a.run(null);
			}
		});

		this.pack();
	}

	/**
	 * @return the associated projectHandler
	 */
	public ProjectHandler getProjectHandler() {
		return this.projectHandler;
	}
	
	public void addPlotItem(final RevealVisualizationPlugin plotPlugin) {
		if(plotPlugin.getMenu() != null) {
			AbstractAction a = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(projectHandler.getSelectedProject() == null) {
						JOptionPane.showMessageDialog(RevealGUI.this, "No project selected", "Missing Prerequisites", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					RevealVisualization plot = plotPlugin.getComponent();
					List<Integer> prerequisites = plot.getPrerequisites();
					
					boolean fulfilled = PrerequisiteChecker.checkPrerequisites(projectHandler.getSelectedProject(), prerequisites);
					
					if(!fulfilled) {
						JOptionPane.showMessageDialog(RevealGUI.this, PrerequisiteChecker.ERROR_MESSAGE, "Missing Prerequisites", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					Set<SNVList> selection = plotPlugin.getProjectHandler().getSelectedSNVLists();
					String snpListNames = SNVLists.createUniqueSNVListName(selection);
					String title = plotPlugin.getMenuName() + " (" + snpListNames + ")";
					
					if(plotPlugin.usesViewSetting()) {
						JSplitPane splitPane = displayPlot(title, plot, !plotPlugin.usesScrollPane(), true);
						HierarchicalSetting viewSetting = plot.getViewSetting();
						SettingComponent plotSetting = viewSetting.getGUIElement();
						
						JPanel settingPanel = SettingPanelCreator.getSettingPanel(plotSetting);
						splitPane.setRightComponent(settingPanel);
					} else {
						displayPlot(title, plot, !plotPlugin.usesScrollPane(), false);
					}
				}
			};
			
			menuBar.addItem(plotPlugin.getMenuName(), plotPlugin.getDescription(), plotPlugin.getMenu(), a);
			
			if(plotPlugin.showInToolbar())
				toolBar.addItem(plotPlugin, a);
		}
	}
	
	/**
	 * @param name 
	 * @param c
	 */
	public JSplitPane displayPlot(String name, Component plot, boolean scrollPane, boolean useViewSetting) {
		if(useViewSetting) {
			Component plotPanel = registerPlot(plot, scrollPane);
			
			JSplitPane plotPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			plotPane.setOneTouchExpandable(true);
			plotPane.setResizeWeight(1.0);
			plotPane.setLeftComponent(plotPanel);
			
			this.plotsPanel.addTab(name, plotPane);
			
			return plotPane;
		} else {
			this.plotsPanel.addTab(name, registerPlot(plot, scrollPane));
			return null;
		}
	}
	
	private Component registerPlot(Component plot, boolean useScrollPane) {
		PlotScrollPane scrollPane = null;
		if(useScrollPane) {
			scrollPane = new PlotScrollPane(plot);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		
		Visualizer ncv = projectHandler.getViewModel(projectHandler.getSelectedProject()).getVisualizer();
		
		DetachablePlot dp = new DetachablePlot(useScrollPane ? scrollPane : plot, ncv, "", false);
		dp.setCollapsible(false);

		return dp;
	}
	
	public void removeNotify() {
		this.projectHandler.clear();
		super.removeNotify();
	}

	public void addItem(final RevealPlugin plugin) {
		String menu = plugin.getMenu();
		
		if(plugin.getCategory() != null) {
			if(plugin.getCategory().startsWith(SNVListPlugin.CATEGORY)) {
				snplistPopupMenu.addMenuItem((SNVListPlugin)plugin);
			}
			
			if(plugin.getCategory().startsWith(MIManipulationPlugin.CATEGORY)) {
				metaInformationPopupMenu.addMenuItem((MIManipulationPlugin)plugin);
			}
		}
		
		if(menu != null) {
			AbstractAction a = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Set<SNVList>snpLists = projectHandler.getSelectedSNVLists();
					plugin.run(snpLists);
				}
			};
				
			menuBar.addItem(plugin.getMenuName(), plugin.getDescription(), menu, a);
		}
	}

	public SNPListPopupMenu getSNPListPopupMenu() {
		return this.snplistPopupMenu;
	}
	
	public MetaInformationPopupMenu getMetaInformationPopupMenu() {
		return this.metaInformationPopupMenu;
	}
}
