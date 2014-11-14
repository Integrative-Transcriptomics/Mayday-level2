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
package mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbIterations;

/**
 * Series for the incompleteGamma function
 *
 * @author Didier H. Besset
 */
public class IncompleteGammaFunctionSeries extends InifiniteSeries {
    /**
     * Series parameter.
     */
    private double alpha;
    /**
     * Auxiliary sum.
     */
    private double sum;

    /**
     * Constructor method
     * @param a double	series parameter
     */
    public IncompleteGammaFunctionSeries(double a) {
        alpha = a;
    }

    /**
     * Computes the n-th term of the series and stores it in lastTerm.
     * @param n int
     */
    protected void computeTermAt(int n) {
        sum += 1;
        lastTerm *= x / sum;
        return;
    }

    /**
     * initializes the series and return the 0-th term.
     */
    protected double initialValue() {
        lastTerm = 1 / alpha;
        sum = alpha;
        return lastTerm;
    }
}