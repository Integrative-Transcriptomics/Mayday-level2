package mayday.wapiti.transformations.impl.summarizeloci;

import java.util.List;

import mayday.genetics.advanced.chromosome.LocusChromosome;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;

public class GeometricMeanSummaryExperimentData implements LocusExpressionData {

	protected LocusReadCountData input;
	protected Experiment e;
	
	private double[][] cov;
	
	public GeometricMeanSummaryExperimentData(Experiment e, LocusReadCountData input, long numberOfFeatures) {
		this.input = input;
		this.e=e;
	}

	public double getExpression(AbstractGeneticCoordinate locus) {
		
		List<? extends AbstractGeneticCoordinate> lslgc = input.getReadsCovering(locus);
		double coverage = 0;
		
		if (lslgc.size()>0) {
			
			LocusChromosome chrome = new LocusChromosome(locus.getChromosome().getSpecies(), locus.getChromosome().getId(), -1);
			for (AbstractGeneticCoordinate slgc : lslgc) {
				chrome.addLocus(slgc.getModel());
			}
			long numberbases = locus.getCoveredBases();
			
			double log_coverage = 0;
			
			for (GBAtom atom : locus.getModel().getCoordinateAtoms()) {
				cov = chrome.computeCoverageShort(atom.from, atom.to, cov);
				double[] strandCov = (atom.strand==Strand.PLUS)?cov[0]:cov[1];
				for (int i=0; i!=strandCov.length; ++i) {
					log_coverage += Math.log(strandCov[i]);
				}
			}
			
			log_coverage /= numberbases;
			coverage = Math.pow(2, log_coverage);
		}
		
		return coverage;
	}
	
	public void compact() {
		cov = null;
	}

	
}	