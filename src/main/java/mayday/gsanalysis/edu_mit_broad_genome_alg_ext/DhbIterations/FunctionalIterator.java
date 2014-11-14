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

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.OneVariableFunction;

/**
 * Iterative process based on a one-variable function,
 * having a single numerical result.
 *
 * @author Didier H. Besset
 */
public abstract class FunctionalIterator extends IterativeProcess {
    /**
     * Best approximation of the zero.
     */
    protected double result = Double.NaN;
    /**
     * Function for which the zero will be found.
     */
    protected OneVariableFunction f;

    /**
     * Generic constructor.
     * @param func OneVariableFunction
     * @param start double
     */
    public FunctionalIterator(OneVariableFunction func) {
        setFunction(func);
    }

    /**
     * Returns the result (assuming convergence has been attained).
     */
    public double getResult() {
        return result;
    }

    /**
     * @return double
     * @param epsilon double
     */
    public double relativePrecision(double epsilon) {
        return relativePrecision(epsilon, Math.abs(result));
    }

    /**
     * @param func edu.mit.broad.genome.alg.ext.DhbInterfaces.OneVariableFunction
     */
    public void setFunction(OneVariableFunction func) {
        f = func;
    }

    /**
     * @param x double
     */
    public void setInitialValue(double x) {
        result = x;
    }
}