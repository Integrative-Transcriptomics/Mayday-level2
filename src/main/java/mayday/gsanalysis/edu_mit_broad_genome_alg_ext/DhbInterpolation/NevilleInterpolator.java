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

/**
 * NevilleInterpolator
 *
 * @author Didier H. Besset
 */
public class NevilleInterpolator extends LagrangeInterpolator {
    protected double[] leftErrors = null;
    protected double[] rightErrors = null;

    /**
     * Constructor method.
     * @param pts edu.mit.broad.genome.alg.ext.DhbInterfaces.PointSeries contains the points sampling
     * 				the function to interpolate.
     * @exception java.lang.IllegalArgumentException points are not sorted
     * 				in increasing x values.
     */
    public NevilleInterpolator(mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbInterfaces.PointSeries pts) {
        super(pts);
        for (int i = 1; i < pts.size(); i++) {
            if (pts.xValueAt(i - 1) >= pts.xValueAt(i))
                throw new IllegalArgumentException
                        ("Points must be sorted in increasing x value");
        }
    }

    /**
     * @param m int	order of the difference
     * @param n int	index of difference
     * @param x double	argument
     */
    protected void computeNextDifference(int m, int n, double x) {
        double leftDist = points.xValueAt(n) - x;
        double rightDist = points.xValueAt(n + m + 1) - x;
        double ratio = (leftErrors[n + 1] - rightErrors[n])
                / (leftDist - rightDist);
        leftErrors[n] = ratio * leftDist;
        rightErrors[n] = ratio * rightDist;
    }

    /**
     * @return int
     */
    private int initializeDifferences(double x) {
        int size = points.size();
        if (leftErrors == null || leftErrors.length != size) {
            leftErrors = new double[size];
            rightErrors = new double[size];
        }
        double minDist = Math.abs(x - points.xValueAt(0));
        if (minDist == 0)
            return -1;
        int nearestIndex = 0;
        leftErrors[0] = points.yValueAt(0);
        rightErrors[0] = leftErrors[0];
        for (int n = 1; n < size; n++) {
            double dist = Math.abs(x - points.xValueAt(n));
            if (dist < minDist) {
                if (dist == 0)
                    return -n - 1;
                minDist = dist;
                nearestIndex = n;
            }
            leftErrors[n] = points.yValueAt(n);
            rightErrors[n] = leftErrors[n];
        }
        return nearestIndex;
    }

    /**
     * @return double
     * @param aNumber double
     */
    public double value(double aNumber) {
        return valueAndError(aNumber)[0];
    }

    /**
     * @return double[]	an array with 2 elements:
     * 		[0] interpolated value, [1] estimated error
     * @param x double
     */
    public double[] valueAndError(double x) {
        double[] answer = new double[2];
        int nearestIndex = initializeDifferences(x);
        if (nearestIndex < 0) {
            answer[0] = points.yValueAt(-1 - nearestIndex);
            answer[1] = 0;
            return answer;
        }
        int size = points.size();
        answer[0] = leftErrors[nearestIndex--];
//        double leftDist, rightDist, ratio;
        for (int m = 0; m < size - 1; m++) {
            for (int n = 0; n < size - 1 - m; n++) {
                computeNextDifference(m, n, x);
            }
            answer[1] = (size - m > 2 * (nearestIndex + 1))
                    ? leftErrors[nearestIndex + 1]
                    : rightErrors[nearestIndex--];
            answer[0] += answer[1];
        }
        return answer;
    }
}