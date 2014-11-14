package mayday.wapiti.transformations.impl.readexpression;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;

public class PseudoCountAddedExperimentData implements LocusReadCountData {

	protected LocusReadCountData input;
	protected int pseudo;
	
	public PseudoCountAddedExperimentData(LocusReadCountData input, int pseudo) {
		this.input = input;
		this.pseudo=pseudo;
	}

	@Override
	public double getHitCount(AbstractGeneticCoordinate locus) {
		return input.getHitCount(locus)+pseudo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends AbstractGeneticCoordinate> getReadsCovering(AbstractGeneticCoordinate locus) {		
		List<? extends AbstractGeneticCoordinate> in = input.getReadsCovering(locus);
		List<AbstractGeneticCoordinate> out;
		if ((in instanceof LinkedList) || (in instanceof ArrayList)) {// not Unmodifiable, good!
			out = (List<AbstractGeneticCoordinate>)in;		
		} else {
			out = new ArrayList<AbstractGeneticCoordinate>(in.size()+pseudo);
			out.addAll(in);
		}
		for (int i=0; i!=pseudo; ++i)
			out.add(locus);
		return out;
	}

	
}	