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
package mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterpolation;

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.OneVariableFunction;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.PointSeries;

/**
 * A LagrangeInterpolator can be used to interpolate values between
 * a series of 2-dimensional points. The interpolation function is
 * the Langrange interpolation polynomial of a degree equal to the
 * number of points in the series minus one.
 *
 * @author Didier H. Besset
 */
public class LagrangeInterpolator implements OneVariableFunction {
    /**
     * Points containing the values.
     */
    protected PointSeries points;

    /**
     * Constructor method.
     * @param pts the series of points.
     * @see PointSeries
     */
    public LagrangeInterpolator(PointSeries pts) {
        points = pts;
    }

    /**
     * Computes the interpolated y value for a given x value.
     * @param aNumber x value.
     * @return interpolated y value.
     */
    public double value(double aNumber) {
        double norm = 1.0;
        int size = points.size();
        double products[] = new double[size];
        for (int i = 0; i < size; i++)
            products[i] = 1;
        double dx;
        for (int i = 0; i < size; i++) {
            dx = aNumber - points.xValueAt(i);
            if (dx == 0)
                return points.yValueAt(i);
            norm *= dx;
            for (int j = 0; j < size; j++) {
                if (i != j)
                    products[j] *= points.xValueAt(j)
                            - points.xValueAt(i);
            }
        }
        double answer = 0.0;
        for (int i = 0; i < size; i++)
            answer += points.yValueAt(i)
                    / (products[i] * (aNumber - points.xValueAt(i)));
        return norm * answer;
    }
}