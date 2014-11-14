package mayday.GWAS.filter.processors;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.Gene;
import mayday.GWAS.data.GeneList;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.meta.MetaInformation;
import mayday.GWAS.data.meta.MetaInformationManager;
import mayday.GWAS.data.meta.SLResults;
import mayday.GWAS.data.meta.StatisticalTestResult;
import mayday.GWAS.data.meta.TLResults;
import mayday.GWAS.filter.AbstractDataProcessor;
import mayday.GWAS.filter.StorageNodeStorable;
import mayday.GWAS.gui.OptionPanelProvider;
import mayday.core.io.StorageNode;

/**
 * 
 * @author jaeger
 *
 */
public class MetaInformationFilter extends AbstractDataProcessor<SNP, Object> implements StorageNodeStorable, OptionPanelProvider {

	private MetaInformation metaInfo = null;
	private String gene = "";
	
	private JComboBox metaInfoGroupBox;
	private JComboBox metaInfoBox;
	private JComboBox geneSelection;
	
	
	@Override
	public void dispose() {
		metaInfo = null;
	}

	@Override
	public JPanel getOptionPanel() {
		DataStorage ds = snpList.getDataStorage();
		final MetaInformationManager mim = ds.getMetaInformationManager();
		
		JPanel panel = new JPanel(new GridLayout(6,1));
		panel.setBorder(BorderFactory.createTitledBorder("MetaInformation Settings"));

		String[] metaGroups = mim.keySet().toArray(new String[0]);
		metaInfoGroupBox = new JComboBox(metaGroups);
		
		if(metaInfo != null) {
			String path = snpList.getDataStorage().getMetaInformationManager().getPathFor(metaInfo);
			String[] split = path.split("/");
			metaInfoGroupBox.setSelectedIndex(getIndex(split[0], metaGroups));
		}
		
		panel.add(new JLabel("Select Meta-Information Group"));
		panel.add(metaInfoGroupBox);
		
		String selection = (String)metaInfoGroupBox.getSelectedItem();
		metaInfoBox = new JComboBox(mim.get(selection).toArray(new MetaInformation[0]));
		
		final JLabel selectGeneLabel = new JLabel("Select a gene:");
		
		setupGeneSelectionBox();
		selectGeneLabel.setVisible(false);
		geneSelection.setVisible(false);
		
		if(metaInfo != null) {
			String path = snpList.getDataStorage().getMetaInformationManager().getPathFor(metaInfo);
			String[] split = path.split("/");
			metaInfoBox.setSelectedIndex(Integer.parseInt(split[1]));
			
			if(metaInfo instanceof SLResults) {
				geneSelection.setVisible(true);
				selectGeneLabel.setVisible(true);
			} else if(metaInfo instanceof TLResults) {
				geneSelection.setVisible(true);
				selectGeneLabel.setVisible(true);
			} else {
				geneSelection.setVisible(false);
				selectGeneLabel.setVisible(false);
			}
			
			
		}

		metaInfoGroupBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String groupID = (String)metaInfoGroupBox.getSelectedItem();
				metaInfoBox.removeAllItems();
				List<MetaInformation> mis = mim.get(groupID);
				for(MetaInformation mi : mis)
					metaInfoBox.addItem(mi);
			}
		});
		
		metaInfoBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				metaInfo = (MetaInformation)metaInfoBox.getSelectedItem();
				
				if(metaInfo instanceof SLResults) {
					geneSelection.setVisible(true);
					selectGeneLabel.setVisible(true);
				} else if(metaInfo instanceof TLResults) {
					geneSelection.setVisible(true);
					selectGeneLabel.setVisible(true);
				} else {
					geneSelection.setVisible(false);
					selectGeneLabel.setVisible(false);
				}
				
				fireChanged();
			}
		});
		
		panel.add(new JLabel("Select Meta-Information Type"));
		panel.add(metaInfoBox);
		
		panel.add(selectGeneLabel);
		panel.add(geneSelection);
		
		metaInfo = (MetaInformation)metaInfoBox.getSelectedItem();
		
		panel.setBorder(BorderFactory.createTitledBorder("Meta-Information Filter Settings"));
		
		return panel;
	}
	
	private void setupGeneSelectionBox() {
		final GeneList genes = snpList.getDataStorage().getGenes();
		
		geneSelection = new JComboBox();
		int selectionIndex = 0;
		for(int i = 0; i < genes.size(); i++) {
			String name = ((Gene)genes.getGene(i)).getName();
			if(name.equals(gene)) {
				selectionIndex = i;
			}
			geneSelection.addItem(name);
		}
		
		if(metaInfo != null) {
			geneSelection.setSelectedIndex(selectionIndex);
		}
		
		geneSelection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gene = (String)geneSelection.getSelectedItem();
				fireChanged();
			}
		});
		
		gene = (String)geneSelection.getSelectedItem();
	}

	@Override
	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("MetaInformationFilter","");
		String path = snpList.getDataStorage().getMetaInformationManager().getPathFor(metaInfo);
		parent.addChild("MetaInfo", path);
		parent.addChild("Gene", gene);
		return parent;
	}

	@Override
	public void fromStorageNode(StorageNode storageNode) {
		Object o = storageNode.getChild("MetaInfo");
		if(o != null) {
			String path = ((StorageNode) o).Value;
			metaInfo = snpList.getDataStorage().getMetaInformationManager().getFromPath(path);
		}
		Object o2 = storageNode.getChild("Gene");
		if(o2 != null) {
			gene = ((StorageNode)o2).Value;
		}
	}

	@Override
	public Class<?>[] getDataClass() {
		return snpList == null ?
				null: metaInfo == null ? 
						null : getClasses();
	}
	
	public Class<?>[] getClasses() {
		Class<?> theClass = metaInfo.getResultClass();
		return new Class<?>[]{ theClass };
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return SNP.class.isAssignableFrom(inputClass[0]);
	}

	@Override
	public String toString() {
		if(metaInfo == null) {
			return "No Meta-Information selected";
		}
		return metaInfo.getName();
	}

	@Override
	protected Object convert(SNP value) {
		if(metaInfo instanceof StatisticalTestResult) {
			return ((StatisticalTestResult)metaInfo).getPValue(value);
		}
		
		if(metaInfo instanceof SLResults) {
			if(gene != null) {
				Gene g = snpList.getDataStorage().getGenes().getGene(gene);
				return ((SLResults)metaInfo).get(g).get(value);
			}
		}
		
		return metaInfo;
	}

	@Override
	public String getName() {
		return "Meta Information";
	}

	@Override
	public String getType() {
		return "data.snplist.filter.mio";
	}

	@Override
	public String getDescription() {
		return "Filter SNPLists by Meta-Information";
	}
	
	private int getIndex(String s, String[] values) {
		for(int i = 0; i < values.length; i++)
			if(s.equals(values[i]))
				return i;
		return 0;
	}
}
