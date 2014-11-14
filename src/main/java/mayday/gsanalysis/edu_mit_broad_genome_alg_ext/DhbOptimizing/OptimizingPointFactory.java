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

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.ManyVariableFunction;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.OneVariableFunction;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbMatrixAlgebra.DhbVector;

/**
 * Factory of point/vector & function holders for optimizing functions.
 *
 * @author Didier H. Besset
 */
public abstract class OptimizingPointFactory {
    /**
     * Constructor method.
     */
    public OptimizingPointFactory() {
        super();
    }

    /**
     * @return edu.mit.broad.genome.alg.ext.DhbOptimizing.OptimizingPoint
     * @param x double
     * @param f OneVariableFunction
     */
    public abstract OptimizingPoint createPoint(double x,
                                                OneVariableFunction f);

    /**
     * @return edu.mit.broad.genome.alg.ext.DhbOptimizing.OptimizingVector
     * @param v double[]
     * @param f ManyVariableFunction
     */
    public abstract OptimizingVector createVector(double[] v,
                                                  ManyVariableFunction f);

    /**
     * @return edu.mit.broad.genome.alg.ext.DhbOptimizing.OptimizingVector
     * @param v DhbVector
     * @param f ManyVariableFunction
     */
    public OptimizingVector createVector(DhbVector v,
                                         mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.ManyVariableFunction f) {
        return createVector(v.toComponents(), f);
    }
}