package mayday.wapiti.transformations.impl.summarizeloci;

import java.util.List;

import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;

public class CoverageSummaryExperimentData implements LocusExpressionData {

	protected LocusReadCountData input;
	protected Experiment e;
	
	public CoverageSummaryExperimentData(Experiment e, LocusReadCountData input) {
		this.input = input;
		this.e=e;
	}

	public double getExpression(AbstractGeneticCoordinate locus) {
		
		List<? extends AbstractGeneticCoordinate> lslgc = input.getReadsCovering(locus);
		
		double coverage = 0;
		
		for (AbstractGeneticCoordinate slgc : lslgc) {
			long coveredBases = slgc.getOverlappingBaseCount(locus);
			coverage+=coveredBases;			
		}
		
		coverage/=(double)locus.length();
		
		return coverage;
	}

	public void compact() {
		// nothing to do
	}
	
}	