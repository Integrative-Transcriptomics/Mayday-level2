package mayday.GWAS.filter.gui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.ProjectHandler;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.filter.SNPListSelectionFilter;
import mayday.GWAS.listeners.DataStorageEvent;
import mayday.GWAS.listeners.DataStorageListener;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;

@SuppressWarnings("serial")
public class SNPListSelectionPanel extends JTabbedPane implements ChangeListener {

	private HashMap<SelectionComponent, DataStorage> selComps = new HashMap<SelectionComponent, DataStorage>();
	private LinkedList<SelectionComponent> byTabIndex = new LinkedList<SelectionComponent>();
	private int lastSelectedTab=0;
	
	private SNPListSelectionPanel() {
		this.addChangeListener(this);
	}
	
	public SNPListSelectionPanel(ProjectHandler projectHandler) {
		this();
		for (DataStorage ds : projectHandler.getProjects()) {
			SelectionComponent sc = new SelectionByList();
			add(ds.getAttribute().getName(),sc.getComponent());
			selComps.put(sc, ds);
			byTabIndex.add(sc);
		}
		populate();
	}
	
	public void setFilter(SNPListSelectionFilter plf) {
		for (SelectionComponent sc : byTabIndex)
			sc.setFilter(plf);
		populate();
	}
	
	private void populate() {
		for(SelectionComponent sc : byTabIndex) {
			sc.populate(selComps.get(sc));
		}
	}
	
	// copy current selection on tab change
	public void stateChanged(ChangeEvent arg0) {
		if (this.getSelectedIndex()!=lastSelectedTab) {
			List<SNPList> sel = byTabIndex.get(lastSelectedTab).getSelectedSNPLists();
			for (SelectionComponent sc : byTabIndex)
				sc.setSelectedSNPLists(sel);
			lastSelectedTab = this.getSelectedIndex();
		}
	}
	
	public List<SNPList> getSelection() {
		return byTabIndex.get(lastSelectedTab).getSelectedSNPLists(); 
	}
	
	public int getSelectableCount() {
		return byTabIndex.get(lastSelectedTab).getSelectableCount();
	}
	
	public void removeNotify() {
		for (SelectionComponent sc : byTabIndex) {
			sc.dispose();
		}
	    super.removeNotify();
	  }
	
	public void addInternalMouseListener(MouseListener ml) {
		for (SelectionComponent sc : byTabIndex) {
			sc.getComponent().addMouseListener(ml);
		}		
	}

	
	
	// the parent class for different kind of probelist lists (tree, by type, by class...)
	private abstract static class SelectionComponent implements DataStorageListener {
		
		protected DataStorage ds;
		protected SNPListSelectionFilter filter;
		
		public abstract String toString();

		public void setFilter(SNPListSelectionFilter slf) {
			filter = slf;
		}
		
		public void populate(DataStorage ds) {
			if (this.ds!=null) 
				dispose(); //unregister first
			this.ds = ds;
			this.ds.addDataStorageListener(this);
			rebuild();
		}
		
		protected abstract void rebuild();
		
		public abstract List<SNPList> getSelectedSNPLists();

		public abstract void setSelectedSNPLists(List<SNPList> sls);
		
		public abstract Component getComponent();
		
		public abstract int getSelectableCount();
		
		public void dispose() {
			ds.removeDataStorageListener(this);
		}
		
		public void dataChanged(DataStorageEvent dse) {
			List<SNPList>  oldSel = getSelectedSNPLists();
			rebuild();
			setSelectedSNPLists(oldSel);
		}
	}
	
	public abstract static class SelectionTree extends SelectionComponent {
		
		protected JTree plTree;
		protected DefaultTreeModel treeModel;
		protected DefaultMutableTreeNode rootNode;
		
		public SelectionTree() {
			rootNode = new DefaultMutableTreeNode("SNPLists");
			treeModel = new DefaultTreeModel(rootNode);
			plTree = new JTree(rootNode);
			plTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			plTree.addMouseListener(new ObjectListMouseListener());
		}
		
		@Override
		public List<SNPList> getSelectedSNPLists() {
			List<SNPList> ret = new LinkedList<SNPList>();
			TreePath[] selectedRows = plTree.getSelectionModel().getSelectionPaths();
			if (selectedRows!=null)
				for (TreePath tp : selectedRows) {
					SNPList sl = nodeToSNPList(((DefaultMutableTreeNode)tp.getLastPathComponent()));
					if (sl!=null)
							ret.add(sl);				
				}
			return ret;
		}

		@Override
		public void setSelectedSNPLists(List<SNPList> groups) {
			TreePath[] paths = new TreePath[groups.size()];
			int pathi=0;
			for (SNPList pl : groups) {				
				paths[pathi] = findInTree(new TreePath(rootNode), pl);
				pathi++;
			}
			plTree.getSelectionModel().setSelectionPaths(paths);
		}

		@Override
		public Component getComponent() {
			return new JScrollPane(plTree);
		}
		
		protected abstract SNPList nodeToSNPList(DefaultMutableTreeNode node);
		
		@SuppressWarnings({ "rawtypes" })
		protected TreePath findInTree(TreePath parent, SNPList object) {
	        DefaultMutableTreeNode startnode = (DefaultMutableTreeNode)parent.getLastPathComponent();
	        	    
	        if (nodeToSNPList(startnode)==object)
	            return parent;
	    
	        // Traverse children
	        if (startnode.getChildCount() >= 0) {
	        	for (Enumeration e=startnode.children(); e.hasMoreElements(); ) {
	        		TreeNode n = (TreeNode)e.nextElement();
	        		TreePath path = parent.pathByAddingChild(n);
	        		TreePath result = findInTree(path, object);
	        		// Found a match
	        		if (result != null) {
	        			return result;
	        		}
	        	}
	        }
	        return null;
		}
		
		protected class ObjectListMouseListener extends MouseInputAdapter {      
			public void mouseClicked( MouseEvent e ) {       
				if ( e.getButton() == MouseEvent.BUTTON1 ) 
					if ( e.getClickCount() == 2 ) { 
						List<SNPList> mgs = getSelectedSNPLists();
						if (mgs.size()>0) {
							AbstractPropertiesDialog dlg;
							//Object selected = mgs.get(0);
							dlg = PropertiesDialogFactory.createDialog(mgs.toArray());
							Component c = plTree;
							while (c!=null && !(c instanceof java.awt.Dialog))
								c = c.getParent();
							if (c!=null)
								dlg.setModal(((java.awt.Dialog)c).isModal());
							//dlg.setModal(isModal()); 
							dlg.setVisible(true);
						}
					}
			}
		}  
		
		
	}
	
	private static class SelectionByList extends SelectionTree {

		private int selectableCount;
		
		public SelectionByList() {
			super();
			plTree.setCellRenderer(new SNPListTreeCellRenderer());
		}
		
		@Override
		protected void rebuild() {
			selectableCount=0;
			rootNode.removeAllChildren();
			
			HashMap<SNPList, DefaultMutableTreeNode> listsWeHaveSeen = new HashMap<SNPList, DefaultMutableTreeNode>();
			listsWeHaveSeen.put(null, rootNode);
			
			for (SNPList sl : ds.getSNPLists()) {				
				if (filter == null || filter.pass(sl)) {
					addSNPListNode(sl, listsWeHaveSeen);
					++selectableCount;					
				}
			}
			treeModel = new DefaultTreeModel(rootNode);
			plTree.setModel(treeModel);
			for (int i = 0; i < plTree.getRowCount(); i++) {
				plTree.expandRow(i);
			}

		}
		
		protected void addSNPListNode(SNPList sl, HashMap<SNPList, DefaultMutableTreeNode> lwhs) {
//			SNPList parent = sl.getParent();
//			if (!lwhs.containsKey(parent)) {
//				// add a parent node 
//				addProbeListNode(parent, lwhs);
//			}
			DefaultMutableTreeNode slNode;
			if (filter==null || filter.pass(sl))
				slNode = new DefaultMutableTreeNode(sl);
			else
				slNode = new DefaultMutableTreeNode(sl.getAttribute().getName());			
//			lwhs.get(parent).add(slNode);			
			rootNode.add(slNode);
			lwhs.put(sl, slNode);
		}
		
		protected SNPList nodeToSNPList(DefaultMutableTreeNode node) {
			Object o = node.getUserObject();
			if (o instanceof SNPList)
				return (SNPList)node.getUserObject();
			return null;
		}
		
		public String toString() {
			return "All SNP Lists";
		}
		
		public int getSelectableCount() {
			return selectableCount;
		}
	}
	
	public static class SNPListTreeCellRenderer extends DefaultTreeCellRenderer {
		
		private SNPListCellRenderer slcr = new SNPListCellRenderer();
		private JList list = new JList();
		
		public Component getTreeCellRendererComponent(JTree tree,
                Object value,
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
			
			Object val = ((DefaultMutableTreeNode)value).getUserObject();
			
			if (val instanceof SNPList) {
				return slcr.getListCellRendererComponent(list,val,0,selected,hasFocus);
			} 
			return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);			
		}
	}
}
