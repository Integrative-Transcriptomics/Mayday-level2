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

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.OneVariableFunction;

/**
 * This class is used to find the inverse distribution function of
 * a probability density function.
 *
 * @author Didier H. Besset
 */
public final class OffsetDistributionFunction
        implements OneVariableFunction {
    /**
     * Probability density function.
     */
    private ProbabilityDensityFunction probabilityDensity;
    /**
     * Value for which the inverse value is desired.
     */
    private double offset;


    /**
     * Create a new instance with given parameters.
     * @param p statistics.ProbabilityDensityFunction
     * @param x double
     */
    protected OffsetDistributionFunction(ProbabilityDensityFunction p,
                                         double x) {
        probabilityDensity = p;
        offset = x;
    }

    /**
     * @return distribution function minus the offset.
     */
    public double value(double x) {
        return probabilityDensity.distributionValue(x) - offset;
    }
}