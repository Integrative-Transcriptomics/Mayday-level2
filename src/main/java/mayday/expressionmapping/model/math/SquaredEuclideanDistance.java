//package mayday.expressionmapping.model.math;
//
//import mayday.expressionmapping.model.geometry.DataPoint;
//
///**
// *
// * @author Stephan Gade
// */
//public class SquaredEuclideanDistance implements DistanceMeasure{
//	
//	
//	public <T extends DataPoint> double computeDistance(T a, T b, boolean valueModus) {
//
//		if (a.getDimension() != b.getDimension())
//			throw new IllegalArgumentException
//			("The Points a and b must have the same dimension. a has "+a.getDimension()+", b has "+b.getDimension()+"!");
//
//		if (!valueModus)
//			return computeDistance(a.getValues(), b.getValues());  /* valueModus is 0 (false) */
//
//		else
//			return computeDistance(a.getCoordinates(), b.getCoordinates());  /* valueModus is 1 (true) */
//
//	}
//
//	public double computeDistance(double[] a, double[] b)  {
//
//		if (a.length != b.length)
//			throw new IllegalArgumentException("The double fields a and b must have the same length. a has "+a.length+", b has "+b.length+"!");
//
//		double res = 0;
//
//		for (int i = 0; i < a.length; ++i) 
//			res += (a[i] - b[i]) * (a[i] - b[i]);
//
//		return res;
//
//
//	}
//	
//	public String toString()  {
//		
//		return "Squared Euclidean Distance";
//		
//	}
//	
//}
