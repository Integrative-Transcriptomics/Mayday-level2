///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package mayday.expressionmapping.model.math;
//
//import mayday.expressionmapping.controller.Constants;
//import mayday.expressionmapping.model.geometry.DataPoint;
//
///**
// *
// * @author Stephan Gade
// */
//public class PearsonDistance implements DistanceMeasure{
//	
//	private SimilarityMeasure pearsCoeff = null;
//	
//	
//	public PearsonDistance()  {
//		
//		this.pearsCoeff = MathFactory.getSimilarityMeasure(Constants.PEARSON_SIMILARITY);
//		
//	}
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
//		double cor = this.pearsCoeff.computeSimilarity(a, b);
//		
//		return 1. - cor;
//
//
//	}
//
//	public String toString()  {
//		
//		return "Pearson Distance";
//		
//	}
//
//}
