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
package mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces;

/**
 * ParametrizedOneVariableFunction is an interface for mathematical
 * functions of one variable depending on several parameters,
 * that is functions of the form f(x;p), where p is a vector.
 *
 * @author Didier H. Besset
 */
public interface ParametrizedOneVariableFunction
        extends OneVariableFunction {
    /**
     * @return double[]	array containing the parameters
     */
    double[] parameters();

    /**
     * @param p double[]	assigns the parameters
     */
    void setParameters(double[] p);

    /**
     * Evaluate the function and the gradient of the function with respect
     * to the parameters.
     * @return double[]	0: function's value, 1,2,...,n function's gradient
     * @param x double
     */
    double[] valueAndGradient(double x);
}