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
package mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbStatistics;

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbScientificCurves.Histogram;

/**
 * Uniform distribution over a given interval.
 *
 * @author Didier H. Besset
 */
public final class UniformDistribution
        extends ProbabilityDensityFunction {
    /**
     * Low limit.
     */
    private double a;
    /**
     * High limit.
     */
    private double b;

    /**
     * Constructor method.
     * @param low double	low limit
     * @param high double	high limit
     * @exception java.lang.IllegalArgumentException
     *										if the limits are inverted.
     */
    public UniformDistribution(double low, double high)
            throws IllegalArgumentException {
        if (low >= high)
            throw new IllegalArgumentException(
                    "Limits of distribution are equal or reversed");
        a = low;
        b = high;
    }

    /**
     * Create an instance of the receiver with parameters estimated from
     * the given histogram using best guesses. This method can be used to
     * find the initial values for a fit.
     * @param h edu.mit.broad.genome.alg.ext.DhbScientificCurves.Histogram
     */
    public UniformDistribution(Histogram h) {
        b = h.standardDeviation() * 1.73205080756888; // sqrt(12)/2
        double c = h.average();
        a = c - b;
        b += c;
    }

    /**
     * @return double average of the distribution.
     */
    public double average() {
        return (a + b) * 0.5;
    }

    /**
     * Returns the probability of finding a random variable smaller
     * than or equal to x.
     * @return integral of the probability density function from a to x.
     * @param x double upper limit of integral.
     */
    public double distributionValue(double x) {
        if (x < a)
            return 0;
        else if (x < b)
            return (x - a) / (b - a);
        else
            return 1;
    }

    /**
     * @return double kurtosis of the distribution.
     */
    public double kurtosis() {
        return -1.2;
    }

    /**
     * @return java.lang.String	 name of the distribution
     */
    public String name() {
        return "Uniform distribution";
    }

    /**
     * @return double[] an array containing the parameters of
     *												the distribution.
     */
    public double[] parameters() {
        double[] answer = new double[2];
        answer[0] = a;
        answer[1] = b;
        return answer;
    }

    /**
     * This method assumes that the range of the argument has been checked.
     * @return double the value for which the distribution function
     *													is equal to x.
     * @param x double value of the distribution function.
     */
    protected double privateInverseDistributionValue(double x) {
        return (b - a) * x + a;
    }

    /**
     * @param p double[]	assigns the parameters
     */
    public void setParameters(double[] params) {
        a = params[0];
        b = params[1];
    }

    /**
     * @return double skewness of the distribution.
     */
    public double skewness() {
        return 0;
    }

    /**
     * @return double probability density function
     * @param x double random variable
     */
    public double value(double x) {
        if (x < a)
            return 0;
        else if (x < b)
            return 1 / (b - a);
        else
            return 0;
    }

    /**
     * @return double variance of the distribution
     */
    public double variance() {
        double range = b - a;
        return range * range / 12;
    }
}