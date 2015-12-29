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
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbIterations.IterativeProcess;

/**
 * Abstract optimizer of many-variable functions.
 *
 * @author Didier H. Besset
 */
public abstract class MultiVariableOptimizer extends IterativeProcess {
    /**
     * Value of the function to optimize.
     */
    protected ManyVariableFunction f;
    /**
     * Best value found so far: must be set to determine the dimension
     * of the argument of the function.
     */
    protected double[] result;
    /**
     * Optimizing strategy (minimum or maximum).
     */
    protected OptimizingPointFactory pointFactory;

    /**
     * Constructor method.
     */
    public MultiVariableOptimizer(ManyVariableFunction func,
                                  OptimizingPointFactory pointCreator, double[] initialValue) {
        f = func;
        pointFactory = pointCreator;
        setInitialValue(initialValue);
    }

    /**
     * @return double[]	result of the receiver
     */
    public double[] getResult() {
        return result;
    }

    /**
     * @param v double[]	educated guess for the optimum's location
     */
    public void setInitialValue(double[] v) {
        result = v;
    }

    /**
     * Use bubble sort to sort the best points
     */
    protected void sortPoints(OptimizingVector[] bestPoints) {
        OptimizingVector temp;
        int n = bestPoints.length;
        int bound = n - 1;
        int i, m;
        while (bound >= 0) {
            m = -1;
            for (i = 0; i < bound; i++) {
                if (bestPoints[i + 1].betterThan(bestPoints[i])) {
                    temp = bestPoints[i];
                    bestPoints[i] = bestPoints[i + 1];
                    bestPoints[i + 1] = temp;
                    m = i;
                }
            }
            bound = m;
        }
    }
}