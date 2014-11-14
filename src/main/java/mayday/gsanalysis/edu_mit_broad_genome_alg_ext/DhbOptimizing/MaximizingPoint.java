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
package mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbOptimizing;

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.OneVariableFunction;

/**
 * Point & function holder used in maximizing one-variable functions.
 *
 * @author Didier H. Besset
 */
public class MaximizingPoint extends OptimizingPoint {
    /**
     * Constructor method.
     * @param x double
     * @param f edu.mit.broad.genome.alg.ext.DhbInterfaces.OneVariableFunction
     */
    public MaximizingPoint(double x, OneVariableFunction f) {
        super(x, f);
    }

    /**
     * @return boolean	true if the receiver is "better" than
     * 												the supplied point
     * @param point OptimizingPoint
     */
    public boolean betterThan(OptimizingPoint point) {
        return getValue() > point.getValue();
    }

    /**
     * (used by method toString).
     * @return java.lang.String
     */
    protected final String printedKey() {
        return " max@";
    }
}