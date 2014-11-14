package mayday.GWAS.filter.processors;

import mayday.GWAS.data.SNP;
import mayday.GWAS.filter.AbstractDataProcessor;
import mayday.GWAS.utilities.SNPLists;

public class AggregationFilter extends AbstractDataProcessor<SNP, Boolean> {
	
	@Override
	public void dispose() {
		//nothing to do
	}

	@Override
	public Class<?>[] getDataClass() {
		return snpList == null ? null : new Class<?>[]{Boolean.class};
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
	protected Boolean convert(SNP value) {
		int index = value.getIndex();
		double[] affected = SNPLists.getGenotypeDistribution(snpList.getDataStorage(), index, true);
		double[] unaffected = SNPLists.getGenotypeDistribution(snpList.getDataStorage(), index, false);
		return (maxIndex(affected) != maxIndex(unaffected));
	}

	@Override
	public String getName() {
		return "Aggregation";
	}

	@Override
	public String getType() {
		return "data.snplist.filter.aggregation";
	}

	@Override
	public String getDescription() {
		return "Filter by difference after aggregation of genotypes";
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
