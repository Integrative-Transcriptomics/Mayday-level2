package mayday.jsc.snippets;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.structures.StringListModel;
import mayday.mushell.snippet.AbstractSnippet;
import mayday.mushell.snippet.SnippetEvent;

/** 
 * Overview over datasets 
 *
 * @version 1.0
 * @author Tobias Ries, ries@exean.net
 */
public class JSDataSetManager extends AbstractSnippet implements ListDataListener
{
	private JScrollPane jsp;
	private StringListModel objectsAndFunctions;
	private JTree lsResultTree;	
	private JSMutableTreeNode root;			
	
	/** 
	 * Snippet which displays all DataSets within a tree 
	 *
	 * @version 1.0
	 * @author Tobias Ries, ries@yuricon.de
	 */
	public JSDataSetManager()
	{
		this.root = new JSMutableTreeNode("DataSetManager", "DataSetMgrInstance");
		this.lsResultTree = new JTree(root);
		this.lsResultTree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()==1 && e.getButton()==MouseEvent.BUTTON3) {
					TreePath selPath = lsResultTree.getPathForLocation(e.getX(), e.getY());
					if(selPath != null){
						lsResultTree.setSelectionPath(selPath);
						String lastSnippet = ((JSMutableTreeNode)lsResultTree.getLastSelectedPathComponent()).getCommand();
						eventfirer.fireEvent(new SnippetEvent(JSDataSetManager.this,lastSnippet,false));
					}
				}
			}
		});
		this.jsp = new JScrollPane(lsResultTree);
		this.jsp.setName("DataSets");		
		this.fill();		
	}
		
	public JComponent getComponent() 
	{
		return this.jsp;
	}
	
	/** 
	 * Fills the tree with all available DataSets 
	 *
	 * @version 1.0
	 */
	private void fill()
	{					
		DataSetManager mgr = DataSetManager.singleInstance;
		mgr.addListDataListener(this);
		List<DataSet> ds = mgr.getDataSets();
		for(int i = 0; i < ds.size(); i++)
		{				
			JSMutableTreeNode dsNode = new JSMutableTreeNode(ds.get(i).getName(), this.root.getCommand() + ".getDataSets().get("+i+")");
			this.root.add(dsNode);
			List<ProbeList> pl = ds.get(i).getProbeListManager().getProbeLists();
			for(int p = 0; p < pl.size(); p++)
			{
				ProbeList pbList = pl.get(p);
				JSMutableTreeNode plNode = new JSMutableTreeNode(pbList.getName(), dsNode.getCommand() + ".getProbeListManager().getProbeLists().get("+p+")");
				dsNode.add(plNode);				
				for(int f = 0; f < pbList.getNumberOfProbes(); f++)
					plNode.add(new JSMutableTreeNode(pbList.getProbe(f).getName(), plNode.getCommand()+".getProbe("+f+")"));
			}			
		}		
			
	}
	
	public StringListModel getFullModel()
	{
		return this.objectsAndFunctions;
	}
	
	@SuppressWarnings("serial")
	class JSMutableTreeNode extends DefaultMutableTreeNode
	{		
		private String command;
		public JSMutableTreeNode(String displayText, String cmd)
		{
			super(displayText);
			this.command = cmd;
		}
		public String getCommand()
		{
			return this.command;
		}
	}
	
	private void updateTree()
	{	
		this.root.removeAllChildren();
		this.fill();
		this.lsResultTree.updateUI();
	}

	@Override
	public void contentsChanged(ListDataEvent arg0) 
	{
		this.updateTree();
	}

	@Override
	public void intervalAdded(ListDataEvent arg0)
	{
		this.updateTree();
	}

	@Override
	public void intervalRemoved(ListDataEvent arg0)
	{
		this.updateTree();
	}

}
