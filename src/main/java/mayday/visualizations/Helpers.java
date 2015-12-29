package mayday.visualizations;

import java.util.ArrayList;
import java.util.List;
/**
 * A class with helper functions.
 * @author Alicia Owen
 *
 */
public class Helpers {


	/**
	 * From a list of values, computes all values that are greater/greater equal than a given threshold
	 * @param values - the list of values
	 * @param threshold - the threshold
	 * @param greaterEqual - TRUE if only values greater than threshold are desired. FALSE if values greater equal the threshold are required.
	 * @return the list of values >/>= threshold
	 */
	public static List<Double> valuesGreaterThan(List<Double> values, double threshold, boolean greaterEqual){

		List<Double> valuesGreaterThan = new ArrayList<Double>();

		for(double val : values){
			if(greaterEqual){
				if(val > threshold){
					valuesGreaterThan.add(val);
				}
			}else{
				if(val >= threshold){
					valuesGreaterThan.add(val);
				}
			}

		}
		return valuesGreaterThan;
	}

	/**
	 * From a list of values, computes all values that are smaller/smaller equal than a given threshold
	 * @param values - the list of values
	 * @param threshold - the threshold
	 * @param smallerEqual - TRUE if only values smaller than threshold are desired. FALSE if values smaller equal the threshold are required.
	 * @return the list of values </<= threshold
	 */
	public static List<Double> valuesSmallerThan(List<Double> values, double threshold, boolean smallerEqual){

		List<Double> valuesSmallerThan = new ArrayList<Double>();

		for(double val : values){
			if(smallerEqual){
				if(val < threshold){
					valuesSmallerThan.add(val);
				}
			}else{
				if(val <= threshold){
					valuesSmallerThan.add(val);
				}
			}

		}
		return valuesSmallerThan;
	}

	/**
	 * Finds the smallest value in a list of values.
	 * @param values - the list of values in which minimum should be found
	 * @return	the minimum value
	 */
	public static  double findMin(List<Double> values){
		double min = Double.POSITIVE_INFINITY;

		for(double val : values){
			if(val < min){
				min = val;
			}
		}
		return min;
	}

	/**
	 * Finds the largest value in a list of values.
	 * @param values - the list of values in which maximum should be found
	 * @return	the maximum value
	 */
	public static double findMax(List<Double> values){
		double max = Double.NEGATIVE_INFINITY;

		for(double val : values){
			if(val > max){
				max = val;
			}
		}
		return max;
	}
}
