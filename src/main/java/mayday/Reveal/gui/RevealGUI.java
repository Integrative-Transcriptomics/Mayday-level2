package mayday.Reveal.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.EmptyMultipleCDockableFactory;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.gui.dock.common.grouping.PlaceholderGrouping;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.perspective.CGridPerspective;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.util.Path;
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
import mayday.Reveal.utilities.Images;
import mayday.Reveal.utilities.SNVLists;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;
import mayday.core.gui.MaydayFrame;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;
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
	
	private RevealToolBar toolBar;
	
	private SNPListPopupMenu snplistPopupMenu;
	private MetaInformationPopupMenu metaInformationPopupMenu;
	
	private ProjectHandler projectHandler;
	
	private CControl control;
	private MultipleCDockableFactory<MultipleCDockable, MultipleCDockableLayout> factory;
	
	private DefaultMultipleCDockable defaultDockable;
	
	private Set<CDockable> visualizations = new HashSet<CDockable>();
	
	/**
	 * @param projectHandler 
	 * @param data
	 */
	public RevealGUI(ProjectHandler projectHandler) {
		super("Reveal - Visual eQTL Analytics");
		
		this.projectHandler = projectHandler;
		toolBar = new RevealToolBar(this);
		menuBar = new RevealMenuBar();
		
		this.setJMenuBar(menuBar);
		
		dataPanel = new RevealDataPanel(this);
		metaDataPanel = new RevealMetaDataPanel(this);
		
		snplistPopupMenu = new SNPListPopupMenu(projectHandler);
		metaInformationPopupMenu = new MetaInformationPopupMenu(projectHandler);
		
		this.setPreferredSize(new Dimension(1024, 768));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ExitReveal a = new ExitReveal();
				a.setProjectHandler(RevealGUI.this.projectHandler);
				a.run(null);
			}
		});
	}
	
	public void initializeComponents() {		
		this.setLayout(new BorderLayout());

		JScrollPane dataScroller = new JScrollPane(dataPanel);
		JScrollPane metaDataScroller = new JScrollPane(metaDataPanel);
		
		this.control = new CControl(this);
		
		ThemeMap themes = control.getThemes();
		themes.select(ThemeMap.KEY_ECLIPSE_THEME);
		
		this.add(control.getContentArea(), BorderLayout.CENTER);
		this.add(toolBar, BorderLayout.NORTH);
		
		this.factory = new EmptyMultipleCDockableFactory<MultipleCDockable>(){
			@Override
			public MultipleCDockable createDockable() {
				return null;
			}
		};
		
		control.addMultipleDockableFactory("RevealFactory", factory);
		
		CPerspective layout = control.getPerspectives().createEmptyPerspective();
		CGridPerspective center = layout.getContentArea().getCenter();
		center.gridPlaceholder(0, 0, 1, 1, new Path("Projects", "Projects"));
		center.gridPlaceholder(0, 1, 1, 1, new Path("Meta-Information", "Meta-Information"));
		center.gridPlaceholder(1, 0, 5, 2, new Path("Visualization", "Visualization"));
		
		control.getPerspectives().setPerspective(layout, true);
		
		DefaultSingleCDockable dataScrollerDockable = new DefaultSingleCDockable("Projects", "Projects", dataScroller);
		dataScrollerDockable.setResizeLocked(true);
		dataScrollerDockable.setGrouping(new PlaceholderGrouping(control, new Path("Projects", "Projects")));
		
		try {
			FMFile fmf = PluginManager.getInstance().getFilemanager().getFile("/mayday/GWAS/icons/seo1.png");
			ImageIcon dataIcon = new ImageIcon(Images.getScaledImage(ImageIO.read(fmf.getStream()), 20, 20));
			dataScrollerDockable.setTitleIcon(dataIcon);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		DefaultSingleCDockable metaDataScrollerDockable = new DefaultSingleCDockable("Meta-Data","Meta-Data", metaDataScroller);
		metaDataScrollerDockable.setResizeLocked(true);
		metaDataScrollerDockable.setGrouping(new PlaceholderGrouping(control, new Path("Meta-Information", "Meta-Information")));

		try {
			FMFile fmf = PluginManager.getInstance().getFilemanager().getFile("/mayday/GWAS/icons/schedule.png");
			ImageIcon metaDataIcon = new ImageIcon(Images.getScaledImage(ImageIO.read(fmf.getStream()), 20, 20));
			metaDataScrollerDockable.setTitleIcon(metaDataIcon);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		defaultDockable = new DefaultMultipleCDockable( factory );
		defaultDockable.setCloseable(false);
		defaultDockable.setMinimizable(false);
		defaultDockable.setMaximizable(false);
		defaultDockable.setExternalizable(false);
		defaultDockable.setTitleIcon(new ImageIcon());
		defaultDockable.setTitleText("Visualizations");
		defaultDockable.setSticky(true);
		defaultDockable.setGrouping(new PlaceholderGrouping(control, new Path("Visualization", "Visualization")));
		control.addDockable(defaultDockable);
		defaultDockable.setVisible(true);
		
		control.addDockable(dataScrollerDockable);
		dataScrollerDockable.setVisible(true);
		
		control.addDockable(metaDataScrollerDockable);
		metaDataScrollerDockable.setVisible(true);
		
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
					
					displayPlot(title, plot, !plotPlugin.usesScrollPane(), plotPlugin.usesViewSetting());
				}
			};
			
			menuBar.addItem(plotPlugin.getMenuName(), plotPlugin.getDescription(), plotPlugin.getMenu(), a);
			
			if(plotPlugin.showInToolbar())
				toolBar.addItem(plotPlugin, a);
		}
	}
	
	public void displayPlot(String name, Component plot, boolean scrollPane, boolean useViewSetting) {
		DefaultMultipleCDockable dockable = new DefaultMultipleCDockable( factory ) {
			public void setVisible(boolean visible) {
				if(visible == false) {
					boolean removed = visualizations.remove(this);
					if(visualizations.size() == 0 && removed) {
						control.addDockable(defaultDockable);
						defaultDockable.setVisible(true);
					}
					super.setVisible(visible);
				} else {
					super.setVisible(visible);
					if(visualizations.size() == 0) {
						defaultDockable.setVisible(false);
					}
					visualizations.add(this);
				}
			}
		};
		dockable.setTitleText(name);
		dockable.setCloseable(true);
		dockable.setRemoveOnClose(true);
		dockable.add(registerPlot(plot, scrollPane));
		
		dockable.setGrouping(new PlaceholderGrouping(control, new Path("Visualization", "Visualization")));
		control.addDockable(dockable);
		dockable.setVisible(true);
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
