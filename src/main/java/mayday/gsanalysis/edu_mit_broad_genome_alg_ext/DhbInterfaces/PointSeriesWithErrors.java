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
 * This is a point series where each point has an error in the y direction.
 *
 * @author Didier H. Besset
 */
public interface PointSeriesWithErrors extends PointSeries {
    /**
     * @return double	weight of the point
     * @param n int
     */
    double weightAt(int n);
}