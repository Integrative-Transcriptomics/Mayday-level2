package mayday.Reveal.filter.processors;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.events.SNVListEvent;
import mayday.Reveal.events.SNVListListener;
import mayday.Reveal.filter.AbstractDataProcessor;
import mayday.Reveal.filter.StorageNodeStorable;
import mayday.Reveal.filter.gui.SNPListSelectionDialog;
import mayday.Reveal.gui.IOptionPanelProvider;
import mayday.core.io.StorageNode;

public class ContainedInSNPListFilter extends AbstractDataProcessor<SNV, Boolean> implements SNVListListener, IOptionPanelProvider, StorageNodeStorable {
	
	public static final String MYTYPE = "ContainedInSNPList";
	
	private JTextField selectedSNPList = new JTextField(30);
	private boolean silent = false;
	private AbstractAction selectAction;
	private SNVList currentSnpList;

	@Override
	public void dispose() {
		setCurrentSNPList(null);
	}

	@Override
	public Class<?>[] getDataClass() {
		return currentSnpList==null?null:new Class[]{Boolean.class};
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return SNV.class.isAssignableFrom(inputClass[0]);
	}

	@Override
	public String toString() {
		return (currentSnpList==null?"unfinished":"contained in "+currentSnpList.getAttribute().getName());
	}

	@Override
	protected Boolean convert(SNV value) {
		return (currentSnpList==null || currentSnpList.contains(value)); 
	}
	
	public JPanel getOptionPanel() {
		JPanel op = new JPanel();
		op.setBorder(BorderFactory.createTitledBorder("Select a SNPList"));
		composeOptionPanel(op);
		return op;
	}
	
	@SuppressWarnings("serial")
	public void composeOptionPanel(JPanel optionPanel) {
		silent=true;
		selectedSNPList.setEditable(false);
		JButton selectSNPListButton = new JButton(selectAction = new AbstractAction("Select") {
			public void actionPerformed(ActionEvent e) {
				SNPListSelectionDialog slsd = new SNPListSelectionDialog(
						getSNPList().getDataStorage().getProjectHandler()
				);
				slsd.setModal(true);
				slsd.setVisible(true);
				List<SNVList> mgs = slsd.getSelection();
				if (mgs.size()>0) {
					setCurrentSNPList(mgs.get(0));
				} else {
					setCurrentSNPList(null);
				}
			}
		});
		optionPanel.add(selectedSNPList);
		optionPanel.add(selectSNPListButton);
		
		if (currentSnpList != null) {
			selectedSNPList.setText(currentSnpList.getAttribute().getName());
		} else {
			selectAction.actionPerformed(null);
		}
		silent=false;
	}
	
	public void setCurrentSNPList(SNVList sl) {
		if (currentSnpList!=null)
			currentSnpList.removeSNVListListener(this);		
		currentSnpList=sl;
		if (sl!=null) {
			selectedSNPList.setText(sl.getAttribute().getName());
			currentSnpList.addSNVListListener(this);
		}else
			selectedSNPList.setText("-- nothing selected --");
		if (!silent)
			fireChanged();
	}

	@Override
	public void snpListChanged(SNVListEvent event) {
		if (event.getChange()==SNVListEvent.CONTENT_CHANGE)
			fireChanged();
		else if (event.getChange()==SNVListEvent.SNPLIST_CLOSED)
			setCurrentSNPList(null);
	}

	@Override
	public StorageNode toStorageNode() {
		return new StorageNode("SNPList", currentSnpList != null ? currentSnpList.getAttribute().getName() : "null");
	}

	@Override
	public void fromStorageNode(StorageNode sn) {
		String s = sn.Value;
		currentSnpList = null;
		if(!s.equals("null")) {
			SNVList sl = getSNPList().getDataStorage().getSNVList(s);
			setCurrentSNPList(sl);
		}
	}

	@Override
	public String getName() {
		return "Contained in SNPList";
	}

	@Override
	public String getType() {
		return "data.snplist.filter." + MYTYPE;
	}

	@Override
	public String getDescription() {
		return "Contained in SNPList";
	}
}
