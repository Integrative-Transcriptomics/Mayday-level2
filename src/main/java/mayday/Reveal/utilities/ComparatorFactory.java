package mayday.Reveal.utilities;

import java.util.Comparator;

import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.meta.StatisticalTestResult;

/**
 * @author jaeger
 *
 */
public class ComparatorFactory {
	
	/**
	 * @param unsorted 
	 * @return comparator for snp position on the chromosomes
	 */
	public static Comparator<Integer> getLocationComparator(final SNVList unsorted) {
		Comparator<Integer> locationComparator = new Comparator<Integer>() {
			@Override
			public int compare(Integer snpIndex1, Integer snpIndex2) {
				SNV s1 = unsorted.get(snpIndex1);
				SNV s2 = unsorted.get(snpIndex2);
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

	public static Comparator<Integer> getStatTestComparator(final SNVList unsorted, final StatisticalTestResult str) {
		Comparator<Integer> statTestComparator = new Comparator<Integer>() {
			@Override
			public int compare(Integer snpIndex1, Integer snpIndex2) {
				SNV s1 = unsorted.get(snpIndex1);
				SNV s2 = unsorted.get(snpIndex2);
				double p1 = str.getPValue(s1);
				double p2 = str.getPValue(s2);
				return Double.compare(p1, p2);
			}
		};
		return statTestComparator;
	}
}
