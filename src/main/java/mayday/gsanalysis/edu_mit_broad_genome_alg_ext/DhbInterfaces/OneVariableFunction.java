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
 * OneVariableFunction is an interface for mathematical functions of
 * a single variable, that is functions of the form f(x).
 *
 * @author Didier H. Besset
 */
public interface OneVariableFunction {


    /**
     * Returns the value of the function for the specified variable value.
     */
    public double value(double x);
}