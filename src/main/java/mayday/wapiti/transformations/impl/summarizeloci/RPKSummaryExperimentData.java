package mayday.wapiti.transformations.impl.summarizeloci;

import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;

public class RPKSummaryExperimentData implements LocusExpressionData {

	protected LocusReadCountData input;
	protected Experiment e;
	
	public RPKSummaryExperimentData(Experiment e, LocusReadCountData input) {
		this.input = input;
		this.e=e;
	}

	public double getExpression(AbstractGeneticCoordinate locus) {
		
		double hits = input.getHitCount(locus);
		
		double featureLength = locus.getCoveredBases();
		
		// RPK is reads per kilobase
		double rpk = (1000d*hits) / featureLength;
		
		return rpk;
	}
	
	public void compact() {
		// ignore
	}

	
}	