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
package mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterpolation;

/**
 * Bulirsch-Stoer interpolation
 *
 * @author Didier H. Besset
 */
public class BulirschStoerInterpolator extends NevilleInterpolator {
    /**
     * Constructor method.
     * @param pts edu.mit.broad.genome.alg.ext.DhbInterfaces.PointSeries
     */
    public BulirschStoerInterpolator(mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.PointSeries pts) {
        super(pts);
    }

    /**
     * @param m int
     * @param n int
     * @param x double
     */
    protected void computeNextDifference(int m, int n, double x) {
        double ratio = (points.xValueAt(n) - x) * rightErrors[n]
                / (points.xValueAt(n + m + 1) - x);
        double diff = (leftErrors[n + 1] - rightErrors[n])
                / (ratio - leftErrors[n + 1]);
        if (Double.isNaN(diff)) {
            diff = 0;
        }
        rightErrors[n] = leftErrors[n + 1] * diff;
        leftErrors[n] = ratio * diff;
    }
}