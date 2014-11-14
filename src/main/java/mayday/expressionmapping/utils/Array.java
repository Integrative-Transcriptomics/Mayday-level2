/**
 *
 */
package mayday.expressionmapping.utils;

//~--- non-JDK imports --------------------------------------------------------


//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;

/**
 * @author Stephan Gade
 *
 */
public class Array {
	public static final Random rand = new Random();

	public static double min(double[] input) {
		double res = input[0];

		for (int i = 1; i < input.length; ++i) {
			if (res > input[i]) {
				res = input[i];
			}
		}

		return res;
	}

	public static int minIndex(double[] input) {
		if (input.length < 1) {
			return -1;
		}

		TIntArrayList tmpIndex = new TIntArrayList();

		/*
		 *  fin minimum in array
		 */
		 double min = input[0];

		for (int i = 0; i < input.length; ++i) {
			if (input[i] < min) {
				min = input[i];
			}
		}

		for (int i = 0; i < input.length; ++i) {
			if (input[i] == min) {
				tmpIndex.add(i);
			}
		}

		/*
		 * return random value of indices with a minimum value
		 */
		 return tmpIndex.get(rand.nextInt(tmpIndex.size()));
	}

	public static double max(double[] input) {
		double res = input[0];

		for (int i = 1; i < input.length; ++i) {
			if (res < input[i]) {
				res = input[i];
			}
		}

		return res;
	}

	public static double sum(double[] input) {
		double res = 0;

		for (int i = 0; i < input.length; ++i) {
			res += input[i];
		}

		return res;
	}

	public static double abssum(double[] input) {
		double res = 0;

		for (int i = 0; i < input.length; ++i) {
			res += Math.abs(input[i]);
		}

		return res;
	}

	public static double mean(double[] input) {
		if (input.length == 0) {
			return Double.NaN;
		}

		double res = 0;

		for (int i = 0; i < input.length; ++i) {
			res += input[i];
		}

		res /= input.length;

		return res;
	}

	public static float mean(float[] input)  {

		if (input.length == 0) {
			return Float.NaN;
		}

		float res = 0;

		for (int i = 0; i < input.length; ++i)  {
			res+=input[i];
		}

		res /= input.length;

		return res;

	}

	/**
	 * Returns a sub array of input defined by indices
	 * @param input The array of double values we want to get a subset
	 * @param indices The indices defining the subset
	 * @return The subset
	 */
	public static double[] subArray(double[] input, int[] indices) {

		double ret[] = new double[indices.length];

		for(int i = 0; i < ret.length; ++i) {

			ret[i] = input[indices[i]];

		}

		return ret;

	}

	public static double[] subArray(double[] input, TIntArrayList indices) {

		double ret[] = new double[indices.size()];

		for(int i = 0; i < ret.length; ++i) {

			ret[i] = input[indices.get(i)];

		}

		return ret;

	}
	public static String arrayToString(double[] input) {
		StringBuffer ret   = new StringBuffer();
		int          bound = input.length - 1;

		ret.append('{');

		for (int i = 0; i < bound; ++i) {
			ret.append(input[i]);
			ret.append(',');
		}

		ret.append(input[bound]);
		ret.append('}');

		return ret.toString();
	}

	public static String arrayToString(float[] input) {
		StringBuffer ret   = new StringBuffer();
		int          bound = input.length - 1;

		ret.append('{');

		for (int i = 0; i < bound; ++i) {
			ret.append(input[i]);
			ret.append(',');
		}

		ret.append(input[bound]);
		ret.append('}');

		return ret.toString();
	}

	public static List<Integer> sortKeysByValues(final Map<Integer, Double> input) {
		List<Integer> ret = new ArrayList<Integer>(input.keySet());

		Collections.sort(ret, new Comparator<Integer>() {
			public int compare(Integer left, Integer right) {
				return input.get(left).compareTo(input.get(right));
			}
		});

		return ret;
	}

	public static int[] sortArray(final double[] sortBy) {
		int[] toSort = new int[sortBy.length];

		for (int i = sortBy.length - 1; i >= 0; --i) {
			toSort[i] = i;
		}

		qsort(toSort, sortBy, 0, toSort.length - 1);

		return toSort;
	}

	private static void qsort(int[] toSort, final double[] sortBy, int left, int right) {
		if (right > left) {
			int pivotIndex = rand.nextInt(right - left + 1) + left;
			int newIndex   = qsortPartition(toSort, sortBy, right, left, pivotIndex);

			qsort(toSort, sortBy, left, newIndex - 1);
			qsort(toSort, sortBy, newIndex + 1, right);
		}
	}

	private static int qsortPartition(int[] toSort, final double[] sortBy, int right, int left, int pivotIndex) {
		int    tmp;
		double pivotValue = sortBy[toSort[pivotIndex]];
		int    newIndex   = left;

		/*
		 *  move pivot element at the end
		 */
		 tmp                = toSort[pivotIndex];
		toSort[pivotIndex] = toSort[right];
		toSort[right]      = tmp;

		for (int i = left; i < right; ++i) {
			double compare_i = sortBy[toSort[i]];

			if (compare_i <= pivotValue) {
				tmp              = toSort[i];
				toSort[i]        = toSort[newIndex];
				toSort[newIndex] = tmp;
				++newIndex;
			}
		}

		tmp              = toSort[newIndex];
		toSort[newIndex] = toSort[right];
		toSort[right]    = tmp;

		return newIndex;
	}

	public static Integer[] rankArray(final double[] input) {
		Integer[] ret = new Integer[input.length];

		for (int i = ret.length - 1; i >= 0; --i) {
			ret[i] = i;
		}

		java.util.Arrays.sort(ret, new Comparator<Integer>() {
			public int compare(Integer left, Integer right) {
				double leftDouble  = input[left];
				double rightDouble = input[right];

				if (leftDouble < rightDouble) {
					return -1;
				}

				if (leftDouble > rightDouble) {
					return 1;
				}

				return 0;
			}
		});

		return ret;
	}

	public static void add(double[] a, double[] b) {
		int index = Math.min(a.length, b.length);

		for (int i = 0; i < index; ++i) {
			a[i] += b[i];
		}
	}

	public static void divide(double[] a, double b) {
		for (int i = 0; i < a.length; ++i) {
			a[i] /= b;
		}
	}

	public static void divide(float[] a, float b) {
		for (int i = 0; i < a.length; ++i) {
			a[i] /= b;
		}
	}

	public static double median(double[] input) {
		if (input.length == 0) {
			return Double.NaN;
		}

		int mode = input.length % 2;

		/*
		 * get a copy of a, so we don't have to modify it
		 * during computation of the median
		 */
		 double temp[] = Arrays.copyOf(input, input.length);

		 /*
		  * sort b
		  */
		 Arrays.sort(temp);

		 if (mode == 1) {
			 return (temp[temp.length / 2]);
		 } else {
			 double t1 = temp[temp.length / 2 - 1];
			 double t2 = temp[temp.length / 2];

			 return (t1 + t2) / 2;
		 }
	}

	/**
	 * The median function without copying the input array. Note
	 * that this method changes the input array, more precisely it orders it.
	 * @param input the input array of double numbers
	 * @return the median of the input array
	 */
	public static double medianUnsecure(double[] input) {
		if (input.length == 0) {
			return Double.NaN;
		}

		int mode = input.length % 2;

		/*
		 * sort the input array
		 */
		Arrays.sort(input);

		if (mode == 1) {
			return (input[input.length / 2]);
		} else {
			double t1 = input[input.length / 2 - 1];
			double t2 = input[input.length / 2];

			return (t1 + t2) / 2;
		}
	}
}


