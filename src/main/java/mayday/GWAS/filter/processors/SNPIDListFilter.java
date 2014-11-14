package mayday.GWAS.filter.processors;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mayday.GWAS.data.SNP;
import mayday.GWAS.filter.AbstractDataProcessor;
import mayday.GWAS.filter.StorageNodeStorable;
import mayday.GWAS.gui.OptionPanelProvider;
import mayday.core.io.StorageNode;

public class SNPIDListFilter extends AbstractDataProcessor<SNP, Boolean> implements OptionPanelProvider, StorageNodeStorable {

	private List<String> snpIDs = new LinkedList<String>();
	private JTextField textField = new JTextField();
	
	@Override
	public void dispose() {
		snpIDs.clear();
	}

	@Override
	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("SNPIDListFilter", "");
		parent.addChild("SNPIDs", toIDListString());
		return parent;
	}

	@Override
	public void fromStorageNode(StorageNode storageNode) {
		String idListString = storageNode.getChild("SNPIDs").Value;
		parseSNPIDs(idListString);
	}

	@Override
	public JPanel getOptionPanel() {
		JPanel panel = new JPanel(new GridLayout(1,1));
		panel.setBorder(BorderFactory.createTitledBorder("Add SNP-IDs (comma Separated):"));
		
		textField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				parseSNPIDs(textField.getText());
				fireChanged();
			}
		});
		
		if(snpIDs.size() > 0) {
			textField.setText(toIDListString());
		}
		
		panel.add(textField);
		
		return panel;
	}

	@Override
	public Class<?>[] getDataClass() {
		return snpList == null ? null : new Class<?>[]{Boolean.class};
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return SNP.class.isAssignableFrom(inputClass[0]);
	}

	@Override
	public String toString() {
		return "User defined SNP-IDs";
	}

	@Override
	protected Boolean convert(SNP value) {
		return snpIDs.contains(value.getID());
	}

	@Override
	public String getName() {
		return "User defined SNP-IDs";
	}

	@Override
	public String getType() {
		return "data.snplist.filter.snpidlist";
	}

	@Override
	public String getDescription() {
		return "Filter SNPs based on a user defined list of identifiers";
	}
	
	private void parseSNPIDs(String snpids) {
		this.snpIDs.clear();
		String[] splitIDs = snpids.split(",");
		for(String s : splitIDs) {
			String trim = s.trim();
			if(trim.length() > 0)
				snpIDs.add(s.trim());
		}
	}
	
	private String toIDListString() {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < snpIDs.size(); i++) {
			sb.append(snpIDs.get(i));
			if(i < snpIDs.size()-1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
}
