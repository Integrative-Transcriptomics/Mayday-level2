package mayday.Reveal.utilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mayday.Reveal.data.SNPList;
import mayday.Reveal.data.meta.StatisticalTestResult;

/**
 * @author jaeger
 *
 */
public class SNPSorter {
	
	public static final String GENOMIC_LOCATION = "Genomic Location";
	public static final String STATISTICAL_TEST = "Statistical Test";
	public static final String NONE = "None";
	
	private HashMap<Integer, Integer> sortedSNPs = new HashMap<Integer, Integer>();

	public SNPSorter(SNPList initial) {
		for(int i = 0; i < initial.size(); i++) {
			sortedSNPs.put(i, initial.get(i).getIndex());
		}
	}
	
	/**
	 * @param unsorted
	 * @param sortOption
	 * @return sorted SNPList
	 */
	public void sortSNPs(SNPList unsorted, String sortOption, StatisticalTestResult str) {
		List<Integer> indexList = unsorted.getIndexList();
		
		if(sortOption.equals(GENOMIC_LOCATION)) {
			Collections.sort(indexList, ComparatorFactory.getLocationComparator(unsorted));
		} else if(sortOption.equals(STATISTICAL_TEST)) {
			Collections.sort(indexList, ComparatorFactory.getStatTestComparator(unsorted, str));
		} else if(sortOption.equals(NONE)) {
			restoreOrdering(unsorted);
		}
		
		for(int i = 0; i < indexList.size(); i++) {
			this.sortedSNPs.put(i, indexList.get(i));
		}
	}

	private void restoreOrdering(SNPList unsorted) {
		for(int i = 0; i < unsorted.size(); i++) {
			sortedSNPs.put(i, unsorted.get(i).getIndex());
		}
		
	}

	public int get(int i) {
		return this.sortedSNPs.get(i);
	}
}
