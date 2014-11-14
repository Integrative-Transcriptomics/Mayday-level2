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

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbFunctionEvaluation.DhbMath;

/**
 * Continued fraction
 *
 * @author Didier H. Besset
 */
public abstract class ContinuedFraction extends IterativeProcess {
    /**
     * Best approximation of the fraction.
     */
    private double result;
    /**
     * Fraction's argument.
     */
    protected double x;
    /**
     * Fraction's accumulated numerator.
     */
    private double numerator;
    /**
     * Fraction's accumulated denominator.
     */
    private double denominator;
    /**
     * Fraction's next factors.
     */
    protected double[] factors = new double[2];

    /**
     * Compute the pair numerator/denominator for iteration n.
     * @param n int
     */
    protected abstract void computeFactorsAt(int n);

    /**
     * @return double
     */
    public double evaluateIteration() {
        computeFactorsAt(getIterations());
        denominator = 1 / limitedSmallValue(factors[0] * denominator
                                            + factors[1]);
        numerator = limitedSmallValue(factors[0] / numerator + factors[1]);
        double delta = numerator * denominator;
        result *= delta;
        return Math.abs(delta - 1);
    }

    /**
     * @return double
     */
    public double getResult() {
        return result;
    }

    public void initializeIterations() {
        numerator = limitedSmallValue(initialValue());
        denominator = 0;
        result = numerator;
        return;
    }

    /**
     * @return double
     */
    protected abstract double initialValue();

    /**
     * Protection against small factors.
     * @return double
     * @param r double
     */
    private double limitedSmallValue(double r) {
        return Math.abs(r) < DhbMath.smallNumber()
                ? DhbMath.smallNumber() : r;
    }

    /**
     * @param r double	the value of the series argument.
     */
    public void setArgument(double r) {
        x = r;
        return;
    }
}