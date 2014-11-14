package mayday.wapiti.experiments.generic.locusreadcount;

import java.util.List;

import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.base.ExperimentData;

/**
 * Expression data where a locus can be "hit" by a number of reads (even fractional reads,
 *  e.g. when quality scores are taken into account)
 */

public interface LocusReadCountData extends ExperimentData {

	public double getHitCount(AbstractGeneticCoordinate locus);
	
	public List<? extends AbstractGeneticCoordinate> getReadsCovering(AbstractGeneticCoordinate locus);
 	
}
