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

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.OneVariableFunction;

/**
 * Zero finding by bisection.
 *
 * @author Didier H. Besset
 */
public class BisectionZeroFinder extends FunctionalIterator {
    /**
     * Value at which the function's value is negative.
     */
    private double xNeg;
    /**
     * Value at which the function's value is positive.
     */
    private double xPos;

    /**
     * @param func edu.mit.broad.genome.alg.ext.DhbInterfaces.OneVariableFunction
     */
    public BisectionZeroFinder(mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.OneVariableFunction func) {
        super(func);
    }

    /**
     * @param func edu.mit.broad.genome.alg.ext.DhbInterfaces.OneVariableFunction
     * @param x1 location at which the function yields a negative value
     * @param x2 location at which the function yields a positive value
     */
    public BisectionZeroFinder(OneVariableFunction func, double x1, double x2)
            throws IllegalArgumentException {
        this(func);
        setNegativeX(x1);
        setPositiveX(x2);
    }

    /**
     * @return double
     */
    public double evaluateIteration() {
        result = (xPos + xNeg) * 0.5;
        if (f.value(result) > 0)
            xPos = result;
        else
            xNeg = result;
        return relativePrecision(Math.abs(xPos - xNeg));
    }

    /**
     * @param x double
     * @exception java.lang.IllegalArgumentException
     * 					if the function's value is not negative
     */
    public void setNegativeX(double x) throws IllegalArgumentException {
        if (f.value(x) > 0)
            throw new IllegalArgumentException("f(" + x +
                                               ") is positive instead of negative");
        xNeg = x;
    }

    /**
     * (c) Copyrights Didier BESSET, 1999, all rights reserved.
     * @param x double
     * @exception java.lang.IllegalArgumentException
     * 					if the function's value is not positive
     */
    public void setPositiveX(double x) throws IllegalArgumentException {
        if (f.value(x) < 0)
            throw new IllegalArgumentException("f(" + x +
                                               ") is negative instead of positive");
        xPos = x;
    }
}