package mayday.wapiti.transformations.impl.summarizeloci;

import java.util.LinkedList;
import java.util.List;

import mayday.core.math.average.IAverage;
import mayday.core.settings.typed.AveragingSetting;
import mayday.genetics.advanced.chromosome.AbstractLocusChromosome;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;

public class FeatureSummaryExperimentData implements LocusExpressionData {

	protected FeatureExpressionData input;
	protected IAverage averager;
	protected Experiment e;
	protected ChromosomeSetContainer cached_csc;
	
	public FeatureSummaryExperimentData(Experiment e, FeatureExpressionData input, AveragingSetting setting) {
		this.input = input;
		this.averager = setting.getSummaryFunction();
		this.e=e;
	}

	@SuppressWarnings("unchecked")
	public double getExpression(AbstractGeneticCoordinate locus) {
		if (cached_csc==null)
			cached_csc = e.getLocusData().asChromosomeSetContainer();
		
		LinkedList<Double> values = new LinkedList<Double>();
		AbstractLocusChromosome alc = (AbstractLocusChromosome)cached_csc.getChromosome(
				locus.getChromosome().getSpecies(), 
				locus.getChromosome().getId());
		
		List<AbstractGeneticCoordinate> lagc = alc.getOverlappingLoci(locus.getFrom(), locus.getTo(), locus.getStrand());
		for (AbstractGeneticCoordinate agc : lagc) {
			if (agc instanceof LocusGeneticCoordinateObject) {
				LocusGeneticCoordinateObject<String> olgc = (LocusGeneticCoordinateObject<String>)agc;
				String featureName = olgc.getObject();
				values.add(input.getExpression(0,featureName));
			}
		}
		
		Double res;
		if (values.size()==1)
			res = values.get(0);
		else
			res = averager.getAverage(values, true);
		if (res==null)
			res = Double.NaN;
		return res;
	}

	public void compact() {
		cached_csc = null;
	}
	
}	