package mayday.GWAS.visualizations.SNPSummaryPlot;

import java.awt.Color;
import java.util.Set;

import mayday.GWAS.data.Gene;
import mayday.GWAS.data.GeneList;
import mayday.GWAS.data.Subject;
import mayday.GWAS.data.SubjectList;
import mayday.GWAS.viewmodel.SNPSorter;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

/**
 * @author jaeger
 *
 */
public class SNPSummaryPlotSetting extends HierarchicalSetting {

	private SNPSummaryPlot plot;
	
	private IntSetting cellWidth;
	private RestrictedStringSetting gene;
	private IntSetting person;
	
	private ColorSetting selectionColor;
	private RestrictedStringSetting snpOrder;
	
	private BooleanSetting horizontalAggregation;
	private BooleanSetting refWithChange;
	
	private String oldSNPOrder = SNPSorter.GENOMIC_LOCATION;
	private Subject oldPerson;
	
	/**
	 * @param plot
	 */
	public SNPSummaryPlotSetting(SNPSummaryPlot plot) {
		super("SNP Summary Plot Setting");
		this.plot = plot;
		
		GeneList genes = plot.getData().getGenes();
		String[] geneNames = new String[genes.size()];
		for(int i = 0; i < genes.size(); i++)
			geneNames[i] = genes.getGene(i).getName();
		
		Set<String> externalSNPListNames = plot.getData().getSNPListNames();
		String[] extNames = new String[externalSNPListNames.size()];
		int i = 0;
		for(String name : externalSNPListNames)
			extNames[i++] = name;
			
		SubjectList persons = plot.getData().getSubjects();
		oldPerson = persons.get(0);
			
		addSetting(gene = new RestrictedStringSetting("Gene", null, 0, geneNames));
		addSetting(person = new IntSetting("Person Index", null, 0, 0, persons.size(), true, false));
		addSetting(cellWidth = new IntSetting("Cell Width", null, 20));
		addSetting(selectionColor = new ColorSetting("Selection Color", null, Color.RED));
		addSetting(horizontalAggregation = new BooleanSetting("Stacked genotype cohort summary", null, false));
		refWithChange = new BooleanSetting("Reference nucleotide change", null, false);
//		addSetting(refWithChange);
		addSetting(snpOrder = new RestrictedStringSetting("Sort SNPs", null, 0, SNPSorter.GENOMIC_LOCATION, SNPSorter.P_VALUE, SNPSorter.MAJORITY_GENOTYPE));
		
		this.addChangeListener(new SNPSummaryChangeListener());
	}
	
	public SNPSummaryPlotSetting clone() {
		SNPSummaryPlotSetting sss = new SNPSummaryPlotSetting(plot);
		sss.fromPrefNode(this.toPrefNode());
		return sss;
	}
	
	/**
	 * @return the cell width
	 */
	public int getCellWidth() {
		return this.cellWidth.getIntValue();
	}
	
	/**
	 * @return the chosen gene
	 */
	public Gene getGene() {
		String geneName = this.gene.getStringValue();
		return plot.getData().getGenes().getGene(geneName);
	}
	
	/**
	 * @return the chosen person
	 */
	public Subject getPerson() {
		int personIndex = person.getIntValue();
		return plot.getData().getSubjects().get(personIndex);
	}
	
	/**
	 * @return selection color
	 */
	public Color getSelectionColor() {
		return this.selectionColor.getColorValue();
	}
	
	/**
	 * @return snp ordering method
	 */
	public String getSNPOrder() {
		return this.snpOrder.getStringValue();
	}
	
	private class SNPSummaryChangeListener implements SettingChangeListener {
		
		@Override
		public void stateChanged(SettingChangeEvent e) {
			if(personChanged()) {
				plot.updatePersonBars();
			}
			
			if(sortingChanged()) {
				plot.getViewModel().sortSNPs(plot.getSNPs(), getSNPOrder(), getGene());
			}
			
			plot.updatePlot();
		}
	}
	
	/**
	 * @param cellWidth
	 */
	public void setCellWidth(int cellWidth) {
		this.cellWidth.setIntValue(cellWidth);
	}

	/**
	 * @return true if aggregation row should be stacked
	 */
	public boolean getHorizontalAggregation() {
		return this.horizontalAggregation.getBooleanValue();
	}
	
	private boolean sortingChanged() {
		boolean change = !oldSNPOrder.equals(getSNPOrder());
		if(change) {
			oldSNPOrder = getSNPOrder();
			return true;
		}
		return false;
	}
	
	private boolean personChanged() {
		boolean change = oldPerson != getPerson();
		if(change) {
			oldPerson = getPerson();
			return true;
		}
		return false;
	}

	public boolean getRefWithChange() {
		return refWithChange.getBooleanValue();
	}
}
