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
 * Multi-strategy optimizer of many-variable functions.
 *
 * @author Didier H. Besset
 */
public class MultiVariableGeneralOptimizer extends MultiVariableOptimizer {
    /**
     * Initial range for random search.
     */
    protected double[] range;

    /**
     * Constructor method.
     * @param func edu.mit.broad.genome.alg.ext.DhbInterfaces.ManyVariableFunction
     * @param pointCreator edu.mit.broad.genome.alg.ext.DhbOptimizing.OptimizingPointFactory
     * @param initialValue double[]
     */
    public MultiVariableGeneralOptimizer(mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.ManyVariableFunction func,
                                         OptimizingPointFactory pointCreator, double[] initialValue) {
        super(func, pointCreator, initialValue);
    }

    public double evaluateIteration() {
        HillClimbingOptimizer finder = new HillClimbingOptimizer(f, pointFactory,
                                                                 result);
        finder.setDesiredPrecision(getDesiredPrecision());
        finder.setMaximumIterations(getMaximumIterations());
        finder.evaluate();
        result = finder.getResult();
        return finder.getPrecision();
    }

    public void initializeIterations() {
        if (range != null)
            performGeneticOptimization();
        performSimplexOptimization();
    }

    private void performGeneticOptimization() {
        VectorChromosomeManager manager = new VectorChromosomeManager();
        manager.setRange(range);
        manager.setOrigin(result);
        VectorGeneticOptimizer finder = new VectorGeneticOptimizer(f, pointFactory, manager);
        finder.evaluate();
        result = finder.getResult();
    }

    private void performSimplexOptimization() {
        SimplexOptimizer finder = new SimplexOptimizer(f, pointFactory, result);
        finder.setDesiredPrecision(Math.sqrt(getDesiredPrecision()));
        finder.setMaximumIterations(getMaximumIterations());
        finder.evaluate();
        result = finder.getResult();
    }

    /**
     * @param x double	component of the origin of the hypercube
     *				constraining the domain of definition of the function
     */
    public void setOrigin(double[] x) {
        result = x;
    }

    /**
     * @param x double	components of the lengths of the hypercube
     *				constraining the domain of definition of the function
     */
    public void setRange(double[] x) {
        range = x;
    }
}