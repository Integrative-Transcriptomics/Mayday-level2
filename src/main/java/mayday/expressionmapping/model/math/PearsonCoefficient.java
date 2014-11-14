//package mayday.expressionmapping.model.math;
//
//import mayday.expressionmapping.model.geometry.DataPoint;
//
///**
// * @author Stephan Gade
// *
// */
//public class PearsonCoefficient implements SimilarityMeasure {
//
//	public <T extends DataPoint> double computeSimilarity(T a, T b, boolean valueModus) {
//
//
//		if (a.getDimension() != b.getDimension()) {
//			throw new IllegalArgumentException("The Points a and b must have the same dimension. a has " + a.getDimension() + ", b has " + b.getDimension() + "!");
//		/* The flag valueModus indicates which array is used for the measuememt computation:
//		 * 0 (false)- the (expression) values array
//		 * 1 (true)- the coordinates array
//		 */
//		}
//		if (!valueModus) {
//			return computeSimilarity(a.getValues(), b.getValues());  /* valueModus is 0 (false) */
//
//		} else {
//			return computeSimilarity(a.getCoordinates(), b.getCoordinates());  /* valueModus is 1 (true) */
//
//		}
//	}
//
//	public double computeSimilarity(double[] a, double[] b) {
//
//		if (a.length != b.length) {
//			throw new IllegalArgumentException("The double fields a and b must have the same length. a has " + a.length + ", b has " + b.length + "!");
//		}
//		
//		double numerator = 0;
//		double denominator_firstTerm = 0;
//		double denominator_secondTerm = 0;
//		double denominator = 0;
//
//		double meanA = 0;
//		double meanB = 0;
//
//		/* compute the mean of both arrays
//		 */
//		for (int i = 0; i < a.length; ++i) {
//
//			meanA += a[i];
//			meanB += b[i];
//
//		}
//
//		meanA /= a.length;
//		meanB /= b.length;
//
//		/*calculate the numerator from Pearson Coeff.
//		 */
//		for (int i = 0; i < a.length; ++i) {
//			numerator += (a[i] - meanA) * (b[i] - meanB);
//
//		/*calculate the denominator from Pearson Coeff.
//		 */
//		}
//		for (int i = 0; i < a.length; ++i) {
//
//			denominator_firstTerm += (a[i] - meanA) * (a[i] - meanA);
//			denominator_secondTerm += (b[i] - meanB) * (b[i] - meanB);
//
//		}
//
//		denominator_firstTerm = Math.sqrt(denominator_firstTerm);
//		denominator_secondTerm = Math.sqrt(denominator_secondTerm);
//
//
//		denominator = denominator_firstTerm * denominator_secondTerm;
//
//		/* check if denominator is equal zero,
//		 * in case standard deviation == 0
//		 */
//		if (denominator < Double.MIN_VALUE) {
//			denominator = Double.MIN_VALUE;
//		/* calculcate and return the result
//		 */
//		}
//		double ret = (numerator / denominator);
//
//		return ret;
//
//	}
//}
//

