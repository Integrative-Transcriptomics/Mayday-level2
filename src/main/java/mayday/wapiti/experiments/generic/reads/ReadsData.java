package mayday.wapiti.experiments.generic.reads;

import java.util.List;

import mayday.genetics.advanced.LocusData;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateLong;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.transkriptorium.data.MappingStore;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;

public class ReadsData implements LocusReadCountData, LocusData {
	
	protected MappingStore _data;
	
	public ReadsData() {
		_data = new MappingStore();
	}

	public double getHitCount(AbstractGeneticCoordinate locus) {
		return (double)getReadsCovering(locus).size();
	}
	
	public List<LocusGeneticCoordinateLong> getReadsCovering(AbstractGeneticCoordinate locus) {		
		return _data.getCSC().getOverlappingLoci(locus.getChromosome(), locus.getModel());
	}

	public ChromosomeSetContainer asChromosomeSetContainer() {
		return _data.getCSC();
	}
	
	
	/** add a mapping position for a read
	 * 
	 * @param readID the identifier of the read (add "/1" and "/2" to indicate mate pairs)
	 * @param mappedPosition the coordinates of the mapping
	 * @param quality the quality of the mapping
	 * @param startInRead the start of the alignment in the read
 	 * @return the id of the added read
	 */
	public void addRead(String readID, AbstractGeneticCoordinate mappedPosition, double quality, int startInRead) {
		_data.addMappedRead(readID, mappedPosition, quality, startInRead);
	}
	
	public long getReadCount() {
		return _data.getTotalMappingCount();
	}
	
	// String representation if this is used as LocusData
	public String toString() {
		return "Locus-Mapped reads ("+getReadCount()+" reads)";
	}
	
	public MappingStore getFullData() {
		return _data;
	}

}
