package mayday.wapiti.transformations.impl.unlog;

import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;

public class UnloggedLocusExpressionExperimentData implements LocusExpressionData {

	protected LocusExpressionData input;
	protected double base;
	
	public UnloggedLocusExpressionExperimentData(LocusExpressionData input, double base) {
		this.input = input;
		this.base = base;
	}


	
	public double getExpression(AbstractGeneticCoordinate locus) {
		Double in = input.getExpression(locus);
		if (in==null)
			return 0;
		double i = Math.pow(base,in);
		return i;

	}

	public void compact() {
		input.compact();
	}

	
}	