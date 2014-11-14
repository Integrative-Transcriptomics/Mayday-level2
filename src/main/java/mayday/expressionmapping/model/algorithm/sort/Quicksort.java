/**
 * 
 */
package mayday.expressionmapping.model.algorithm.sort;

import java.util.Arrays;

import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.container.PointList;

/**
 * @author Stephan Gade
 *
 */
public class Quicksort implements SortAlgorithm {

	private PointList<? extends DataPoint> points;
	private int mod;

	/*  1 -> ascending
	 * -1 -> descending
	 */
	private int sortOrder;
	private int index;


	/* (non-Javadoc)
	 * @see model.algorithm.SortAlgorithm#sortAsc(java.util.List)
	 */
	public int[] sortAsc(PointList<? extends DataPoint> points, int mod, int index) {

		this.points = points;

		if (index < 0 || index >= this.points.getDimension()) {
			throw new IllegalArgumentException("The index has to be > 0 and < dim:" + index);
		}
		this.index = index;

		this.mod = mod;

		this.sortOrder = 1;

		Integer[] tmp = new Integer[this.points.size()];

		int[] ret = new int[this.points.size()];

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

	/* (non-Javadoc)
	 * @see model.algorithm.SortAlgorithm#sortDesc(java.util.List)
	 */
	public int[] sortDesc(PointList<? extends DataPoint> points, int mod, int index) {

		this.points = points;

		if (index < 0 || index >= this.points.getDimension()) {
			throw new IllegalArgumentException("The index has to be > 0 and < dim:" + index);
		}
		this.index = index;

		this.mod = mod;

		this.sortOrder = -1;

		Integer[] tmp = new Integer[this.points.size()];

		int[] ret = new int[this.points.size()];

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

//	private int valueCompareTo(DataPoint a, DataPoint b) {
//
//		double[] tmpA = a.getValues();
//
//		double[] tmpB = b.getValues();
//
//		/* should not be happen, points in one PointList have same dimension
//		 *
//		 */
////		if (tmpA.length < tmpB.length)
////			return -1;
////
////		if (tmpA.length > tmpB.length)
////			return 1;
//
//		/* tmpA.length = points.getDimension, therefor, index lies within
//		 *
//		 */
//		for (int i = this.index; i < tmpA.length; ++i) {
//
//			if (tmpA[i] < tmpB[i]) {
//				return -1;
//			}
//			if (tmpA[i] > tmpB[i]) {
//				return 1;
//			}
//		}
//
//		return 0;
//
//	}
//
//	private int coordinateCompareTo(DataPoint a, DataPoint b) {
//
//		double[] tmpA = a.getCoordinates();
//
//		double[] tmpB = b.getCoordinates();
//
//		if (tmpA.length < tmpB.length) {
//			return -1;
//		}
//		if (tmpA.length > tmpB.length) {
//			return 1;
//		}
//	}

	
	public int compare(Integer o1, Integer o2) {


		//using Autboxing from Java
		//get the two integer Values we want to compare
		int i1 = (Integer) o1;
		int i2 = (Integer) o2;

		if (i1 < 0 || i1 >= points.size()) {
			throw new ArrayIndexOutOfBoundsException("The first integer you want to compare is not an index of the point list!");
		}
		if (i2 < 0 || i2 >= points.size()) {
			throw new ArrayIndexOutOfBoundsException("The second integer you want to compare is not an index of the point list!");
		}
		double[] tmpA;
		double[] tmpB;

		switch (this.mod) {

			case SortAlgorithm.VALUES: {

				tmpA = this.points.get(i1).getValues();
				tmpB = this.points.get(i2).getValues();

				break;
			}

			case SortAlgorithm.COORDINATES: {

				tmpA = this.points.get(i1).getCoordinates();
				tmpB = this.points.get(i2).getCoordinates();

				break;

			}

			default: {

				System.err.println("Given mod doesn't match any modus, assuming values to be sort.");

				tmpA = this.points.get(i1).getValues();
				tmpB = this.points.get(i2).getValues();

				break;

			}
		}

		/* should not be happen, points in one PointList have same dimension
		 *
		 */
//		if (tmpA.length < tmpB.length)
//			return -1;
//
//		if (tmpA.length > tmpB.length)
//			return 1;

		/* tmpA.length = points.getDimension, therefor, index lies within
		 *
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



