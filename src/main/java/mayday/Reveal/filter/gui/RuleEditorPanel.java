package mayday.Reveal.filter.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import mayday.Reveal.data.SNVList;
import mayday.Reveal.filter.RuleSet;
import mayday.Reveal.gui.IOptionPanelProvider;
import mayday.Reveal.utilities.SNVLists;
import mayday.core.DelayedUpdateTask;

@SuppressWarnings("serial")
public class RuleEditorPanel extends JPanel {
	
	private SNVList snpList;
	private SNVList clonedSNPList;
	protected JLabel filterSizeLabel = new JLabel();
	
	public RuleEditorPanel(SNVList snpList) {
		super(new BorderLayout());
		this.snpList = snpList;
		
		filterSizeLabel.setText(snpList.size() + " matching SNPs.");
		
		clonedSNPList = new SNVList("Temporary clone of \""+snpList.getAttribute().getName()+"\"", snpList.getDataStorage());
		clonedSNPList.getRuleSet().fromStorageNode(this.snpList.getRuleSet().toStorageNode());
		
		startWork();

		final JPanel rightPane = new JPanel();
		rightPane.add(new JLabel("Select a node in the tree to edit its properties"));
		
		final RuleTreeEditorPane leftPane = new RuleTreeEditorPane(clonedSNPList.getRuleSet());

		final JSplitPane jsli  = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		jsli.setContinuousLayout(true);

		TreeSelectionListener sel;
		
		leftPane.addTreeSelectionListener(sel=new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode tn = (DefaultMutableTreeNode)leftPane.getSelectedNode();
				if (tn!=null) {
					Object uo = tn.getUserObject();
					if (uo instanceof IOptionPanelProvider) {
						jsli.setRightComponent(((IOptionPanelProvider)uo).getOptionPanel());
					}
				} else
					jsli.setRightComponent(rightPane);
			}
		});

		add(jsli, BorderLayout.CENTER);
		
		
        Box l_buttonPanel = Box.createHorizontalBox();
        
        l_buttonPanel.add(filterSizeLabel);
        
        l_buttonPanel.add( Box.createHorizontalGlue() ); // right-aligns the buttons

        l_buttonPanel.add(new JButton(new ApplyAction()));   

        add(l_buttonPanel, BorderLayout.SOUTH);
        setMinimumSize(new Dimension(550,300));
        sel.valueChanged(null);
	}
	
	public void doExternalApply() {
		snpList.getRuleSet().clear();
		snpList.setSilent(false);
		snpList.getRuleSet().fromStorageNode(clonedSNPList.getRuleSet().toStorageNode());
	}
	
	public void removeNotify() {
		super.removeNotify();
		finalizeWork();
	}
	
	protected void finalizeWork() {
		removeListenersFrom(clonedSNPList);
		clonedSNPList.propagateClosing();
		clonedSNPList = null;
	}
	
	protected void startWork() {
		clonedSNPList.setSilent(true);
		addListenersTo(clonedSNPList);
	}
	
	protected void addListenersTo(SNVList dsl) {
		RuleSet rs = dsl.getRuleSet();
		rs.addChangeListener(sizeListener);
	}
	
	protected void removeListenersFrom(SNVList dsl) {
		RuleSet rs = dsl.getRuleSet();	
		rs.removeChangeListener(sizeListener);
	}
	
	protected ChangeListener sizeListener = new ChangeListener() {

		public void stateChanged(ChangeEvent e) {
			filterSizeLabel.setText("Evaluating ...");
			counter.trigger();
		}
	};
	
	protected DelayedUpdateTask counter = new DelayedUpdateTask("Testing filter",100) {
		
		@Override
		protected void performUpdate() {
			int size = SNVLists.countSNVs(clonedSNPList.getDataStorage(), clonedSNPList.getRuleSet());
			filterSizeLabel.setText(size+ " matching SNPs.");
		}
	
		@Override
		protected boolean needsUpdating() {
			return true;
		}
	};
	
	protected class ApplyAction extends AbstractAction {
		public ApplyAction() {
			super( "Apply" );
		}
		
		public void actionPerformed( ActionEvent event ) {			
			doExternalApply();
			clonedSNPList.setSilent(true);
		}
	}
}
