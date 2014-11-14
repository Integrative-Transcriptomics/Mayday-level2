//package mayday.expressionmapping.model.math;
//
//import mayday.expressionmapping.controller.Constants;
//
///**
// * @author Stephan Gade
// *
// */
//public class MathFactory {
//
//        public static SimilarityMeasure getSimilarityMeasure(int type) {
//
//	switch (type)  {
//
//	case Constants.PEARSON_SIMILARITY:
//	    return new PearsonCoefficient();
//
//	default:  {
//	    System.err.println("Cannot determine the type of similarity you want. Returning the Pearson.");
//	    return new PearsonCoefficient();
//
//	}
//
//	}
//
//
//    }
//    
//    public static DistanceMeasure getDistanceMeasure(int type) {
//	
//	switch (type)  {
//
//	case Constants.EUCLIDEAN:
//	    return new EuclideanDistance();
//	    
//		case Constants.SQUARED_EUCLIDEAN:
//			return new SquaredEuclideanDistance();
//			
//		case Constants.MANHATTAN:
//			return new ManhattanDistance();
//			
//		case Constants.PEARSON_DIST:
//			return new PearsonDistance();
//
//	default:  {
//	    System.err.println("Cannot determine the type of similarity you want. Returning the Euclidean.");
//	    return new EuclideanDistance();
//
//	}
//
//	}
//
//	
//	
//    }
//
//
//}
