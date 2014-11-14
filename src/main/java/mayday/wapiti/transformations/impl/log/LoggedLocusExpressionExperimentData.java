package mayday.wapiti.transformations.impl.log;

import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;

public class LoggedLocusExpressionExperimentData implements LocusExpressionData {

	protected LocusExpressionData input;
	protected double baseLog;
	
	public LoggedLocusExpressionExperimentData(LocusExpressionData input, double logbase) {
		this.input = input;
		this.baseLog = 1.0/Math.log(logbase);
	}


	
	public double getExpression(AbstractGeneticCoordinate locus) {
		Double in = input.getExpression(locus);
		double i = Math.log(in)*baseLog;
		if (Double.isInfinite(i))
			i = Double.NaN;
		return i;

	}

	public void compact() {
		input.compact();
	}

	
}	