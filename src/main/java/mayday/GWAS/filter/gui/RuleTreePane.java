package mayday.GWAS.filter.gui;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;

import mayday.GWAS.filter.RuleSet;

@SuppressWarnings("serial")
public class RuleTreePane extends JScrollPane implements ChangeListener, TreeSelectionListener {

	private RuleSet ruleSet;
	private RuleTreeModel ruleTreeModel;
	private JTree tree;
	
	public RuleTreePane(RuleSet ruleSet) {
		this.ruleSet = ruleSet;
		this.ruleSet.addChangeListener(this);
		this.ruleTreeModel = new RuleTreeModel(this.ruleSet);
		this.tree = new JTree(ruleTreeModel);
		tree.setCellRenderer(new RuleNodeRenderer());
		tree.addTreeSelectionListener(this);
		setViewportView(tree);
	}
	
	public RuleTreeModel getRuleTreeModel() {
		return ruleTreeModel;
	}
	
	public JTree getRuleTree() {
		return tree;
	}
	
	public void removeNotify() {
		ruleSet.removeChangeListener(this);
		super.removeNotify();
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		try {
			ruleTreeModel.nodeChanged((TreeNode)e.getNewLeadSelectionPath().getLastPathComponent());
			ruleTreeModel.nodeChanged((TreeNode)e.getOldLeadSelectionPath().getLastPathComponent());
		} catch (Exception ex) {
			// never mind.
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		tree.repaint();
	}
}
