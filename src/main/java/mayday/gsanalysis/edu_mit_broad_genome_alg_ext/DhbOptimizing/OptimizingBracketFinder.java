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


import java.util.Random;

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.OneVariableFunction;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbIterations.FunctionalIterator;

/**
 * Finds a bracket for the optimum of a one-variable function.
 *
 * @author Didier H. Besset
 */
public class OptimizingBracketFinder extends FunctionalIterator {
    private OptimizingPoint[] bestPoints = null;
    private OptimizingPointFactory pointFactory;

    /**
     * Constructor method
     * @param func OneVariableFunction
     * @param pointCreator OptimizingPointFactory	a factory to create
     *													strategy points
     */
    public OptimizingBracketFinder(OneVariableFunction func,
                                   OptimizingPointFactory pointCreator) {
        super(func);
        pointFactory = pointCreator;
    }

    /**
     * @return double	1 as long as no bracket has been found
     */
    private double computePrecision() {
        return bestPoints[1].betterThan(bestPoints[0]) &&
                bestPoints[1].betterThan(bestPoints[2])
                ? 0 : 1;
    }

    /**
     * @return double	pseudo-precision of the current search
     */
    public double evaluateIteration() {
        if (bestPoints[0].betterThan(bestPoints[1]))
            moveTowardNegative();
        else if (bestPoints[2].betterThan(bestPoints[1]))
            moveTowardPositive();
        return computePrecision();
    }

    /**
     * @return OptimizingPoint[]	a triplet bracketing the optimum
     */
    public OptimizingPoint[] getBestPoints() {
        return bestPoints;
    }

    /**
     * Use random locations (drunkard's walk algorithm).
     */
    public void initializeIterations() {
        Random generator = new Random();
        bestPoints = new OptimizingPoint[3];
        if (Double.isNaN(result))
            result = generator.nextDouble();
        bestPoints[0] = pointFactory.createPoint(result, f);
        bestPoints[1] = pointFactory.createPoint(generator.nextDouble()
                                                 + bestPoints[0].getPosition(), f);
        bestPoints[2] = pointFactory.createPoint(generator.nextDouble()
                                                 + bestPoints[1].getPosition(), f);
    }

    /**
     * Shift the best points toward negative positions.
     */
    private void moveTowardNegative() {
        OptimizingPoint newPoint = pointFactory.createPoint(
                3 * bestPoints[0].getPosition()
                - 2 * bestPoints[1].getPosition(), f);
        bestPoints[2] = bestPoints[1];
        bestPoints[1] = bestPoints[0];
        bestPoints[0] = newPoint;
    }

    /**
     * Shift the best points toward positive positions.
     */
    private void moveTowardPositive() {
        OptimizingPoint newPoint = pointFactory.createPoint(
                3 * bestPoints[2].getPosition()
                - 2 * bestPoints[1].getPosition(), f);
        bestPoints[0] = bestPoints[1];
        bestPoints[1] = bestPoints[2];
        bestPoints[2] = newPoint;
    }
}