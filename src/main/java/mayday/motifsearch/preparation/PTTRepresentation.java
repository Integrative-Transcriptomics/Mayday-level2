package mayday.motifsearch.preparation;

import java.util.concurrent.*;

/**
 * Representation of PPT (ProTein Table) file format
 * 
 * found at: http://doc.bioperl.org/bioperl-live/Bio/FeatureIO/ptt.html
 * The PTT file format is a table of protein features.
 *It is used mainly by NCBI who produce PTT files for
 *all their published genomes found in ftp://ftp.ncbi.nih.gov/genomes/.
 *It has the following format:
 *
 *    Line 1
 *   Description of sequence to which the features belong
 *eg. "Leptospira interrogans chromosome II, complete sequence - 0..358943"
 *    It is usually equivalent to the DEFINITION line of a Genbank file,
 *with the length of the sequence appended. It is unclear why "0" is
 *used as a starting range, it should be "1".
 *
 *    Line 2
 *    Number of feature lines in the table
 *eg. "367 proteins"
 *
 *   Line 3
 *    Column headers, tab separated
 *eg. "Location Strand Length PID Gene Synonym Code COG Product"
 * 
 * 
 * @author Frederik Weber
 */

public class PTTRepresentation {

    private String description; // Line 1 of PTT
    private int numberProteins; // Line 2 of PTT
    private String[] columnHeaders; // Line 3 of PTT
    private ConcurrentHashMap<String, GeneLocation> geneLocationHashMap;

    public PTTRepresentation() {
	super();
	this.geneLocationHashMap = new ConcurrentHashMap<String, GeneLocation>();
    }

    public void addGeneLocation(GeneLocation geneLocation) {
	this.geneLocationHashMap.put(geneLocation.getSynonym(), geneLocation);
    }

    public GeneLocation getGeneLocationBySynonym(String geneLocationSynonym) {
	return this.geneLocationHashMap.get(geneLocationSynonym);
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public int getNumberProteins() {
	return numberProteins;
    }

    public void setNumberProteins(int numberProteins) {
	this.numberProteins = numberProteins;
    }

    public String getColumnHeader(int column) {
	if (column < columnHeaders.length) {
	    return columnHeaders[column];
	}
	else {
	    return "fehler";
	}

    }

    public void setColumnHeaders(String[] columnHeaders) {
	this.columnHeaders = columnHeaders;
    }

    
    public ConcurrentHashMap<String, GeneLocation> getGeneLocationHashMap() {
        return geneLocationHashMap;
    }

}
