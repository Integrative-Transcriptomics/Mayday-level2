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

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbFunctionEvaluation.DhbMath;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbIterations.RombergIntegrator;

/**
 * Subclasses oare fdr whose distribution cannot be expressed
 * as a closed expression or have no numerical approximation.
 *
 * @author Didier H. Besset
 */
public abstract class ProbabilityDensityWithUnknownDistribution
        extends ProbabilityDensityFunction {

    /**
     * Returns the probability of finding a random variable smaller than
     * or equal to x.
     * Computation is done using Romberg integration.
     * @return integral of the probability density function from lowValue() to x.
     * @param x double upper limit of intergral.
     */
    public double distributionValue(double x) {
        RombergIntegrator integrator = new RombergIntegrator(this, lowValue(), x);
        integrator.setDesiredPrecision(DhbMath.defaultNumericalPrecision());
        integrator.evaluate();
        return integrator.getResult();
    }

    /**
     * Returns the probability of finding a random variable between x1 and x2.
     * @return double integral of the probability density function from x1 to x2.
     * @param x1 double lower limit of integral.
     * @param x2 double upper limit of integral.
     */
    public double distributionValue(double x1, double x2) {
        RombergIntegrator integrator = new RombergIntegrator(this, x1, x2);
        integrator.setDesiredPrecision(
                DhbMath.defaultNumericalPrecision());
        integrator.evaluate();
        return integrator.getResult();
    }

    /**
     * @return double	lower limit of the integral used to compute
     *											the distribution function
     */
    protected abstract double lowValue();
}