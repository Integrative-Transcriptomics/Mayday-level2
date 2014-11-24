package mayday.Reveal.visualizations.snpcharacterization;

import mayday.core.settings.generic.HierarchicalSetting;

public class SNPCharInformationTableSetting extends HierarchicalSetting {

	private SNPCharInformationTable table;
	
	public SNPCharInformationTableSetting(SNPCharInformationTable table) {
		super("SNP Characterization Information Table");
		
		this.table = table;
	}
	
	public SNPCharInformationTableSetting clone() {
		SNPCharInformationTableSetting s = new SNPCharInformationTableSetting(table);
		s.fromPrefNode(this.toPrefNode());
		return s;
	}
}
