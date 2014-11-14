package mayday.wapiti.transformations.impl.fakereads;

import java.util.ArrayList;
import java.util.List;

import mayday.genetics.advanced.chromosome.AbstractLocusGeneticCoordinate;
import mayday.genetics.advanced.chromosome.LocusChromosomeObject;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.locusmap.LocusMap;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;

public class Array2ReadCountData implements LocusReadCountData {

	protected FeatureExpressionData input;
	protected Experiment e;
	protected LocusMap lm;
	protected int readCount;
	protected double reads_per_expression;
	protected double min_expression;
	
	public Array2ReadCountData(Experiment e, FeatureExpressionData input, LocusMap lm, int totalNoReads, double minExpression) {
		this.input = input;
		this.e=e;
		this.lm = lm;
		readCount = totalNoReads;
		min_expression=minExpression;
		
		// compute scaling factor
		double totalexpression = 0;
		
		for (String s : lm.keySet()) {
			Double d = input.getExpression(0, s);
			if (d!=null) 
				totalexpression += d-minExpression;
		}
		
		reads_per_expression = (double)readCount/totalexpression;
	}

	public void compact() {
		// ignore
	}

	@SuppressWarnings("unchecked")
	@Override
	public double getHitCount(AbstractGeneticCoordinate locus) {
		LocusChromosomeObject<String> alc = ((LocusChromosomeObject<String>)lm.asChromosomeSetContainer().getChromosome(locus.getChromosome()));
		
		List<LocusGeneticCoordinateObject<String>> lagc = alc.getOverlappingLoci(locus.getModel());
		
		double count = 0;
		
		for (LocusGeneticCoordinateObject<String> agc : lagc) {
			Double d = input.getExpression(0, agc.getObject());
			if (d!=null)
				count += d-min_expression; 
		}
		
		count *= reads_per_expression;
		count = Math.round(count);
		//TODO scaling function
		
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends AbstractLocusGeneticCoordinate> getReadsCovering(AbstractGeneticCoordinate locus) {
		
		LocusChromosomeObject<String> alc = ((LocusChromosomeObject<String>)lm.asChromosomeSetContainer().getChromosome(locus.getChromosome()));
		
		List<LocusGeneticCoordinateObject<String>> lagc = alc.getOverlappingLoci(locus.getModel());
		double tec=0;
		for (LocusGeneticCoordinateObject<String> agc : lagc) {
			Double d = input.getExpression(0, agc.getObject());
			if (d!=null && d>=min_expression)
				tec += d;   
		}
		
		tec*=reads_per_expression;
		tec=Math.round(tec);
		
		List<LocusGeneticCoordinateObject<String>> result = new ArrayList<LocusGeneticCoordinateObject<String>>((int)tec);
		
		for (LocusGeneticCoordinateObject<String> agc : lagc) {
			Double ex = input.getExpression(0, agc.getObject());			
			if (ex!=null) {
				ex-=min_expression;
				ex*=reads_per_expression;
				int lex=(int)Math.round(ex);
				for (int i=0; i<=lex; ++i)
					result.add(agc);
			}
		}

		return result;
	}

	
}	