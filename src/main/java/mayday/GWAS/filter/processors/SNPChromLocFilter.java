package mayday.GWAS.filter.processors;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import mayday.GWAS.data.SNP;
import mayday.GWAS.filter.AbstractDataProcessor;
import mayday.GWAS.filter.StorageNodeStorable;
import mayday.GWAS.gui.OptionPanelProvider;
import mayday.core.io.StorageNode;

public class SNPChromLocFilter extends AbstractDataProcessor<SNP, Boolean> implements OptionPanelProvider, StorageNodeStorable {

	private String chromosome = "";
	private Integer startPosition = 0;
	private Integer stopPosition = Integer.MAX_VALUE;
	
	private JTextField chromField = new JTextField();
	private JTextField startPosField = new JTextField();
	private JTextField stopPosField = new JTextField();
	
	@Override
	public void dispose() {
		//nothing to do here
	}

	@Override
	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("SNPChromLocFilter", "");
		parent.addChild("Chromosome", chromosome);
		parent.addChild("StartPosition", startPosition);
		parent.addChild("StopPosition", stopPosition);
		return parent;
	}

	@Override
	public void fromStorageNode(StorageNode storageNode) {
		chromosome = storageNode.getChild("Chromosome").Value;
		startPosition = Integer.parseInt(storageNode.getChild("StartPosition").Value);
		stopPosition = Integer.parseInt(storageNode.getChild("StopPosition").Value);
	}

	@Override
	public JPanel getOptionPanel() {
		JPanel panel = new JPanel(new GridLayout(6,1));
		panel.setBorder(BorderFactory.createTitledBorder("Define the chromosomal location"));
		
		panel.add(new JLabel("Chromosome:"));
		
		DocumentListener dl = new DocumentListener() {
			
			public void actionPerformed() {
				if(chromosome != chromField.getText())
					chromosome = chromField.getText();
				try {
					startPosition = Integer.parseInt(startPosField.getText());
					startPosField.setBackground(Color.GREEN);
				} catch(NumberFormatException ex) {
					startPosField.setBackground(Color.RED);
				}
				
				try {
					stopPosition = Integer.parseInt(stopPosField.getText());
					stopPosField.setBackground(Color.GREEN);
				} catch(NumberFormatException ex) {
					stopPosField.setBackground(Color.RED);
				}
				fireChanged();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				actionPerformed();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				actionPerformed();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				actionPerformed();
			}	
		};
		
		chromField.setText(chromosome);
		startPosField.setText(startPosition+"");
		stopPosField.setText(stopPosition+"");
		
		chromField.getDocument().addDocumentListener(dl);
		startPosField.getDocument().addDocumentListener(dl);
		stopPosField.getDocument().addDocumentListener(dl);
		
		panel.add(chromField);
		panel.add(new JLabel("Start Position"));
		panel.add(startPosField);
		panel.add(new JLabel("Stop Position"));
		panel.add(stopPosField);
		
		return panel;
	}

	@Override
	public Class<?>[] getDataClass() {
		return snpList == null ? null :
			new Class<?>[]{Boolean.class};
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return SNP.class.isAssignableFrom(inputClass[0]);
	}

	@Override
	public String toString() {
		return "Located on " + chromosome + " [" + startPosition + "," + stopPosition + "]";
	}

	@Override
	protected Boolean convert(SNP value) {
		if(chromosome != null && startPosition <= stopPosition) {
			if(value.getChromosome().equals(chromosome)) {
				int position = value.getPosition();
				if(startPosition <= position && stopPosition >= position) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return "Chromosomal Location";
	}

	@Override
	public String getType() {
		return "data.snplist.filter.chromlocfilter";
	}

	@Override
	public String getDescription() {
		return "Filter SNPs based on their chromosomal location";
	}
}
