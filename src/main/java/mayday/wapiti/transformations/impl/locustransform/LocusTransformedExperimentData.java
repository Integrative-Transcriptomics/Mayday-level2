package mayday.wapiti.transformations.impl.locustransform;

import mayday.genetics.advanced.ChromosomeSetIterator;
import mayday.genetics.advanced.LocusData;
import mayday.genetics.advanced.LocusTransformer;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;

public class LocusTransformedExperimentData implements LocusData {

	protected LocusData input;
	protected LocusTransformer transform;
	
	public LocusTransformedExperimentData(LocusData input, LocusTransformer transform) {
		this.input = input;
		this.transform = transform;
	}

	public ChromosomeSetContainer asChromosomeSetContainer() {
		// expensive operation only once
		ChromosomeSetContainer base = input.asChromosomeSetContainer();
		
		ChromosomeSetContainer csc = new ChromosomeSetContainer( base );
		
		for (AbstractGeneticCoordinate locus : new ChromosomeSetIterator(base) ) {
			transform.addTransformedCoordinate(csc, locus);
		}
		return csc;
	}
	
}	