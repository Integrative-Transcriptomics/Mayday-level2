package mayday.wapiti.transformations.impl.summarizeloci;

import java.util.List;

import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;

public class DCPMSummaryExperimentData implements LocusExpressionData {

	protected LocusReadCountData input;
	protected Experiment e;
	protected double millionHits;
	
	public DCPMSummaryExperimentData(Experiment e, LocusReadCountData input, long numberOfFeatures) {
		this.input = input;
		this.e=e;
		millionHits = ((double)numberOfFeatures)/1000000d;
	}

	public double getExpression(AbstractGeneticCoordinate locus) {
		
		List<? extends AbstractGeneticCoordinate> lslgc = input.getReadsCovering(locus);
		
		double coverage = 0;
		
		for (AbstractGeneticCoordinate slgc : lslgc) {
			long coveredBases = slgc.getOverlappingBaseCount(locus);
			coverage+=coveredBases;			
		}
		
		coverage/=(double)locus.getCoveredBases();
		coverage/=millionHits;
		
		return coverage;
	}
	
	public void compact() {
		// ignore
	}

	
}	