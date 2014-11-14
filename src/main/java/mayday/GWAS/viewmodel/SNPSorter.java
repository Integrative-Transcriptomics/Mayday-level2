package mayday.GWAS.viewmodel;

import java.util.Collections;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.Gene;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.utilities.ComparatorFactory;

/**
 * @author jaeger
 *
 */
public class SNPSorter {
	
	public static final String GENOMIC_LOCATION = "Genomic Location";
	public static final String P_VALUE = "p-Value";
	public static final String MAJORITY_GENOTYPE = "Majority Genotype";
	
	private DataStorage ds;
	
	/**
	 * @param ds
	 */
	public SNPSorter(DataStorage ds) {
		this.ds = ds;
	}

	/**
	 * @param unsorted
	 * @param sortOption
	 * @param g
	 * @return sorted SNPList
	 */
	public SNPList getSortedSNPList(SNPList unsorted, String sortOption, Gene g) {
		SNPList sorted = new SNPList("Sorted SNPs", ds);
		for(SNP s : unsorted) {
			sorted.add(s);
		}
		
		if(sortOption.equals(GENOMIC_LOCATION)) {
			Collections.sort(sorted, ComparatorFactory.getLocationComparator());
		}
		if(sortOption.equals(P_VALUE)) {
			Collections.sort(sorted, ComparatorFactory.getPValueComparator(ds, g));
		}
		if(sortOption.equals(MAJORITY_GENOTYPE)) {
			Collections.sort(sorted, ComparatorFactory.getMajorityGenotypeComparator(ds));
		}
		
		return sorted;
	}
}
