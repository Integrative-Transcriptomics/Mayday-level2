package mayday.Reveal.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import mayday.Reveal.actions.snplist.ModifySNPList;
import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.gui.menu.SNPListPopupMenu;
import mayday.Reveal.listeners.ProjectEvent;
import mayday.Reveal.listeners.ProjectEventListener;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class RevealDataPanel extends JTree implements ProjectEventListener, TreeSelectionListener {
	
	private RevealGUI gui;
	private DataTreeModel treeModel;
	
	private Set<SNPList> selectedSNPLists;
	private List<DataStorage> lastSelectedProjects;
	
	/**
	 * @param gui
	 */
	public RevealDataPanel(RevealGUI gui) {
		this.setModel(treeModel = new DataTreeModel(gui.getProjectHandler()));
		
		this.setEditable(false);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		this.setShowsRootHandles(false);
		
		this.addTreeSelectionListener(this);
		
		this.gui = gui;
		
		this.setRootVisible(false);
		this.putClientProperty("JTree.lineStyle", "Angled");
		this.setCellRenderer(new RevealTreeCellRenderer());
		
		this.gui.getProjectHandler().getProjectEventHandler().addProjectEventListener(this);
		
		this.selectedSNPLists = new HashSet<SNPList>();
		this.lastSelectedProjects = new LinkedList<DataStorage>();
		
		this.addMouseListener(new DataMouseListener());
	}
	
	private void addProject() {
		ProjectHandler projectHandler = gui.getProjectHandler();
		DataStorage project = projectHandler.getLast();
		
		if(treeModel.contains(project.getAttribute().getName())) {
			System.out.println("The project is already in the tree");
			return;
		}
		
		treeModel.add(project);
		
		expandAll();
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

	@Override
	public void projectChanged(ProjectEvent pe) {
		switch(pe.getChange()) {
		case ProjectEvent.PROJECT_ADDED:
			addProject();
			expandAll();
			break;
		case ProjectEvent.PROJECT_REMOVED:
			treeModel.removeProject();
			expandAll();
			break;
		case ProjectEvent.PROJECT_CHANGED:
//			System.out.println("RevealDataPanel: Project changed ...");
			DataStorage project = (DataStorage)pe.getSource();
			treeModel.updateProject(project);
			expandAll();
			break;
		case ProjectEvent.SNP_SELECTION_CLEARED:
			this.selectedSNPLists.clear();
			break;
		}
		
		repaint();
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreePath[] paths = e.getPaths();
		
		for(int i = 0; i < paths.length; i++) {
			TreePath p = paths[i];
			
			if(e.isAddedPath(p)) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode)p.getLastPathComponent();
				Object o = n.getUserObject();
				if(o instanceof SNPList) {
					if(selectedSNPLists.size() > 0) {
						DataStorage ds1 = selectedSNPLists.iterator().next().getDataStorage();
						DataStorage ds2 = ((SNPList)o).getDataStorage();
						if(ds1 == ds2) {
							selectedSNPLists.add((SNPList)o);
						} else {
							System.out.println("It is not allowed to select SNPLists from different projects.");
						}
					} else {
						selectedSNPLists.add((SNPList)o); 
					}
				}
				DefaultMutableTreeNode projectNode = (DefaultMutableTreeNode)p.getPathComponent(1);
				if(projectNode.getUserObject() instanceof DataStorage) {
					DataStorage ds = (DataStorage)projectNode.getUserObject();
//					if(lastSelectedProjects.contains(ds)) {
//						lastSelectedProjects.remove(ds);
//					}
					lastSelectedProjects.add(ds);
				}
			} else {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode)p.getLastPathComponent();
				Object o = n.getUserObject();
				if(o instanceof SNPList) {
					selectedSNPLists.remove((SNPList)o);
				} 
				DefaultMutableTreeNode projectNode = (DefaultMutableTreeNode)p.getPathComponent(1);
				if(projectNode.getUserObject() instanceof DataStorage) {
					DataStorage ds = (DataStorage)projectNode.getUserObject();
					if(lastSelectedProjects.contains(ds))
						lastSelectedProjects.remove(ds);
				}
			}
		}

		gui.getProjectHandler().setSelectedSNPLists(selectedSNPLists);
		if(lastSelectedProjects.size() > 0) {
			int lastProject = lastSelectedProjects.size() - 1;
			gui.getProjectHandler().setSelectedProject(lastSelectedProjects.get(lastProject));
		} else {
			gui.getProjectHandler().setSelectedProject(null);
		}

		repaint();
	}
	
	private void expandAll(JTree tree, boolean expand) {
	    TreeNode root = (TreeNode) tree.getModel().getRoot();
	    // Traverse tree from root
	    expandAll(tree, new TreePath(root), expand);
	}
	 
	@SuppressWarnings("rawtypes")
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
	
	private class DataMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			int selRow = getRowForLocation(e.getX(), e.getY());
	        TreePath selPath = getPathForLocation(e.getX(), e.getY());
	        if(selRow != -1) {
	        	if(e.getClickCount() == 1) {
	        		
	        		DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
	                Object o = node.getUserObject();
	                
	                if(o instanceof SNPList) {
	                	if(gui.getProjectHandler().getSelectedSNPLists().contains(o)) {
	                		if(e.getButton() == MouseEvent.BUTTON2) {
			        			//TODO
			        		} else if(e.getButton() == MouseEvent.BUTTON1) {
			        			//TODO
			        		} else if(e.getButton() == MouseEvent.BUTTON3) {
			        			SNPListPopupMenu menu = gui.getSNPListPopupMenu();
			                	menu.show(RevealDataPanel.this, e.getX(), e.getY());
			        		}
	                	}
	                }
	            }
	            else if(e.getClickCount() == 2) {
	                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
	                Object o = node.getUserObject();
	                if(o instanceof SNPList) {
	                	SNPList snpList = (SNPList)o;
	                	ModifySNPList a = new ModifySNPList();
	                	a.setProjectHandler(gui.getProjectHandler());
		               	try {
							a.triggerAction(snpList);
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(gui, ex.getMessage());
						}
	                }
	            }
	        }
		}

		@Override
		public void mouseReleased(MouseEvent e) {}
	}
}
