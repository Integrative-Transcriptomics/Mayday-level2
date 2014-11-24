package mayday.Reveal.gui.menu;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class RevealMenuBar extends JMenuBar {
	
	protected JMenu fileMenu;
	protected JMenu visMenu;
	protected JMenu helpMenu;
//	protected JMenu projectMenu;
	protected JMenu snpListMenu;
//	
//	protected JMenu statisticsMenu;
	protected JMenu metaInformationMenu;
	
	protected RevealMenuTree revealMenuTree;
	
	/**
	 * construct a new Reveal Menubar
	 * @param gui 
	 */
	public RevealMenuBar() {
		fileMenu = new JMenu("File");
		revealMenuTree = new RevealMenuTree();
		
//		final NewProject newProject = new NewProject();
//		newProject.setProjectHandler(gui.getProjectHandler());
//		JMenuItem newProjectItem = new JMenuItem(newProject.getMenuName());
//		newProjectItem.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				newProject.run(null);
//			}
//		});
//		
//		fileMenu.add(newProjectItem);
		
//		final LoadProject loadProject = new LoadProject();
//		loadProject.setProjectHandler(gui.getProjectHandler());
//		JMenuItem loadProjectItem = new JMenuItem(loadProject.getMenuName());
//		loadProjectItem.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				loadProject.run(null);
//			}
//		});
//		
//		fileMenu.add(loadProjectItem);
		
//		final SaveProject saveProject = new SaveProject();
//		saveProject.setProjectHandler(gui.getProjectHandler());
//		JMenuItem saveProjectItem = new JMenuItem(saveProject.getMenuName());
//		saveProjectItem.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				saveProject.run(null);
//			}
//		});
//		
//		fileMenu.add(saveProjectItem);
		
//		fileMenu.addSeparator();
//		
//		JMenuItem exitItem = new JMenuItem("Exit");
//		exitItem.addActionListener(new ExitRevealAction(gui.getProjectHandler(), gui));
//		
//		projectMenu = new JMenu("Project");
		
//		JMenu importMenu = new JMenu("Import...");
//		JMenuItem importSingleLocusResults = new JMenuItem("PLINK Single Locus Results");
//		JMenuItem importTwoLocusResults = new JMenuItem("PLINK Two Locus Results");
//		JMenuItem importExternalSNPs = new JMenuItem("External SNPs");
//		JMenuItem importLDResults = new JMenuItem("LD Results");
//		
//		importExternalSNPs.addActionListener(new ImportExternalSNPsAction(this.gui.getProjectHandler()));
//		importLDResults.addActionListener(new ImportLDResultsAction(gui.getProjectHandler()));
//		
//		importMenu.add(importSingleLocusResults);
//		importMenu.add(importTwoLocusResults);
//		importMenu.add(importExternalSNPs);
//		importMenu.add(importLDResults);
//		
//		projectMenu.add(importMenu);
//		
		metaInformationMenu = new JMenu("Meta Information");
//		
//		statisticsMenu = new JMenu("Statistics");
//		
		snpListMenu = new JMenu("SNPList");
//		
		visMenu = new JMenu("Visualizations");
//		
		helpMenu = new JMenu("Help");
		
//		JMenuItem aboutItem = new JMenuItem("About");
//		aboutItem.addActionListener(new AboutRevealAction(gui.getProjectHandler()));
//		
//		JMenuItem helpItem = new JMenuItem("Help");
//		helpItem.addActionListener(new HelpAction(gui.getProjectHandler()));
//		
//		helpMenu.add(helpItem);
//		helpMenu.addSeparator();
//		helpMenu.add(aboutItem);
//		
		this.add(fileMenu);
		this.revealMenuTree.addRootMenu(fileMenu.getText(), fileMenu);
//		this.add(projectMenu);
		this.add(snpListMenu);
		this.revealMenuTree.addRootMenu(snpListMenu.getText(), snpListMenu);
		this.add(metaInformationMenu);
		this.revealMenuTree.addRootMenu(metaInformationMenu.getText(), metaInformationMenu);
		this.add(visMenu);
		this.revealMenuTree.addRootMenu(visMenu.getText(), visMenu);
		this.add(helpMenu);
		this.revealMenuTree.addRootMenu(helpMenu.getText(), helpMenu);
		
//		JMenu exportMenu = new JMenu("Export");
//		
//		JMenuItem exportSNPsGeneWiseItem = new JMenuItem("SNPs gene wise");
//		exportSNPsGeneWiseItem.addActionListener(new ExportSNPsGeneWiseAction(gui.getProjectHandler()));
//		exportMenu.add(exportSNPsGeneWiseItem);
//		
//		JMenuItem exportSLRSNPItem = new JMenuItem("SLR SNPs gene wise");
//		exportSLRSNPItem.addActionListener(new ExportSLRSNPsAction(gui.getProjectHandler()));
//		exportMenu.add(exportSLRSNPItem);
//		
//		JMenuItem exportSNPListItem = new JMenuItem("SNPList");
//		exportSNPListItem.addActionListener(new ExportSNPList(gui.getProjectHandler()));
//		exportMenu.add(exportSNPListItem);
//		
//		JMenuItem exportSNPExpressionMatrices = new JMenuItem("SNP based Expression Matrices");
//		exportSNPExpressionMatrices.addActionListener(new CalculateSNPMatricesAction(gui.getProjectHandler()));
//		exportMenu.add(exportSNPExpressionMatrices);
//		
//		exportMenu.addSeparator();
//		
//		JMenuItem exportLDSInfo = new JMenuItem("LDS Information");
//		exportLDSInfo.addActionListener(new ExortLDStructureInformationAction(gui.getProjectHandler()));
//		exportMenu.add(exportLDSInfo);
//		
//		exportMenu.addSeparator();
		
//		JMenuItem exportSNPNetwork2GML = new JMenuItem("TL SNP Network to GML");
//		exportSNPNetwork2GML.addActionListener(new ExportSNPNetwork2GML(gui.getProjectHandler()));
//		exportMenu.add(exportSNPNetwork2GML);
//		
//		projectMenu.add(exportMenu);
//		
//		JMenuItem calcLDStructure = new JMenuItem("Calculate LD structure");
//		calcLDStructure.addActionListener(new CalculateLDStructure(gui.getProjectHandler()));
//		
//		metaInformationMenu.add(calcLDStructure);
//		
//		projectMenu.addSeparator();
//		projectMenu.add(metaInformationMenu);
//		projectMenu.addSeparator();
//		
//		snpListMenu.add(statisticsMenu);
//		
//		fileMenu.add(exitItem);
	}
	
	/**
	 * @param title
	 * @param description 
	 * @param toMenu
	 * @param a
	 */
	public void addItem(String title, String description, String toMenu, Action a) {
		addToMenu(this, toMenu, title, description, a);
	}
	
	private void addToMenu(Component menuContainer, String toMenu, String title, String description, Action a) {
		String[] singleMenus = toMenu.split("/");
		Component current = null;
		
		for(int i = 0; i < singleMenus.length; i++) {
			if(revealMenuTree.hasItem(singleMenus[i])) {
				current = revealMenuTree.getItem(singleMenus[i]);
			} else {
				if(i == 0) {
					JMenu rootMenu = new JMenu(singleMenus[i]);
					this.add(rootMenu);
					revealMenuTree.addRootMenu(rootMenu.getText(), rootMenu);
					current = rootMenu;
				} else {
					JMenu menu = new JMenu(singleMenus[i]);
					revealMenuTree.addItem((JMenu)current, menu, menu.getText());
					current = menu;
				}
			}
		}
		
		JMenuItem item = new JMenuItem(title);
		item.getAccessibleContext().setAccessibleDescription(description);
		item.addActionListener(a);
		revealMenuTree.addItem((JMenu)current, item, item.getText());
	}
}
