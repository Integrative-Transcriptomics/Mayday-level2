package mayday.wapiti.transformations.impl.summarizeloci;

import java.util.List;

import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;

public class NaiveReadCountSummaryExperimentData implements LocusExpressionData {

	protected LocusReadCountData input;
	protected Experiment e;
	protected boolean fullOverlap;
	
	public NaiveReadCountSummaryExperimentData(Experiment e, LocusReadCountData input, boolean requireFullOverlap) {
		this.input = input;
		this.e=e;
		fullOverlap = requireFullOverlap;
	}

	public double getExpression(AbstractGeneticCoordinate locus) {
		double hits;
		if (fullOverlap) {
			hits=0;
			long len = locus.getCoveredBases();
			List<? extends AbstractGeneticCoordinate> lagc = input.getReadsCovering(locus);
			for (AbstractGeneticCoordinate agc : lagc)
				if (locus.getOverlappingBaseCount(agc)==len) 
					++hits;
		} else {
			hits = input.getHitCount(locus);
		}
		
		return hits;
	}

	public void compact() {
		// ignore
	}
	
}	