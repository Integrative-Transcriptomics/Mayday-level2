package mayday.motifsearch.model;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * this comparator compares sequences according to its properties
 * 
 * @author Frederik Weber
 * 
 */
public class SequenceComparator
implements Comparator<Sequence> {

    /* sort mechanisms name = mechanism */
    public static final byte SORT_BY_NUMBER_OF_SITES = 0;

    public static final byte SORT_BY_NUMBER_OF_DIFFERENT_MOTIF_SITES = 1;

    public static final byte SORT_BY_LENGTH_OF_SEQUENCE = 2;

    public static final byte SORT_BY_OVERALL_SIGNIFICANCE_OF_SEQUENCE = 3;


    private byte storedSortMechanism;

    /**
     * @param sortMechanism
     *                the sort mechanisms:
     *                SequenceComparator.SORT_BY_NUMBER_OF_SITES
     *                SequenceComparator.SORT_BY_NUMBER_OF_DIFFERENT_MOTIF_SITES
     *                SequenceComparator.SORT_BY_LENGTH_OF_SEQUENCE
     *                SequenceComparator.SORT_BY_OVERALL_SIGNIFICANCE_OF_SEQUENCE 
     */
    public SequenceComparator(byte sortMechanism) {
	this.storedSortMechanism = ((sortMechanism >= 0 && sortMechanism <= 2) ? sortMechanism
		: 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     *      @author Frederik Weber
     */
    public int compare(Sequence s1, Sequence s2) {
	switch (this.storedSortMechanism) {
	    case 0:
		return this.compareByNumberOfBindigs(s1, s2);
	    case 1:
		return this.compareByNumberOfDifferentMotifs(s1, s2);
	    case 2:
		return this.compareBylength(s1, s2);
	    case 3:
		return this.compareByOverallSignificance(s1, s2);
	    default:
		throw new RuntimeException("Unexpected Sort Mechanism");
	}
    }

    /**
     * compare by number of sites of a sequence
     * 
     * @param s1
     *                a sequence
     * @param s2
     *                an other sequence
     * @return
     * {@link mayday.motifsearch.model.SequenceComparator#compare(Sequence, Sequence)}
     */
    private int compareByNumberOfBindigs(Sequence s1, Sequence s2) {
	return s1.getSites().size() - s2.getSites().size();
    }

    /**
     * compare by number different motif sites on sequence
     * 
     * @param s1
     *                a sequence
     * @param s2
     *                an other sequence
     * @return
     *  {@link mayday.motifsearch.model.SequenceComparator#compare(Sequence, Sequence)}
     */
    private int compareByNumberOfDifferentMotifs(Sequence s1, Sequence s2) {
	ArrayList<Motif> mList1 = new ArrayList<Motif>();
	ArrayList<Motif> mfList2 = new ArrayList<Motif>();
	for (Site s : s1.getSites()) {
	    Motif m = s.getMotif();
	    if (!mList1.contains(m)) {
		mList1.add(m);
	    }
	}

	for (Site s : s2.getSites()) {
	    Motif m = s.getMotif();
	    if (!mfList2.contains(m)) {
		mfList2.add(m);
	    }
	}
	return mList1.size() - mfList2.size();
    }

    /**
     * compare by overall significance motif sites on sequence
     * 
     * @param s1
     *                a sequence
     * @param s2
     *                an other sequence
     * @return
     *  {@link mayday.motifsearch.model.SequenceComparator#compare(Sequence, Sequence)}
     */
    private int compareByOverallSignificance(Sequence s1, Sequence s2) {
	double val1 = (s1.getSites().isEmpty()?Double.MIN_VALUE:0);
	double val2 = (s2.getSites().isEmpty()?Double.MIN_VALUE:0);
	for (Site s : s1.getSites()) {
	    val1 += s.getSignificanceValue();
	}
	for (Site s : s2.getSites()) {
	    val2 += s.getSignificanceValue();
	}
	double c = (1/val1) - (1/val2);
	return (c <= 0.0 ? (c == 0.0 ? 0 : -1) : 1);

    }

    /**
     * compare by length of the sequence
     * 
     * @param s1
     *                a sequence
     * @param s2
     *                an other sequence
     * @return
     *  {@link mayday.motifsearch.model.SequenceComparator#compare(Sequence, Sequence)}
     */
    private int compareBylength(Sequence s1, Sequence s2) {
	return s1.getLength() - s2.getLength();
    }



    public final void setStoredSortMechanism(byte sortMechanism) {
	this.storedSortMechanism = ((sortMechanism >= 0 && sortMechanism <= 2) ? sortMechanism
		: 0);
    }

}
