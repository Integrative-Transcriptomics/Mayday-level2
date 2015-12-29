package mayday.wapiti.experiments.generic.mapping;

import java.util.LinkedList;
import java.util.List;

import mayday.core.structures.CompactableStructure;
import mayday.genetics.advanced.LocusData;
import mayday.genetics.advanced.chromosome.LocusChromosome;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinate;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;

public class MappingData implements LocusReadCountData, LocusData, CompactableStructure {
	
	protected ChromosomeSetContainer _data;
	protected long count;
	
	public MappingData() {
		_data = new ChromosomeSetContainer(new LocusChromosome.Factory());
	}

	public double getHitCount(AbstractGeneticCoordinate locus) {
		return (double)getReadsCovering(locus).size();
	}
	
	public List<LocusGeneticCoordinate> getReadsCovering(AbstractGeneticCoordinate locus) {
		LocusChromosome slc = (LocusChromosome)_data.getChromosome(
				locus.getChromosome().getSpecies(), 
				locus.getChromosome().getId());
		List<LocusGeneticCoordinate> loci = slc.getOverlappingLoci(locus.getModel());
		return loci;
	}

	public ChromosomeSetContainer asChromosomeSetContainer() {
		return _data;
	}
	
	protected LocusChromosome speedup_slc;
	protected Chromosome speedup_c;
	
	public void addRead(AbstractGeneticCoordinate locus) {
		Chromosome c = locus.getChromosome();
		if (c!=speedup_c) {
			speedup_slc = (LocusChromosome)_data.getChromosome(c);
			speedup_c = c;
		}
		speedup_slc.addLocus(locus.getFrom(), locus.getTo(), locus.getStrand());
		++count;
	}
	
	public long getReadCount() {
		return count;
	}
	
	// String representation if this is used as LocusData
	public String toString() {
		return "Locus-Mapped reads ("+getReadCount()+" reads)";
	}

	@Override
	public void compact() {
		_data.compact();
	}

	@Override
	public String getCompactionInitializer() {
		return _data.getCompactionInitializer();
	}

	@Override
	public void setCompaction(LinkedList<String> compactionInitializer) {
		_data.setCompaction(compactionInitializer);		
	}
}
