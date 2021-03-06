package mayday.Reveal.utilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mayday.Reveal.data.SNVList;
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

	public SNPSorter(SNVList initial) {
		for(int i = 0; i < initial.size(); i++) {
			sortedSNPs.put(i, initial.get(i).getIndex());
		}
	}
	
	/**
	 * @param unsorted
	 * @param sortOption
	 * @return sorted SNPList
	 */
	public void sortSNPs(SNVList unsorted, String sortOption, StatisticalTestResult str) {
		List<Integer> indexList = unsorted.getIndexList();
		
		switch(sortOption) {
		case GENOMIC_LOCATION:
			Collections.sort(indexList, ComparatorFactory.getLocationComparator(unsorted));
			break;
		case STATISTICAL_TEST:
			Collections.sort(indexList, ComparatorFactory.getStatTestComparator(unsorted, str));
			break;
		case NONE:
			restoreOrdering(unsorted);
			break;
		}
		
		for(int i = 0; i < indexList.size(); i++) {
			this.sortedSNPs.put(i, indexList.get(i));
		}
	}

	private void restoreOrdering(SNVList unsorted) {
		for(int i = 0; i < unsorted.size(); i++) {
			sortedSNPs.put(i, unsorted.get(i).getIndex());
		}
		
	}

	public int get(int i) {
		return this.sortedSNPs.get(i);
	}
}
