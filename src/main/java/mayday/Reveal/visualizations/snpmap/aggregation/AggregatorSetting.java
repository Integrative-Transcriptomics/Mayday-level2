package mayday.Reveal.visualizations.snpmap.aggregation;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class AggregatorSetting extends HierarchicalSetting {

	private Aggregator ag;
	
	public static final int MAX = 0;
	public static final int MEAN = 1;
	public static final int MEDIAN = 2;
	public static final int MIN = 3;
	
	private String[] agMethods = {"Max", "Mean", "Median", "Min"};
	
	private RestrictedStringSetting aggregationMethod;
	
	public AggregatorSetting(Aggregator ag) {
		super("Aggregator");
		this.ag = ag;
		
		addSetting(aggregationMethod = new RestrictedStringSetting("Gene Aggregation Method", "Choose an aggregation method for gene expression value aggregation", 1, agMethods));
	}
	
	public int getAggregationMethod() {
		return this.aggregationMethod.getSelectedIndex();
	}
	
	public AggregatorSetting clone() {
		AggregatorSetting s = new AggregatorSetting(ag);
		s.fromPrefNode(this.toPrefNode());
		return s;
	}
}
