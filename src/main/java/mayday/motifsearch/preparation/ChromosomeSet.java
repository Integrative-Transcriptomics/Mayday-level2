package mayday.motifsearch.preparation;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChromosomeSet extends ArrayList<Chromosome>{
    
    private static final long serialVersionUID = -7869922493254311885L;
    
    private ConcurrentHashMap<String, GeneLocation> geneLocationHashMap = new ConcurrentHashMap<String, GeneLocation>();
    
    
    
    public ConcurrentHashMap<String, GeneLocation> getGeneLocationHashMap() {
        return geneLocationHashMap;
    }

    /**
     * adds first header and sequence of FASTA file representation
     */
    public void addFromFastaRep(FastaRepresentation fastaRep) {
	    this.add(new Chromosome(
		    fastaRep.getHeader(0), 
		    fastaRep.getFASTAFile()));
    }
    
    public void putAllGeneLocationsFromHashMap(Map<? extends String,? extends GeneLocation> m) {
        this.geneLocationHashMap.putAll(m);
    }
    
    public GeneLocation getGeneLocationBySynonym(String geneLocationSynonym) {
	return this.geneLocationHashMap.get(geneLocationSynonym);
    }
    
    public void sortChomosomesGeneLocations(byte method) {
	for (Chromosome chromosome: this) {
	    chromosome.sortTempNeededGeneLocations(method);    
	}
    }
    
    
}
