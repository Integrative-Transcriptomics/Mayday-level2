package mayday.Reveal.utilities;

import java.util.Comparator;

import mayday.Reveal.data.SNP;

/**
 * @author jaeger
 *
 */
public class ComparatorFactory {
	
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
}
