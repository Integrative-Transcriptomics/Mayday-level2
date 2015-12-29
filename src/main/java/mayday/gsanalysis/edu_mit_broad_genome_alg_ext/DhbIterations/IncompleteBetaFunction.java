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
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.OneVariableFunction;

/**
 * Incomplete Beta function
 *
 * @author Didier H. Besset
 */
public class IncompleteBetaFunction implements OneVariableFunction {
    /**
     * Function parameters.
     */
    private double alpha1;
    private double alpha2;
    /**
     * Constant to be computed once only.
     */
    private double logNorm;
    /**
     * Continued fractions.
     */
    private IncompleteBetaFunctionFraction fraction;
    @SuppressWarnings("unused")
	private IncompleteBetaFunctionFraction inverseFraction;

    /**
     * Constructor method.
     * @param a1 double
     * @param a2 double
     */
    public IncompleteBetaFunction(double a1, double a2) {
        alpha1 = a1;
        alpha2 = a2;
        logNorm = GammaFunction.logGamma(alpha1 + alpha2)
                - GammaFunction.logGamma(alpha1)
                - GammaFunction.logGamma(alpha2);
    }

    /**
     * @return double
     * @param x double
     */
    private double evaluateFraction(double x) {
        if (fraction == null) {
            fraction = new IncompleteBetaFunctionFraction(alpha1, alpha2);
            fraction.setDesiredPrecision(DhbMath.defaultNumericalPrecision());
        }
        fraction.setArgument(x);
        fraction.evaluate();
        return fraction.getResult();
    }

    /**
     * @return double
     * @param x double
     */
    private double evaluateInverseFraction(double x) {
        if (fraction == null) {
            fraction = new IncompleteBetaFunctionFraction(alpha2, alpha1);
            fraction.setDesiredPrecision(DhbMath.defaultNumericalPrecision());
        }
        fraction.setArgument(x);
        fraction.evaluate();
        return fraction.getResult();
    }

    public double value(double x) {
        if (x == 0)
            return 0;
        if (x == 1)
            return 1;
        double norm = Math.exp(alpha1 * Math.log(x)
                               + alpha2 * Math.log(1 - x) + logNorm);
        return (alpha1 + alpha2 + 2) * x < (alpha1 + 1)
                ? norm / (evaluateFraction(x) * alpha1)
                : 1 - norm / (evaluateInverseFraction(1 - x)
                * alpha2);
    }
}