/**
 * The Broad Institute
 * SOFTWARE COPYRIGHT NOTICE AGREEMENT
 * This software and its documentation are copyright 2004-2005 by the
 * Broad Institute/Massachusetts Institute of Technology.
 * All rights are reserved.
 * This software is supplied without any warranty or guaranteed support
 * whatsoever. Neither the Broad Institute nor MIT can be responsible for
 * its use, misuse, or functionality.
 */
package mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces;

/**
 * PointSeries is an interface used by many classes of the package numericalMethods.
 *
 * A PointSeries has the responsibility of handling mathematical
 * points in 2-dimensional space.
 * It is a BRIDGE to a vector containing the points.
 *
 * @author Didier H. Besset
 */
public interface PointSeries {

    /**
     * Returns the number of points in the series.
     */
    public int size();

    /**
     * Returns the x coordinate of the point at the given index.
     * @param index the index of the point.
     * @return x coordinate
     */
    public double xValueAt(int index);

    /**
     * Returns the y coordinate of the point at the given index.
     * @param index the index of the point.
     * @return y coordinate
     */
    public double yValueAt(int index);
}