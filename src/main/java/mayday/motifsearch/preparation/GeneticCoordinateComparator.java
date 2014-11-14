package mayday.motifsearch.preparation;

import java.util.Comparator;

/**
 * this comparator compares gene Locations according to its properties
 * the one on the + strand
 * 
 * @author Frederik Weber
 * 
 */
public class GeneticCoordinateComparator
implements Comparator<IGeneticCoordinate> {

    /* sort menchanisms name = mechanism */
    public static final byte PLUS_STRAND_AND_GENESTART_FIRST = 0;
    public static final byte FIRST_OCCURRENCE_FIRST = 1;

    private byte storedSortMechanism;

    /**
     * @param sortMechanism
     *                the sort mechanisms:
     *                SequenceComparator.PLUS_STRAND_AND_GENESTART_FIRST
     *               
     */
    public GeneticCoordinateComparator(byte sortMechanism) {
	this.storedSortMechanism = ((sortMechanism >= 0 && sortMechanism <= 1) ? sortMechanism
		: 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     *      @author Frederik Weber
     */
    public int compare(IGeneticCoordinate gl1, IGeneticCoordinate gl2) {
	switch (this.storedSortMechanism) {
	    case PLUS_STRAND_AND_GENESTART_FIRST:
		return this.compareByPLUS_STRAND_AND_GENESTART_FIRST(gl1, gl2);
	    case FIRST_OCCURRENCE_FIRST:
		return this.compareByFIRST_OCCURRENCE_FIRST(gl1, gl2);
	    default:
		throw new RuntimeException("Unexpected Sort Mechanism");
	}
    }

    /**
     * compare by PLUS_STRAND_AND_GENESTART_FIRST of a IGeneticCoordinate
     * 
     * @param gl1
     *                a IGeneticCoordinate
     * @param gl2
     *                an other IGeneticCoordinate
     * @return
     * {@link GeneticCoordinateComparator#compare(IGeneticCoordinate, IGeneticCoordinate)}
     */
    private int compareByPLUS_STRAND_AND_GENESTART_FIRST(IGeneticCoordinate gl1, IGeneticCoordinate gl2) {

	if (gl1.isPlusStrand() && !gl2.isPlusStrand()){ // + -
	    return 1;
	} else if (gl1.isPlusStrand() && gl2.isPlusStrand()){ // + +
	    long c = (gl1.getFrom() - gl2.getFrom());
	    return (c <= 0 ? (c == 0 ? 0 : -1) : 1); // signum
	}else if (!gl1.isPlusStrand() && gl2.isPlusStrand()){ // - +
	    return -1;
	}else if (!gl1.isPlusStrand() && !gl2.isPlusStrand()){ // - -
	    long c = (gl2.getTo() - gl1.getTo());
	    return (c <= 0 ? (c == 0 ? 0 : -1) : 1); 
	} else {
	    System.err.println("GeneticCoordianteComparator failed to Compare gene locations"
		    + gl1
		    + gl2
		    + " (maybe neither only + nor only - strand))");
	    return 0;
	}
    }

    /**
     * compare by FIRST_OCCURRENCE_FIRST of a IGeneticCoordinate
     * 
     * @param gl1
     *                a IGeneticCoordinate
     * @param gl2
     *                an other IGeneticCoordinate
     * @return
     * {@link GeneticCoordinateComparator#compare(IGeneticCoordinate, IGeneticCoordinate)}
     */
    private int compareByFIRST_OCCURRENCE_FIRST(IGeneticCoordinate gl1, IGeneticCoordinate gl2) {
	return (int) (gl1.getFrom() - gl2.getFrom());
    }


    public final void setStoredSortMechanism(byte sortMechanism) {
	this.storedSortMechanism = ((sortMechanism >= 0 && sortMechanism <= 0) ? sortMechanism
		: 0);
    }

}
