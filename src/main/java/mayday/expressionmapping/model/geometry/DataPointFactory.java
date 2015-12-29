package mayday.expressionmapping.model.geometry;

import java.util.List;
import mayday.expressionmapping.controller.Constants;
import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.utils.Array;

/**
 *
 * @author Stephan Gade <stephan.gade@googlemail.com>
 */
public class DataPointFactory {
	/**
	 * 
	 * @param ID the ID of the data point, every data point must have a unique ID
	 * @param values the expression values 
	 * @param mappings
	 * @param groupingMode
	 * @return data point
	 */
	public static DataPoint getDataPoint(int ID, double[] values, List<TIntArrayList> mappings, int groupingMode) {
		/*
		 * create the expression values of the data point
		 * the length of the expression value array and therewith the dimension of the data point
		 * is determined by the length of the mappings array
		 * indicating which of the original values is combined
		 */
		double[] exprValues = new double[mappings.size()];

		for (int i = 0; i < exprValues.length; ++i) {
			/*
			 * get the values dscribed in the current mapping entry
			 */
			double[] tempValues = new double[mappings.get(i).size()];

			for (int j = 0; j < tempValues.length; ++j) {	
				tempValues[j] = values[mappings.get(i).get(j)];	
			}

			/*
			 * now these temporary values are combined using either the mean or the median
			 * depending of the grouping mode
			 */
			switch (groupingMode) {
				
				case Constants.COMBINE_MEAN:
					exprValues[i] = Array.mean(tempValues);
					break;

				case Constants.COMBINE_MEDIAN:
					exprValues[i] = Array.medianUnsecure(tempValues);
					break;

				default:
					 System.err.println("No valid combining mode given, taking the median.");
					 exprValues[i] = Array.medianUnsecure(tempValues);
					 break;
			}
		}
		return getDataPoint(ID, exprValues);
	}

	/**
	 * @param ID
	 * @param values
	 * @return data point
	 */
	public static DataPoint getDataPoint(int ID, double[] values) {
		switch (values.length) {
			case 2:
				return new DataPoint2D(ID, values);
			case 3: 
				return new DataPoint3D(ID, values);
			case 4: 
				return new DataPoint4D(ID, values);
			default:
				System.err.println("The array with expression values has a wrong length. Returning null.");
				return null;
		}
	}
}
