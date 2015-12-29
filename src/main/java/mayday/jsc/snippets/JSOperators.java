package mayday.jsc.snippets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.jsc.adjustableBehaviour.JSOperator;
import mayday.jsc.adjustableBehaviour.JSOverloadingOperators;
import mayday.jsc.shell.ToolBox;
import mayday.mushell.dispatch.DispatchEvent;
import mayday.mushell.dispatch.DispatchListener;
import mayday.mushell.snippet.AbstractSnippet;

/** 
 * Snippet for displaying and editing overloaded operators 
 *
 * @version 1.0
 * @author Tobias Ries, ries@yuricon.de
 */
public class JSOperators
	extends AbstractSnippet
	implements DispatchListener, Observer
{
	private JScrollPane jsp;	
	private JPanel mainPanel;
	private JLabel currentCommandLabel;	
	protected JSOverloadingOperators operators;
	private JTree operatorTree;
	private DefaultMutableTreeNode root;	
	private StringSetting _class,_for,_do;
	private BooleanSetting _assignable;
	private SettingDialog dialog;
	

	public JSOperators(JSOverloadingOperators ops)
	{			
		this.operators = ops;
		this.operators.addObserver(this);
						
		this.root = new DefaultMutableTreeNode("Definitions");
		this.operatorTree = new JTree(this.root);
		
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();		
	    renderer.setOpenIcon(null);
	    renderer.setClosedIcon(null);
	    renderer.setLeafIcon(ToolBox.loadImage("mayday/jsc/icoOperator.png"));
	    this.operatorTree.setCellRenderer(renderer);

		
		this.initGui();	
		this.update();		
	}
	
	private void initGui()
	{
		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		this.mainPanel.setName("Defined Operators");										
		
		this.dialog = createEditDialog();
		
		//-----------queuePanel
		JPanel queuePanel = new JPanel(new BorderLayout());
		queuePanel.setBackground(Color.white);
		queuePanel.setBorder(BorderFactory.createTitledBorder("Operators"));
						
		this.jsp = new JScrollPane(this.operatorTree);
		queuePanel.add(this.jsp, BorderLayout.CENTER);								
		
		this.mainPanel.add(queuePanel);
		//-----------queuePanel EOF
		
		//-----------buttonpanel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		JButton neu = new JButton("new");
		neu.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{								
				JSOperators.this.showEditDialog(null, true);				
			}			
		});
		buttonPanel.add(neu);
		
		JButton edit = new JButton("edit");
		edit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(operatorTree.getSelectionPath() == null)
					return;
				DefaultMutableTreeNode selected = (DefaultMutableTreeNode)(operatorTree.getSelectionPath().getLastPathComponent());
				if(root.equals(selected))
					return;
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)(operatorTree.getSelectionPath().getParentPath().getLastPathComponent());
				if(parent.equals(root))				
					return;
											
				JSOperator op = (JSOperator)((selected).getUserObject());
				JSOperators.this.showEditDialog(op, false);								
			}			
		});
		buttonPanel.add(edit);
		
		JButton delete = new JButton("delete");
		delete.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				DefaultMutableTreeNode selected = (DefaultMutableTreeNode)(operatorTree.getSelectionPath().getLastPathComponent());
				if(root.equals(selected))
					return;
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)(operatorTree.getSelectionPath().getParentPath().getLastPathComponent());
				if(!parent.equals(root))
					//operators.removeAllOperatorsOf((Class<?>)(selected).getUserObject());				
				{					
					operators.removeOperator((JSOperator)(selected).getUserObject());
					update();
				}													
			}			
		});
		buttonPanel.add(delete);
		queuePanel.add(buttonPanel, BorderLayout.SOUTH);
		//-----------buttonpanel EOF			
	}
	
	/** 
	 * Creates the settings for editing operators 
	 *
	 * @version 1.0
	 */
	private SettingDialog createEditDialog()
	{	
		HierarchicalSetting hs = new HierarchicalSetting("Overloaded Operator", LayoutStyle.PANEL_VERTICAL, true)
		.setCombineNonhierarchicalChildren(true)
		.addSetting
		(
				_class = new StringSetting("DEFINE FOR CLASS:", "Class to be affected by this operator", "")				
		)
		.addSetting
		(
				_for = new StringSetting("FOR:", "Enter a new operator, e.g. 'obj[sample]=result'", "")				
		)
		.addSetting
		(
				_do = new StringSetting("DO:", "What should be done for this Operator?", "")				
		)
		.addSetting
		(
				_assignable = new BooleanSetting("Assignable to subclasses", "May this operator be used for classes which can be assigned to the specified class", false)				
		);
		
		return new SettingDialog(null, "", hs);		
	}	
	
	/** 
	 * Displays the settings-dialog for a specific operator 
	 *
	 * @version 1.0
	 * @param op Operator to be edited
	 * @param newOperator Indicates whether a new operator shall be spawned upon the world
	 */
	private void showEditDialog(JSOperator op, boolean newOperator)
	{
		this._class.setValueString(op==null?"":op.getOperatorClass().getName());
		this._for.setValueString((op==null?"":op.getOperator()));
		this._do.setValueString((op==null?"":op.getOperation()));
		this._assignable.setBooleanValue((op==null?false:op.isAssignable()));
		
		String title = newOperator ? "Create new Operator" : "Edit existing Operator";
		this.dialog.setTitle(title);				
		
		while(this.dialog.showAsInputDialog().closedWithOK())	
			if(this.handleEditResult(op, newOperator))								
					return;			
	}
	
	/** 
	 * Creates a new operator from the results of the EditDialog 
	 *
	 * @version 1.0
	 * @param op Operator to be replaced (removed after new Operator was inserted successfully)
	 * @param newOperator Indicates whether a new operator shall be spawned upon the world
	 * @return boolean indicating whether operator-creation was successful 
	 */
	private boolean handleEditResult(JSOperator op, boolean newOperator)
	{				
		try
		{			
			String _def = this._class.getValueString();			
			String _for = this._for.getValueString();		
			String _do = this._do.getValueString();
			if(_def.isEmpty()||_for.isEmpty()||_do.isEmpty())
			{
				JOptionPane.showConfirmDialog(mainPanel, "All fields need to be filled.", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
				return false;
			}
			boolean _ass = this._assignable.getBooleanValue();			
			this.operators.addOperator(_def,_for,_do,_ass);
			if(!newOperator)			
				this.operators.removeOperator(op);											
			
			return true;
		} catch (ClassNotFoundException e)
		{
			JOptionPane.showConfirmDialog(mainPanel, "Class could not be identified!", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);			
			return false;
		}
	}
		
	/** 
	 * Fills the snippet's tree with all defined operators 
	 *
	 * @version 1.0
	 */
	private void fill()
	{		
		
		HashMap<Class<?>, List<JSOperator>> opMap = this.operators.getOperators();
		this.root.removeAllChildren();
		Set<Class<?>> classes = opMap.keySet();
		for(Class<?> c : classes)
		{
			DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(c.getSimpleName()+" ("+c.getPackage()+")");
			DefaultMutableTreeNode assNode = new DefaultMutableTreeNode("Assignable");
			DefaultMutableTreeNode absNode = new DefaultMutableTreeNode("Absolute");
			
			classNode.add(assNode);
			classNode.add(absNode);
			this.root.add(classNode);
			List<JSOperator> ops = opMap.get(c);
			for(JSOperator j : ops)
			{
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(j);
				if(j.isAssignable())
					assNode.add(node);					
				else
					absNode.add(node);				
			}
			if(assNode.isLeaf())
				classNode.remove(assNode);
			if(absNode.isLeaf())
				classNode.remove(absNode);
		}
	}
		
	private void update()
	{				
		this.fill();	
		ToolBox.expandJTree(this.operatorTree, new TreePath(root));
		this.operatorTree.updateUI();
	}
	
	@Override
	public JComponent getComponent()
	{
		return this.mainPanel;
	}

	@Override
	public void dispatching(DispatchEvent evt)
	{
		if(evt.getCommand() != null)
			this.currentCommandLabel.setText(evt.getCommand());
		else
			this.currentCommandLabel.setText(" Nothing ");
		
	}

	@Override
	public void update(Observable arg0, Object arg1)
	{
		this.update();
	}
}
