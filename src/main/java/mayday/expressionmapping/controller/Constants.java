package mayday.expressionmapping.controller;

/**
 * The class contains constants used in the project to specify algorithms. 
 * Only constants used in serveral classes and thus constants that have to be synchronized are listed here.
 * @author seraphim
 */
public class Constants {

	/**
	 *Methods defining how to treat with negative values contained in the input data
	 */
	/**
	 * negative values are set to the 0
	 */
	public static final int SIMPLE = 0;
	
	/**
	 * 2^x transformation
	 */
	public static final int FOLDCHANGE = 1;
	
	/**
	 *the rank of the expression values is taken 
	 */
	public static final int RANK = 2;

	
	/**
	 * Methods defing how the data are clustered 
	 */
	/**
	 * the simple k-means algorithm
	 */
	public static final int KMEANS = 3;
	
	/**
	 *a weighted variant of the k-means algorithm 
	 */
	public static final int WKMEANS = 4;
	
	/**
	 *the Neural Gas cluster algorithm 
	 */
	public static final int NG = 5;

	/**
	 * further methods operating on the point list
	 */
	/**
	 *the computer of the attractor areas 
	 */
	public static final int ATTRAC = 6;

	/**
	 * a quicksort method for the pointlist
	 */
	public static final int QSORT = 7;

	/*
	 * the modes of combining expression values to form the groups
	 */
	/**
	 * combine expression values using their mean
	 */
	public static final int COMBINE_MEAN = 8;

	/**
	 * combine expression values using their median
	 */
	public static final int COMBINE_MEDIAN = 9;
}
