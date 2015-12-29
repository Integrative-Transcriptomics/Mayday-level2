package mayday.Reveal.visualizations.tables.slrTable;

import mayday.Reveal.data.Gene;
import mayday.Reveal.data.GeneList;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class SLRTableSetting extends HierarchicalSetting {

	private SLRTableVisualization slrTable;
	
	private GeneList genes;
	private RestrictedStringSetting geneSelection;
	
	public SLRTableSetting(SLRTableVisualization slrTable) {
		super("SLR Table Setting");
		this.slrTable = slrTable;
		this.genes = slrTable.getData().getGenes();
		
		String[] geneNames = this.genes.getGeneNames();
		addSetting(geneSelection = new RestrictedStringSetting("Gene", 
				"Select the gene that you want sing locus results to be displayed for.", 
				0, 
				geneNames));
		
		this.addChangeListener(new SLRTableChangeListener());
	}
	
	public Gene getSelectedGene() {
		int geneIndex = geneSelection.getSelectedIndex();
		Gene g = (Gene)genes.getProbe(geneIndex);
		return g;
	}
	
	public SLRTableSetting clone() {
		SLRTableSetting s = new SLRTableSetting(slrTable);
		s.fromPrefNode(this.toPrefNode());
		return s;
	}
	
	private class SLRTableChangeListener implements SettingChangeListener {
		@Override
		public void stateChanged(SettingChangeEvent e) {
			if(e.getSource().equals(geneSelection)) {
				slrTable.updateTableData();
			}
			slrTable.updatePlot();
		}
	}
}
