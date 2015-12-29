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
 * Continued fraction for the incompleteGamma function
 *
 * @author Didier H. Besset
 */
public class IncompleteGammaFunctionFraction extends ContinuedFraction {
    /**
     * Series parameter.
     */
    private double alpha;
    /**
     * Auxiliary sum.
     */
    private double sum;

    /**
     * Constructor method.
     * @param a double
     */
    public IncompleteGammaFunctionFraction(double a) {
        alpha = a;
    }

    /**
     * Compute the pair numerator/denominator for iteration n.
     * @param n int
     */
    protected void computeFactorsAt(int n) {
        sum += 2;
        factors[0] = (alpha - n) * n;
        factors[1] = sum;
        return;
    }

    protected double initialValue() {
        sum = x - alpha + 1;
        return sum;
    }
}