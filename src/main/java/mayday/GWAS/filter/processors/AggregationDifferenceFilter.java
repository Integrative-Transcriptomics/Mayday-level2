package mayday.GWAS.filter.processors;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.SNP;
import mayday.GWAS.filter.AbstractDataProcessor;
import mayday.GWAS.utilities.SNPLists;

public class AggregationDifferenceFilter extends AbstractDataProcessor<SNP, Double> {
	
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
		return SNP.class.isAssignableFrom(inputClass[0]);
	}

	@Override
	public String toString() {
		return "Aggregation Difference";
	}

	@Override
	protected Double convert(SNP value) {
		int index = value.getIndex();
		DataStorage data = snpList.getDataStorage();
		double[] affected = SNPLists.getGenotypeDistribution(data, index, true);
		double[] unaffected = SNPLists.getGenotypeDistribution(data, index, false);
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
