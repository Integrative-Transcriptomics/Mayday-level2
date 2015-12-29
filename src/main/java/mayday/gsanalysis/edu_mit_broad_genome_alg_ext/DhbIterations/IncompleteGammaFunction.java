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
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbFunctionEvaluation.GammaFunction;

/**
 * IncompleteGamma function
 *
 * @author Didier H. Besset
 */
public class IncompleteGammaFunction implements mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.OneVariableFunction {
    /**
     * Function parameter.
     */
    private double alpha;
    /**
     * Constant to be computed once only.
     */
    private double alphaLogGamma;
    /**
     * Infinite series.
     */
    private IncompleteGammaFunctionSeries series;
    /**
     * Continued fraction.
     */
    private IncompleteGammaFunctionFraction fraction;

    /**
     * Constructor method.
     */
    public IncompleteGammaFunction(double a) {
        alpha = a;
        alphaLogGamma = GammaFunction.logGamma(alpha);
    }

    /**
     * @return double
     * @param x double
     */
    private double evaluateFraction(double x) {
        if (fraction == null) {
            fraction = new IncompleteGammaFunctionFraction(alpha);
            fraction.setDesiredPrecision(
                    DhbMath.defaultNumericalPrecision());
        }
        fraction.setArgument(x);
        fraction.evaluate();
        return fraction.getResult();
    }

    /**
     * @return double		evaluate the series of the incomplete gamma function.
     * @param x double
     */
    private double evaluateSeries(double x) {
        if (series == null) {
            series = new IncompleteGammaFunctionSeries(alpha);
            series.setDesiredPrecision(
                    DhbMath.defaultNumericalPrecision());
        }
        series.setArgument(x);
        series.evaluate();
        return series.getResult();
    }

    /**
     * Returns the value of the function for the specified variable value.
     */
    public double value(double x) {
        if (x == 0)
            return 0;
        double norm = Math.exp(Math.log(x) * alpha - x - alphaLogGamma);
        return x - 1 < alpha
                ? evaluateSeries(x) * norm
                : 1 - norm / evaluateFraction(x);
    }
}