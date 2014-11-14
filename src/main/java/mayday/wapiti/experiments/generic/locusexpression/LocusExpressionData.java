package mayday.wapiti.experiments.generic.locusexpression;

import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.base.ExperimentData;

/**
 * Expression data that can be accessed via genetic coordinates 
 */


public interface LocusExpressionData extends ExperimentData {

	/** returns NaN if the locus is not covered by the data */
	public double getExpression(AbstractGeneticCoordinate locus);
	
	/** removes cached data to conserve memory */
	public void compact(); 
	
	
}
