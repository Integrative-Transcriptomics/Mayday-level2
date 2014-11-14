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

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterpolation.NevilleInterpolator;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbScientificCurves.Curve;

/**
 * Romberg integration method
 *
 * @author Didier H. Besset
 */
public class RombergIntegrator extends TrapezeIntegrator {
    /**
     * Order of the interpolation.
     */
    private int order = 5;
    /**
     * Structure containing the last estimations.
     */
    private Curve estimates;
    /**
     * Neville interpolator.
     */
    private NevilleInterpolator interpolator;

    /**
     * RombergIntegrator constructor.
     * @param func edu.mit.broad.genome.alg.ext.DhbInterfaces.OneVariableFunction
     * @param from double
     * @param to double
     */
    public RombergIntegrator(mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.OneVariableFunction func,
                             double from, double to) {
        super(func, from, to);
    }

    /**
     * @return double
     */
    public double evaluateIteration() {
        estimates.addPoint(estimates.xValueAt(estimates.size() - 1) * 0.25,
                           highOrderSum());
        if (estimates.size() < order)
            return 1;
        double[] interpolation = interpolator.valueAndError(0);
        estimates.removePointAt(0);
        result = interpolation[0];
        return relativePrecision(Math.abs(interpolation[1]));
    }

    public void initializeIterations() {
        super.initializeIterations();
        estimates = new Curve();
        interpolator = new NevilleInterpolator(estimates);
        estimates.addPoint(1, sum);
    }

    /**
     * @param n int
     */
    public void setOrder(int n) {
        order = n;
    }
}