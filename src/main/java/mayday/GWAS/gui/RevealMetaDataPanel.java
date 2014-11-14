package mayday.GWAS.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import mayday.GWAS.actions.metainfo.DisplayMetaInformationAction;
import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.ProjectHandler;
import mayday.GWAS.data.meta.MetaInformation;
import mayday.GWAS.gui.menu.MetaInformationPopupMenu;
import mayday.GWAS.listeners.ProjectEvent;
import mayday.GWAS.listeners.ProjectEventListener;

@SuppressWarnings("serial")
public class RevealMetaDataPanel extends JTree implements TreeSelectionListener, ProjectEventListener {

	private RevealGUI gui;
	private MetaDataTreeModel treeModel;
	
	private Set<MetaInformation> selectedMetaInformation;
	
	public RevealMetaDataPanel(RevealGUI gui) {
		this.gui = gui;
		this.treeModel = new MetaDataTreeModel(gui.getProjectHandler());
		this.setModel(treeModel);
		
		this.addTreeSelectionListener(this);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.setShowsRootHandles(false);
		
		this.setRootVisible(false);
		this.putClientProperty("JTree.lineStyle", "Angled");
		this.setCellRenderer(new RevealTreeCellRenderer());
		
		this.gui.getProjectHandler().getProjectEventHandler().addProjectEventListener(this);
		
		this.selectedMetaInformation = new HashSet<MetaInformation>();
		
		this.addMouseListener(new MetaDataMouseListener());
	}
	
	public void addProject() {
		ProjectHandler projectHandler = gui.getProjectHandler();
		DataStorage dataStorage = projectHandler.getLast();
		
		if(treeModel.contains(dataStorage.getAttribute().getName())) {
			System.out.println("Meta Data for this project has already been added.");
			return;
		}
		
		treeModel.add(dataStorage);
		expandAll();
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreePath[] paths = e.getPaths();
		
		for(int i = 0; i < paths.length; i++) {
			TreePath p = paths[i];
			
			if(e.isAddedPath(p)) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode)p.getLastPathComponent();
				Object o = n.getUserObject();
				if(o instanceof MetaInformation) {
					this.selectedMetaInformation.add((MetaInformation) o);
				}
			} else {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode)p.getLastPathComponent();
				Object o = n.getUserObject();
				if(o instanceof MetaInformation) {
					this.selectedMetaInformation.remove(o);
				}
			}
		}

		if(selectedMetaInformation.size() > 0) {
			gui.getProjectHandler().setSelectedMetaInformation(this.selectedMetaInformation.iterator().next());
//			System.out.println(selectedMetaInformation.iterator().next());
		}
		
		repaint();
	}

	@Override
	public void projectChanged(ProjectEvent pe) {
		DataStorage selectedProject = gui.getProjectHandler().getSelectedProject();
		switch(pe.getChange()) {
		case ProjectEvent.PROJECT_ADDED:
			treeModel.add((DataStorage)pe.getSource());
			break;
		case ProjectEvent.PROJECT_REMOVED:
			treeModel.removeProject(); // fall through
		case ProjectEvent.PROJECT_CHANGED: // fall through
		case ProjectEvent.PROJECT_SELECTION_CHANGED:
			treeModel.updateProject(selectedProject);
			expandAll();
			break;
		}
		
		repaint();
	}
	
	/**
	 * expand all nodes
	 */
	public void expandAll() {
		expandAll(this, true);
	}
	
	/**
	 * collapse all nodes
	 */
	public void collapseAll() {
		expandAll(this, false);
	}
	
	private void expandAll(JTree tree, boolean expand) {
	    TreeNode root = (TreeNode) tree.getModel().getRoot();
	    // Traverse tree from root
	    expandAll(tree, new TreePath(root), expand);
	}
	
	@SuppressWarnings({ "rawtypes" })
	private void expandAll(JTree tree, TreePath parent, boolean expand) {
	    // Traverse children
	    TreeNode node = (TreeNode) parent.getLastPathComponent();
	    if (node.getChildCount() >= 0) {
	        for (Enumeration e = node.children(); e.hasMoreElements();) {
	            TreeNode n = (TreeNode) e.nextElement();
	            TreePath path = parent.pathByAddingChild(n);
	            expandAll(tree, path, expand);
	        }
	    }
	    // Expansion or collapse must be done bottom-up
	    if (expand) {
	        tree.expandPath(parent);
	    } else {
	        tree.collapsePath(parent);
	    }
	}

	private class MetaDataMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			int selRow = getRowForLocation(e.getX(), e.getY());
	        TreePath selPath = getPathForLocation(e.getX(), e.getY());
	        if(selRow != -1) {
	        	if(e.getClickCount() == 1) {
	        		DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
	                Object o = node.getUserObject();
	                
	                if(o instanceof MetaInformation) {
	                	if(gui.getProjectHandler().getSelectedMetaInformation() != null 
	                			&& gui.getProjectHandler().getSelectedMetaInformation().equals(o)) {
	                		if(e.getButton() == MouseEvent.BUTTON2) {
			        			//TODO
			        		} else if(e.getButton() == MouseEvent.BUTTON1) {
			        			//TODO
			        		} else if(e.getButton() == MouseEvent.BUTTON3) {
			        			MetaInformationPopupMenu menu = gui.getMetaInformationPopupMenu();
			                	menu.show(RevealMetaDataPanel.this, e.getX(), e.getY());
			        		}
	                	}
	                }
	            }
	            else if(e.getClickCount() == 2) {
	                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
	                Object o = node.getUserObject();
	                if(o instanceof MetaInformation) {
	                	MetaInformation metaInfo = (MetaInformation)o;
	                	DisplayMetaInformationAction dmia = new DisplayMetaInformationAction(gui.getProjectHandler());
	                	dmia.triggerAction(metaInfo);
	                }
	            }
	        }
		}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
	}
}
