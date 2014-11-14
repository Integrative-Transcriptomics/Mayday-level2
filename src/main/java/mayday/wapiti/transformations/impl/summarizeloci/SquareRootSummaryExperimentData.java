package mayday.wapiti.transformations.impl.summarizeloci;

import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;

public class SquareRootSummaryExperimentData implements LocusExpressionData {

	protected LocusReadCountData input;
	protected Experiment e;
	protected double nof;
	
	public SquareRootSummaryExperimentData(Experiment e, LocusReadCountData input, long numberOfFeatures) {
		this.input = input;
		this.e=e;
		this.nof = numberOfFeatures;
	}

	public double getExpression(AbstractGeneticCoordinate locus) {
		
		double hits = input.getHitCount(locus);
		
		double sqnor = Math.sqrt(nof);
		double frac = Math.sqrt( hits/nof );
		double res = Math.asin(frac);
		res *= sqnor;
		
		return res;
	}
	
	public void compact() {
		// ignore
	}

	
}	