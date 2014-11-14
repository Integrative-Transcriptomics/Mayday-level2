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
 * Factory of point/vector & function holders for minimizing functions.
 *
 * @author Didier H. Besset
 */
public class MinimizingPointFactory extends OptimizingPointFactory {
    /**
     * Constructor method.
     */
    public MinimizingPointFactory() {
        super();
    }

    /**
     * @return OptimizingPoint	an minimizing point strategy.
     */
    public OptimizingPoint createPoint(double x, mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.OneVariableFunction f) {
        return new MinimizingPoint(x, f);
    }

    /**
     * @return OptimizingVector	an minimizing vector strategy.
     */
    public OptimizingVector createVector(double[] v, mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.ManyVariableFunction f) {
        return new MinimizingVector(v, f);
    }
}