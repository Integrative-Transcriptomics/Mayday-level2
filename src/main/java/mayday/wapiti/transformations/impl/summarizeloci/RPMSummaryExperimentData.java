package mayday.wapiti.transformations.impl.summarizeloci;

import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;

public class RPMSummaryExperimentData implements LocusExpressionData {

	protected LocusReadCountData input;
	protected Experiment e;
	protected double millionHits;
	
	public RPMSummaryExperimentData(Experiment e, LocusReadCountData input, long numberOfFeatures) {
		this.input = input;
		this.e=e;
		millionHits = ((double)numberOfFeatures)/1000000d;
	}

	public double getExpression(AbstractGeneticCoordinate locus) {
		
		double hits = input.getHitCount(locus);		
	
		// RPM is reads per million mapped
		double rpm = hits / millionHits;
		
		return rpm;
	}
	
	public void compact() {
		// ignore
	}

	
}	