package mayday.GWAS.visualizations.tables.statsTable;

import mayday.core.settings.generic.HierarchicalSetting;

public class StatisticalResultTableSetting extends HierarchicalSetting {

	private StatisticalResultTableVisualization statTableViz;
	
	public StatisticalResultTableSetting(StatisticalResultTableVisualization statTableViz) {
		super("Statistics Table Setting");
		this.statTableViz = statTableViz;
	}
	
	public StatisticalResultTableSetting clone() {
		StatisticalResultTableSetting s = new StatisticalResultTableSetting(statTableViz);
		s.fromPrefNode(this.toPrefNode());
		return s;
	}
}
