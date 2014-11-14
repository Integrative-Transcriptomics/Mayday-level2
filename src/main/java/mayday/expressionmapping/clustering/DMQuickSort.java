package mayday.expressionmapping.clustering;

import java.util.Arrays;
import java.util.Comparator;

import mayday.core.structures.linalg.matrix.DoubleMatrix;

/**
 * @author jaeger
 *
 */
public class DMQuickSort implements Comparator<Integer> {
	
	/*  
	 * 1 -> ascending
	 * -1 -> descending
	 */
	private int sortOrder;
	private int index;
	private DoubleMatrix data;
	
	/**
	 * @param data
	 * @param mod
	 * @param index
	 * @return sorted index list in ascending order
	 */
	public int[] sortAsc(DoubleMatrix data, int index) {

		this.data = data;

		if (index < 0 || index >= this.data.nrow()) {
			throw new IllegalArgumentException("The index has to be > 0 and < dim:" + index);
		}
		this.index = index;

		this.sortOrder = 1;

		Integer[] tmp = new Integer[this.data.nrow()];

		int[] ret = new int[this.data.nrow()];

		/* fill index list
		 */
		for (int i = 0; i < tmp.length; ++i) {
			tmp[i] = i;
		}
		Arrays.sort(tmp, this);

		for (int i = 0; i < ret.length; ++i) {
			ret[i] = tmp[i];  /* using autoboxing  */

		}
		return ret;
	}
	
	
	@Override
	public int compare(Integer o1, Integer o2) {
		//using Autboxing from Java
		//get the two integer Values we want to compare
		int i1 = (Integer) o1;
		int i2 = (Integer) o2;

		if (i1 < 0 || i1 >= data.nrow()) {
			throw new ArrayIndexOutOfBoundsException("The first integer you want to compare is not an index of the point list!");
		}
		if (i2 < 0 || i2 >= data.nrow()) {
			throw new ArrayIndexOutOfBoundsException("The second integer you want to compare is not an index of the point list!");
		}
		
		double[] tmpA = data.getRow(i1).toArray();
		double[] tmpB = data.getRow(i2).toArray();

		/* 
		 * should not be happen, points in one PointList have same dimension
		 */
//		if (tmpA.length < tmpB.length)
//			return -1;
//
//		if (tmpA.length > tmpB.length)
//			return 1;

		/* 
		 * tmpA.length = points.getDimension, meaning that index lies within
		 */
		for (int i = this.index; i < tmpA.length; ++i) {
			if (tmpA[i] < tmpB[i]) {
				return -1 * this.sortOrder;
			}
			if (tmpA[i] > tmpB[i]) {
				return 1 * this.sortOrder;
			}
		}

		return 0;
	}
}
