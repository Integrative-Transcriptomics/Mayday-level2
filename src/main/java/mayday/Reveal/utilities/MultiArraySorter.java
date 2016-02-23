package mayday.Reveal.utilities;

import java.util.Arrays;
import java.util.Comparator;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.core.structures.maps.BidirectionalHashMap;

public class MultiArraySorter {

	public static Integer[] sort(final AbstractVector template, final boolean descending) {
		Integer[] indices = new Integer[template.size()];
		
		for(int i = 0; i < indices.length; i++)
			indices[i] = i;
		
		Comparator<Integer> c = new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				Double d1 = template.get(o1);
				Double d2 = template.get(o2);
				int res = Double.compare(d1, d2);
				if(descending)
					res *= -1;
				return res;
			}
		};
		
		Arrays.sort(indices, c);
		
		return indices;
	}
	
	public static DoubleVector sort(Integer[] indices, DoubleVector toSort) {
		DoubleVector sorted = new DoubleVector(toSort.size());
		for(int i = 0; i < indices.length; i++) {
			Integer oldIndex = indices[i];
			double value = toSort.get(oldIndex);
			sorted.set(i, value);
		}
		return sorted;
	}
	
	public static BidirectionalHashMap<Integer, Integer> indicesToMap(Integer[] indices, BidirectionalHashMap<Integer, Integer> toSort) {
		BidirectionalHashMap<Integer, Integer> sorted = new BidirectionalHashMap<Integer, Integer>();
		for(int i = 0; i < indices.length; i++) {
			sorted.put(i, indices[i]);
		}
		return sorted;
	}
}
