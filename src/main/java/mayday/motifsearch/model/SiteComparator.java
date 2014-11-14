package mayday.motifsearch.model;

import java.util.Comparator;

;
/**
 * This class represents a comparator for sites
 * 
 * @author Frederik Weber
 */

public class SiteComparator
implements Comparator<Site> {

    /* sorts by the sites significance */
    public static final byte SORT_BY_SIGNIFICANCE_VALUE = 0;

    private byte storedSortMechanism;

    /**
     * constructor of a comparator for a site
     * 
     * @param sortMechanism
     *                the sort Mechanism that is to be used to sort the
     *                sites. Mechanisms are:
     *                SiteComparator.SORT_BY_SIGNIFICANCE_VALUE 
     */
    public SiteComparator(byte sortMechanism) {
	this.storedSortMechanism = ((sortMechanism >= 0 && sortMechanism <= 0) ? sortMechanism
		: 0);
    }

    /**
     * compare two sites
     * 
     * @param s1
     *                a  site
     * @param s2
     *                an other site
     */
    public int compare(Site s1, Site s2) {
	switch (this.storedSortMechanism) {
	    case 0:
		return this.compareByScore(s1, s2);
	    default:
		throw new RuntimeException("Unexpected Sort Mechanism");
	}
    }

    /**
     * compare two sites according to its significance
     * 
     * @param s1
     *                a site
     * @param s2
     *                an other site
     */
    public int compareByScore(Site s1, Site s2) {
	double c = s1.getSignificanceValue() - s2.getSignificanceValue();
	return (c <= 0.0 ? (c == 0.0 ? 0 : -1) : 1);
    }
}
