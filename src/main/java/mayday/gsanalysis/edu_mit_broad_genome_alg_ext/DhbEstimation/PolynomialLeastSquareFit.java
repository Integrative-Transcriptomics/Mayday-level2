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
package mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbEstimation;

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbMatrixAlgebra.DhbIllegalDimension;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbMatrixAlgebra.DhbNonSymmetricComponents;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbMatrixAlgebra.LUPDecomposition;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbMatrixAlgebra.SymmetricMatrix;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbStatistics.StatisticalMoments;

/**
 * Polynomial least square fit
 *
 * @author Didier H. Besset
 */
public class PolynomialLeastSquareFit {
    double[][] systemMatrix;
    double[] systemConstants;

    /**
     * Constructor method.
     */
    public PolynomialLeastSquareFit(int n) {
        int n1 = n + 1;
        systemMatrix = new double[n1][n1];
        systemConstants = new double[n1];
        reset();
    }

    /**
     * @param x double
     * @param m StatisticalMoments
     */
    public void accumulateAverage(double x, StatisticalMoments m) {
        accumulatePoint(x, m.average(), m.errorOnAverage());
    }

    /**
     * @param x double
     * @param n int	bin content
     */
    public void accumulateBin(double x, int n) {
        accumulateWeightedPoint(x, n, 1.0 / Math.max(1, n));
    }

    /**
     * @param x double
     * @param y double
     */
    public void accumulatePoint(double x, double y) {
        accumulateWeightedPoint(x, y, 1);
    }

    /**
     * @param x double
     * @param y double
     * @param error double	standard deviation on y
     */
    public void accumulatePoint(double x, double y, double error) {
        accumulateWeightedPoint(x, y, 1.0 / (error * error));
    }

    /**
     * @param x double
     * @param y double
     * @param w double	weight of point
     */
    public void accumulateWeightedPoint(double x, double y, double w) {
        double xp1 = w;
        double xp2;
        for (int i = 0; i < systemConstants.length; i++) {
            systemConstants[i] += xp1 * y;
            xp2 = xp1;
            for (int j = 0; j <= i; j++) {
                systemMatrix[i][j] += xp2;
                xp2 *= x;
            }
            xp1 *= x;
        }
    }

    /**
     * @return edu.mit.broad.genome.alg.ext.DhbEstimation.EstimatedPolynomial
     */
    public EstimatedPolynomial evaluate() {
        for (int i = 0; i < systemConstants.length; i++) {
            for (int j = i + 1; j < systemConstants.length; j++)
                systemMatrix[i][j] = systemMatrix[j][i];
        }
        try {
            LUPDecomposition lupSystem = new LUPDecomposition(
                    systemMatrix);
            double[][] components = lupSystem.inverseMatrixComponents();
            LUPDecomposition.symmetrizeComponents(components);
            return new EstimatedPolynomial(
                    lupSystem.solve(systemConstants),
                    SymmetricMatrix.fromComponents(components));
        } catch (DhbIllegalDimension e) {
        } catch (DhbNonSymmetricComponents ex) {
        }
        ;
        return null;
    }

    public void reset() {
        for (int i = 0; i < systemConstants.length; i++) {
            systemConstants[i] = 0;
            for (int j = 0; j < systemConstants.length; j++)
                systemMatrix[i][j] = 0;
        }
    }
}