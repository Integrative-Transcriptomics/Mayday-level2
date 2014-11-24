package mayday.Reveal.viewmodel;

import java.util.Collections;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.utilities.ComparatorFactory;

/**
 * @author jaeger
 *
 */
public class SNPSorter {
	
	public static final String GENOMIC_LOCATION = "Genomic Location";
	
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
	 * @return sorted SNPList
	 */
	public SNPList getSortedSNPList(SNPList unsorted, String sortOption) {
		SNPList sorted = new SNPList("Sorted SNPs", ds);
		for(SNP s : unsorted) {
			sorted.add(s);
		}
		
		if(sortOption.equals(GENOMIC_LOCATION)) {
			Collections.sort(sorted, ComparatorFactory.getLocationComparator());
		}
		
		return sorted;
	}
}
