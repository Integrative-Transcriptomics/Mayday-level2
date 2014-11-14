package mayday.GWAS.visualizations.SNPExpHeatMap;

import mayday.core.settings.generic.HierarchicalSetting;

public class SNPExpressionHeatMapSetting extends HierarchicalSetting {

	private SNPExpressionHeatMap plot;
	
	public SNPExpressionHeatMapSetting(SNPExpressionHeatMap plot) {
		super("SNP Expression Heat Map Setting");
	}
	
	public SNPExpressionHeatMapSetting clone() {
		SNPExpressionHeatMapSetting sehms = new SNPExpressionHeatMapSetting(plot);
		sehms.fromPrefNode(this.toPrefNode());
		return sehms;
	}
}
