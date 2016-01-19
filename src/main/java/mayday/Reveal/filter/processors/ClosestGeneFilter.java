package mayday.Reveal.filter.processors;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import mayday.Reveal.data.Gene;
import mayday.Reveal.data.GeneList;
import mayday.Reveal.data.SNV;
import mayday.Reveal.filter.AbstractDataProcessor;
import mayday.Reveal.filter.StorageNodeStorable;
import mayday.Reveal.gui.IOptionPanelProvider;
import mayday.core.Probe;
import mayday.core.io.StorageNode;

public class ClosestGeneFilter extends AbstractDataProcessor<SNV, Boolean> implements IOptionPanelProvider, StorageNodeStorable {

	private String geneName = "";
	private int index = 0;
	
	private JComboBox geneBox = new JComboBox();
	
	@Override
	public void dispose() {
		//nothing to do
	}

	@Override
	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("ClosestGeneFilter", "");
		parent.addChild("Gene", geneName);
		parent.addChild("Index", index);
		return parent;
	}

	@Override
	public void fromStorageNode(StorageNode storageNode) {
		geneName = storageNode.getChild("Gene").Value;
		index = Integer.parseInt(storageNode.getChild("Index").Value);
	}

	@Override
	public JPanel getOptionPanel() {
		JPanel panel = new JPanel(new GridLayout(1,1));
		panel.setBorder(BorderFactory.createTitledBorder("Select a gene"));
		
		geneBox.removeAllItems();
		
		GeneList genes = snpList.getDataStorage().getGenes();
		for(Probe p : genes) {
			Gene g = (Gene)p;
			geneBox.addItem(g.getName());
		}
		
		geneBox.setSelectedIndex(index);
		
		geneBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				geneName = (String)geneBox.getSelectedItem();
				index = geneBox.getSelectedIndex();
				fireChanged();
			}
		});
		
		panel.add(geneBox);
		
		return panel;
	}

	@Override
	public Class<?>[] getDataClass() {
		return snpList == null ? null : 
			new Class<?>[]{Boolean.class};
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return SNV.class.isAssignableFrom(inputClass[0]);
	}

	@Override
	public String toString() {
		return "Closest gene " + geneName;
	}

	@Override
	protected Boolean convert(SNV value) {
		return value.getGene().equals(geneName);
	}

	@Override
	public String getName() {
		return "Closest Gene";
	}

	@Override
	public String getType() {
		return "data.snplist.filter.closestgenefilter";
	}

	@Override
	public String getDescription() {
		return "Filter all SNPs whose closest gene is equal to a user defined choice";
	}
}
