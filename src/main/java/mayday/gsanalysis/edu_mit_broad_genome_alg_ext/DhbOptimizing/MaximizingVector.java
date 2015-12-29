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



/**
 * Vector & function holder used in maximizing many-variable functions.
 *
 * @author Didier H. Besset
 */
public class MaximizingVector extends OptimizingVector {
    /**
     * Constructor method.
     * @param v double[]
     * @param f edu.mit.broad.genome.alg.ext.DhbInterfaces.ManyVariableFunction
     */
    public MaximizingVector(double[] v,
                            mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.ManyVariableFunction f) {
        super(v, f);
    }

    /**
     * @return boolean	true if the receiver is "better" than
     *												the supplied point
     * @param point OptimizingVector
     */
    public boolean betterThan(OptimizingVector point) {
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