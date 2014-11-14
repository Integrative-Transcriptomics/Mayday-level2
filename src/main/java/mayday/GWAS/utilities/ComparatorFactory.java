package mayday.GWAS.utilities;

import java.util.Comparator;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.Gene;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.meta.SLResults;
import mayday.GWAS.data.meta.SingleLocusResult;

/**
 * @author jaeger
 *
 */
public class ComparatorFactory {
	

	/**
	 * @param ds
	 * @param g
	 * @return comparator for snp p-value associated with the gene g
	 */
	public static Comparator<SNP> getPValueComparator(DataStorage ds, Gene g) {
		final SingleLocusResult slr = ((SLResults)(ds.getMetaInformationManager().get(SLResults.MYTYPE).get(0))).get(g);
		Comparator<SNP> pValueComparator = new Comparator<SNP>() {
			@Override
			public int compare(SNP s1, SNP s2) {
				if(slr.get(s1).p == -1)
					return 1;
				if(slr.get(s2).p == -1)
					return -1;
				if(slr.get(s1).p < slr.get(s2).p)
					return -1;
				if(slr.get(s1).p > slr.get(s2).p)
					return 1;
				return 0;
			}
		};
		return pValueComparator;
	}
	
	/**
	 * @return comparator for snp position on the chromosomes
	 */
	public static Comparator<SNP> getLocationComparator() {
		Comparator<SNP> locationComparator = new Comparator<SNP>() {
			@Override
			public int compare(SNP s1, SNP s2) {
				int chrCompare = s1.getChromosome().compareTo(s2.getChromosome());
				if(chrCompare < 0 || chrCompare > 0) {
					return chrCompare;
				} else {
					if(s1.getPosition() < s2.getPosition()) return -1;
					if(s1.getPosition() > s2.getPosition()) return 1;
					return 0;
				}
			}
		};
		return locationComparator;
	}

	/**
	 * @param ds
	 * @return comparator for majority genotype
	 */
	public static Comparator<SNP> getMajorityGenotypeComparator(
			DataStorage ds) {
		Comparator<SNP> majorityGenotypeComparator = new Comparator<SNP>() {
			@Override
			public int compare(SNP s1, SNP s2) {
				// TODO implement this
				return 0;
			}
		};
		return majorityGenotypeComparator;
	}
}
