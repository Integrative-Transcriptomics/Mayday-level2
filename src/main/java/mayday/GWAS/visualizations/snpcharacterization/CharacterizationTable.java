package mayday.GWAS.visualizations.snpcharacterization;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import mayday.GWAS.data.meta.snpcharacterization.SNPCharacterization;
import mayday.GWAS.data.meta.snpcharacterization.SNPCharacterizations;
import mayday.GWAS.io.gff3.GFFElement;

public class CharacterizationTable {
	
	private JTable table;
	
	@SuppressWarnings("serial")
	public CharacterizationTable(SNPCharacterizations snpChars) {
		Object[][] data = new Object[snpChars.size()][];
		Object[] columnNames = new Object[]{
				"Subject ID",
				"Subject Name",
				"SNP",
				"Reference Nucleotide",
				"Genotype",
				"Chromosome",
				"Strand",
				"Frame",
				"SNP Position", 
				"Target Element",
				"Target Name",
				"Target Start",
				"Target End", 
				"Class",
				"Non-Synonymous",
				"Impact",
				"AA Change"};
		
		for(int i = 0; i < snpChars.size(); i++) {
			SNPCharacterization snpChar = snpChars.get(i);
			GFFElement e = snpChar.getGFFElement();
			
			Object[] dataRow = new Object[] {
					snpChar.getPersonID(),
					snpChar.getPersonName(),
					snpChar.getSNP().getID(),
					snpChar.getSNP().getReferenceNucleotide(),
					snpChar.getIndividualNucleotideA() + "|" + snpChar.getIndividualNucleotideB(),
					snpChar.getSNP().getChromosome(),
					e != null ? e.getChromosomalLocation().getStrand() : "NA",
					e != null ? e.getPhase() : "NA",
					snpChar.getSNP().getPosition(),
					e != null ? e.getFeature() : "NA",
					e != null ? e.getName() : "NA",
					e != null ? e.getChromosomalLocation().getStart() : "NA",
					e != null ? e.getChromosomalLocation().getStop() : "NA",
					snpChar.getSNPClass(),
					new NonSynField(snpChar.nonSynonymous()),
					snpChar.getImpact(),
					new SequenceComparisonField(snpChar.getOriginalAA(), snpChar.getModifiedAAA(), snpChar.getModifiedAAB())};
			data[i] = dataRow;
		}
		
		table = new JTable() {
			private NonSynFieldRenderer nonSynRenderer = new NonSynFieldRenderer();
			private ImpactFieldRenderer impactRenderer = new ImpactFieldRenderer();
			private AAChangeRenderer aaChangeRenderer = new AAChangeRenderer();
			
			public TableCellRenderer getCellRenderer(int row, int column) {
				
				if(this.getModel().getColumnName(column).equals("Non-Synonymous")) {
					return nonSynRenderer;
				}
				
				if(this.getModel().getColumnName(column).equals("Impact")) {
					return impactRenderer;
				}
				
				if(this.getModel().getColumnName(column).equals("AA Change")) {
					return aaChangeRenderer;
				}
				
		        return super.getCellRenderer(row, column);
		    }
		};
		
		table.setModel(new DefaultTableModel(data, columnNames));
		table.setAutoCreateRowSorter(true);
	}
	
	public JComponent getContentPane() {
		JScrollPane scroller = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		return scroller;
	}
}
