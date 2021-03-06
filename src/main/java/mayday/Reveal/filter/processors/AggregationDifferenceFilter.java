package mayday.Reveal.filter.processors;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNV;
import mayday.Reveal.filter.AbstractDataProcessor;
import mayday.Reveal.utilities.SNVLists;

public class AggregationDifferenceFilter extends AbstractDataProcessor<SNV, Double> {
	
	@Override
	public void dispose() {
		//nothing to do
	}

	@Override
	public Class<?>[] getDataClass() {
		return snpList == null ? null : new Class<?>[]{Double.class};
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return SNV.class.isAssignableFrom(inputClass[0]);
	}

	@Override
	public String toString() {
		return "Aggregation Difference";
	}

	@Override
	protected Double convert(SNV value) {
		int index = value.getIndex();
		DataStorage data = snpList.getDataStorage();
		double[] affected = SNVLists.getGenotypeDistribution(data, index, true);
		double[] unaffected = SNVLists.getGenotypeDistribution(data, index, false);
		double res = Math.abs(affected[maxIndex(affected)] - unaffected[maxIndex(unaffected)]);
		return res;
	}

	@Override
	public String getName() {
		return "Aggregation Difference";
	}

	@Override
	public String getType() {
		return "data.snplist.filter.aggDiff";
	}

	@Override
	public String getDescription() {
		return "Filter SNPs based on difference in strength of the aggregated genotype distributions";
	}
	
	private int maxIndex(double[] a) {
		int maxIndex = 0;
		double max = Double.MIN_VALUE;
		for(int i = 0; i < a.length; i++) {
			if(a[i] > max) {
				max = a[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}
}
