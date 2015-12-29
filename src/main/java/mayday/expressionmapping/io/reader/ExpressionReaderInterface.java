package mayday.expressionmapping.io.reader;

import java.util.*;

import mayday.expressionmapping.model.geometry.DataPoint;

/**
 * @author Stephan Gade
 *
 */
public interface ExpressionReaderInterface {

	/**
	 * This Method read the Expressionvales
	 *
	 * @param inputReader
	 * @param labels
	 * @return
	 * @throws IOException if an IOError occurs (from readLine() method)
	 * @throws MalformedInputException if the inputstream doesn't match the
	 */
	//    public List<DataPoint> readExpressionValues (Reader inputReader, List<String> labels) throws IOException, MalformedInputException;
	//
	//    public List<DataPoint> readExpressionValues (List<String> labels) throws IOException, MalformedInputException;

	/**
	 * The main method for the expression values.
	 */
	public List<DataPoint> readExpressionValues();


}
