package mayday.Reveal.visualizations.SNPSummaryPlot;

import java.awt.Color;
import java.util.Set;

import mayday.Reveal.data.GeneList;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.viewmodel.SNPSorter;
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
	
	private ColorSetting selectionColor;
	private RestrictedStringSetting snpOrder;
	
	private BooleanSetting horizontalAggregation;
	private BooleanSetting refWithChange;
	
	private String oldSNPOrder = SNPSorter.GENOMIC_LOCATION;
	
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
			
		addSetting(cellWidth = new IntSetting("Cell Width", null, 20));
		addSetting(selectionColor = new ColorSetting("Selection Color", null, Color.RED));
		addSetting(horizontalAggregation = new BooleanSetting("Stacked genotype cohort summary", null, false));
		refWithChange = new BooleanSetting("Reference nucleotide change", null, false);
		addSetting(snpOrder = new RestrictedStringSetting("Sort SNPs", null, 0, SNPSorter.GENOMIC_LOCATION));
		
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
			if(sortingChanged()) {
				SNPList newList = plot.getViewModel().sortSNPs(plot.getSNPs(), getSNPOrder());
				plot.setSNPList(newList);
				plot.calculateViewElements();
				plot.updatePlot();
				System.gc();
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

	public boolean getRefWithChange() {
		return refWithChange.getBooleanValue();
	}
}
