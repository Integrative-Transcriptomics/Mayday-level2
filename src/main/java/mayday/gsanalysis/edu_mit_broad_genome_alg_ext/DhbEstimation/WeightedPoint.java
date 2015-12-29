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
package mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbEstimation;

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.OneVariableFunction;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbStatistics.StatisticalMoments;

/**
 * Point with error used in chi-square test and least square fits
 *
 * @author Didier H. Besset
 */
public class WeightedPoint {
    private double xValue;
    private double yValue;
    private double weight;
    private double error = Double.NaN;

    /**
     * Constructor method.
     * @param x double
     * @param y double
     */
    public WeightedPoint(double x, double y) {
        this(x, y, 1);
    }

    /**
     * Constructor method.
     * @param x double
     * @param y double
     * @param w double
     */
    public WeightedPoint(double x, double y, double w) {
        xValue = x;
        yValue = y;
        weight = w;
    }

    /**
     * Constructor method.
     * @param x double
     * @param n int	a Histogram bin content
     */
    public WeightedPoint(double x, int n) {
        this(x, n, 1.0 / Math.max(n, 1));
    }

    /**
     * Constructor method.
     * @param x double
     * @param m edu.mit.broad.genome.alg.ext.DhbStatistics.StatisticalMoments
     */
    public WeightedPoint(double x, StatisticalMoments m) {
        this(x, m.average());
        setError(m.errorOnAverage());
    }

    /**
     * @return double	contribution to chi^2 sum against
     *												a theoretical function
     * @param wp WeightedPoint
     */
    public double chi2Contribution(WeightedPoint wp) {
        double residue = yValue - wp.yValue();
        return residue * residue / (1 / wp.weight() + 1 / weight);
    }

    /**
     * @return double	contribution to chi^2 sum against
     *												a theoretical function
     * @param f edu.mit.broad.genome.alg.ext.DhbInterfaces.OneVariableFunction
     */
    public double chi2Contribution(OneVariableFunction f) {
        double residue = yValue - f.value(xValue);
        return residue * residue * weight;
    }

    /**
     * @return double	error of the receiver
     */
    public double error() {
        if (Double.isNaN(error))
            error = 1 / Math.sqrt(weight);
        return error;
    }

    /**
     * @param e double error on the point
     */
    public void setError(double e) {
        error = e;
        weight = 1 / (e * e);
    }

    /**
     * @return java.lang.String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('(');
        sb.append(xValue);
        sb.append(',');
        sb.append(yValue);
        sb.append("+-");
        sb.append(error());
        sb.append(')');
        return sb.toString();
    }

    /**
     * @return double	weight of the receiver
     */
    public double weight() {
        return weight;
    }

    /**
     * @return double	x value of the receiver
     */
    public double xValue() {
        return xValue;
    }

    /**
     * @return double	y value of the receiver
     */
    public double yValue() {
        return yValue;
    }
}