/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mayday.expressionmapping.io.reader;


import java.util.ArrayList;
import java.util.List;

import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.DataPointFactory;

/**
 *
 * @author Stephan Gade <stephan.gade@googlemail.com>
 *
 * Read the expression value from a given source and returns a list of DataPoints. If there are more expression
 * values for one probe than there are dimensions, the expression values are grouped according to a mapping and combination method.
 * This can be either be the mean or the median.
 */
public class ExpressionReader implements ExpressionReaderInterface {

	private DataSource source;
	private List<TIntArrayList> groupMappings;
	private int groupMode;


	public ExpressionReader(DataSource source, List<TIntArrayList> groupMappings, int groupMode) {

		this.source = source;

		this.groupMappings = groupMappings;

		this.groupMode = groupMode;

	}

	@Override
	public List<DataPoint> readExpressionValues() {

		List<DataPoint> points = new ArrayList<DataPoint>();

		/*
		 * Main Loop
		 * iterate over the input source using the next() function of the source
		 */

		/*
		 * the IDs of the points are starting with 0
		 */
		int pointID = 0;

		while (source.hasNext()) {

			/*
			 * get the raw data values of the source
			 */
			double[] rawData = source.next();

			/*
			 * create a new data point with the DataPointFactory out of the raw values
			 * using the mappings in the dataset
			 * and the group mode (either mean or median)
			 */

			points.add(DataPointFactory.getDataPoint(pointID, rawData, groupMappings, groupMode));

			/*
			 * increase the data point ID counter
			 */
			++pointID;

		}


		return points;
	}
//	private void processExpressionValues() {
//
//		int dim = this.data.groupMappings.size();
//
//		if(dim != source.getDim())
//			throw new IllegalStateException("Dimension of the data doesn't match the number of groups");
//
//
//		List<DataPoint> points = new ArrayList<DataPoint>();
//
//		/*
//		 * Main Loop
//		 * iterate over the input source using the next() function of the source
//		 */
//
//		int pointID = 0;
//
//		while(source.hasNext()) {
//
//			/*
//			 * get the raw data values of the source
//			 */
//			double[] rawData = source.next();
//
//			/*
//			 * create a new data point with the DataPointFactory out of the raw values
//			 * using the mappings in the dataset
//			 * and the group mode (either mean or median)
//			 */
//
//			points.add(DataPointFactory.getDataPoint(pointID, rawData, data.groupMappings, data.groupMode));
//
//			/*
//			 * increase the data point ID counter
//			 */
//			++pointID;
//
//		}
//
//
//	}
//
//
//	public DataSet readExpressionValues() {
//
//
//		processExpressionValues();
//
//		return this.data;
//
//	}
}
