package mayday.GWAS.filter.processors;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import mayday.GWAS.filter.AbstractDataProcessor;
import mayday.GWAS.filter.StorageNodeStorable;
import mayday.GWAS.gui.OptionPanelProvider;
import mayday.core.io.StorageNode;

public class CompareNumber extends AbstractDataProcessor<Double, Boolean> implements OptionPanelProvider, StorageNodeStorable {
	
	private String[] operations = new String[]{
			"<","<=","==",">=",">"
	};
	private String[] operations2 = new String[]{
			"&lt;","&lt;=","==","&gt;=","&gt;"
	};
	private JComboBox operationCB = new JComboBox(operations);
	protected int operation=2;
	protected Double number = 0d;
	private String substring;
	private JTextField numberTF = new JTextField(10);
	
	public CompareNumber() {
		substring = ""+number;
	}
	
	@Override
	public void dispose() {
		//nothing to do
	}

	@Override
	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("NumberMatcher","");
		parent.addChild("number",number);
		parent.addChild("operation",operation);
		return parent;
	}

	@Override
	public void fromStorageNode(StorageNode storageNode) {
		number = parseDouble(storageNode.getChild("number").Value);
		operation = Integer.parseInt(storageNode.getChild("operation").Value);
		substring=""+number;
		numberTF.setText(substring);
		operationCB.setSelectedIndex(operation);
	}

	@Override
	public JPanel getOptionPanel() {
		JPanel optionPanel = new JPanel();
		optionPanel.setBorder(BorderFactory.createTitledBorder("Threshold Filter - Setting"));
		
		optionPanel.add(new JLabel("Value should be "));
		operationCB.setSelectedIndex(operation);
		operationCB.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int newOp = operationCB.getSelectedIndex();
				if (newOp!=operation) {
					operation=newOp;
					fireChanged();
				}
			}
			
		});
		optionPanel.add(operationCB);
		
		numberTF.setText(substring);
		final JLabel mistake = new JLabel("Please check your input.");
		numberTF.getDocument().addDocumentListener(new DocumentListener() {

			public void actionPerformed() {
				if (substring != numberTF.getText()) {
					substring = numberTF.getText();
					try {
						number = parseDouble(substring);
						numberTF.setBackground(Color.green);
						mistake.setVisible(false);
						fireChanged();
					} catch (Exception e) {
						numberTF.setBackground(Color.red);
						mistake.setVisible(true);
					}
				}
			}

			public void changedUpdate(DocumentEvent e) {
				actionPerformed();
			}

			public void insertUpdate(DocumentEvent e) {
				actionPerformed();			}

			public void removeUpdate(DocumentEvent e) {
				actionPerformed();	
			}
		});
		optionPanel.add(numberTF);
		optionPanel.add(mistake);
		mistake.setVisible(false);
		
		return optionPanel;
	}

	@Override
	public Class<?>[] getDataClass() {
		return snpList==null?null:new Class[]{Boolean.class};
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return Double.class.isAssignableFrom(inputClass[0]);
	}

	@Override
	public String toString() {
		return " "+operations2[operation]+" "+number;
	}

	@Override
	public String getName() {
		return "Threshold Filter";
	}

	@Override
	public String getType() {
		return "data.snplist.filter.compareNumber";
	}

	@Override
	public String getDescription() {
		return "Compares a number to a user defined value";
	}

	@Override
	protected Boolean convert(Double v) {
		switch(operation) {
		case 0: return v<number;
		case 1: return v<=number;
		case 2: return v==number;
		case 3: return v>=number;
		case 4: return v>number;
		}
		return null;
	}
	
	protected Double parseDouble(String value) {
		if(value == null)
			return null;
		Double d = Double.parseDouble(value);
		return d;
	}
}
