package mayday.Reveal.visualizations.snpmap.aggregation;

import java.util.HashMap;

import mayday.core.structures.linalg.vector.DoubleVector;

public class Aggregator {

	public AggregatorSetting setting;
	
	private double aggregationValue = 0;
	private double frequency = 0;
	
	public Aggregator() {
		this.setting = new AggregatorSetting(this);
	}
	
	public void aggregateGene(DoubleVector values) {
		aggregateMeta(setting.getAggregationMethod(), values);
	}
	
	public double getAggregationValue() {
		return this.aggregationValue;
	}
	
	public double getFrequency() {
		return this.frequency;
	}
	
	public void aggregate(DoubleVector values) {
		aggregationValue = getMaxCountValue(values);
		frequency = getFrequency(aggregationValue, values);
	}
	
	public void aggregateMeta(int method, DoubleVector values) {
		switch(method) {
		case AggregatorSetting.MAX:
			aggregationValue = values.max();
			break;
		case AggregatorSetting.MEAN:
			aggregationValue = values.mean();
			break;
		case AggregatorSetting.MEDIAN:
			aggregationValue = values.median();
			break;
		case AggregatorSetting.MIN:
			aggregationValue = values.min();
			break;
		}
		
		frequency = 1;
	}
	
	public double getMaxCountValue(DoubleVector values) {
		double maxCountValue = -1;
		int max = 0;
		HashMap<Double, Integer> counts = new HashMap<Double, Integer>();
		
		for(int i = 0; i < values.size(); i++) {
			if(counts.containsKey(values.get(i))) {
				int oldCount = counts.get(values.get(i));
				counts.put(values.get(i), oldCount+1);
			} else {
				counts.put(values.get(i), 0);
			}
		}
		
		for(double value : counts.keySet()) {
			if(counts.get(value) > max) {
				maxCountValue = value;
				max = counts.get(value);
			}
		}
		
		return maxCountValue;
	}
	
	public double getFrequency(double value, DoubleVector values) {
		int count = 0;
		for(int i = 0; i < values.size(); i++) {
			if(Double.compare(value, values.get(i)) == 0) {
				count++;
			}
		}
		return (double)count / values.size();
	}
}
