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
 * InifiniteSeries
 *
 * @author Didier H. Besset
 */
public abstract class InifiniteSeries extends IterativeProcess {
    /**
     * Best approximation of the sum.
     */
    private double result;
    /**
     * Series argument.
     */
    protected double x;
    /**
     * Value of the last term.
     */
    protected double lastTerm;

    /**
     * Computes the n-th term of the series and stores it in lastTerm.
     * @param n int
     */
    protected abstract void computeTermAt(int n);

    public double evaluateIteration() {
        computeTermAt(getIterations());
        result += lastTerm;
        return relativePrecision(Math.abs(lastTerm), Math.abs(result));
    }

    /**
     * @return double
     */
    public double getResult() {
        return result;
    }

    /**
     * Set the initial value for the sum.
     */
    public void initializeIterations() {
        result = initialValue();
    }

    /**
     * @return double		the 0-th term of the series
     */
    protected abstract double initialValue();

    /**
     * @param r double	the value of the series argument.
     */
    public void setArgument(double r) {
        x = r;
        return;
    }
}