package mayday.wapiti.transformations.impl.summarizeloci;

import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;

public class RPKMSummaryExperimentData implements LocusExpressionData {

	protected LocusReadCountData input;
	protected Experiment e;
	protected double millionHits;
	
	public RPKMSummaryExperimentData(Experiment e, LocusReadCountData input, long numberOfFeatures) {
		this.input = input;
		this.e=e;
		millionHits = ((double)numberOfFeatures)/1000000d;
	}

	public double getExpression(AbstractGeneticCoordinate locus) {
		
		double hits = input.getHitCount(locus);
		
		double featureLength = locus.getCoveredBases();
		
		// RPKM is reads per million per kilobase
		double rpk = (1000d*hits) / featureLength;
		double rpkm = rpk / millionHits;
		
		return rpkm;
	}
	
	public void compact() {
		// ignore
	}

	
}	