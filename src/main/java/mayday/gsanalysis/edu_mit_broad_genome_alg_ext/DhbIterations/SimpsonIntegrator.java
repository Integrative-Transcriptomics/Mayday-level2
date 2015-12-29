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

/**
 * Simpson integration method
 *
 * @author Didier H. Besset
 */
public class SimpsonIntegrator extends TrapezeIntegrator {
    /**
     * SimpsonIntegrator constructor.
     * @param f edu.mit.broad.genome.alg.ext.DhbInterfaces.OneVariableFunction
     * @param from double
     * @param to double
     */
    public SimpsonIntegrator(mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.OneVariableFunction f,
                             double from, double to) {
        super(f, from, to);
    }

    /**
     * @return double
     */
    public double evaluateIteration() {
        if (getIterations() < 2) {
            highOrderSum();
            return getDesiredPrecision();
        }
        double oldResult = result;
        double oldSum = sum;
        result = (4 * highOrderSum() - oldSum) / 3.0;
        return relativePrecision(Math.abs(result - oldResult));
    }
}