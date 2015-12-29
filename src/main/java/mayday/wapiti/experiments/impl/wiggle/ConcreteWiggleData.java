package mayday.wapiti.experiments.impl.wiggle;

import mayday.core.math.average.IAverage;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;

public class ConcreteWiggleData implements LocusExpressionData {

	protected IAverage method;
	protected AbstractWiggleData wig;
	
	public ConcreteWiggleData(AbstractWiggleData wig, IAverage method) {
		this.method= method;
		this.wig = wig;
	}
	
	@Override
	public void compact() {
		// ignore
	}

	@Override
	public double getExpression(AbstractGeneticCoordinate locus) {
		return wig.getExpression(locus, method);
	}

}
