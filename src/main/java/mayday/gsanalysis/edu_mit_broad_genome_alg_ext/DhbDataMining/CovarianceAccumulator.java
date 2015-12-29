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
package mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbDataMining;

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbMatrixAlgebra.DhbIllegalDimension;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbMatrixAlgebra.DhbNonSymmetricComponents;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbMatrixAlgebra.SymmetricMatrix;

/**
 * Statistical average and covariance for vectors
 *
 * @author Didier H. Besset
 */
public class CovarianceAccumulator extends VectorAccumulator {
    private double[][] covariance;

    /**
     * Constructor method
     * @param n int
     */
    public CovarianceAccumulator(int n) {
        super(n, 1);
        covariance = new double[n][n];
        reset();
    }

    /**
     * @param v DhbVector	vector to accumulate in the receiver
     */
    public void accumulate(double[] v) {
        long n = count;
        count += 1;
        double[] deltas = new double[average.length];
        int j;
        double r = (double) n / (double) count;
        for (int i = 0; i < average.length; i++) {
            deltas[i] = (average[i] - v[i]) / count;
            average[i] -= deltas[i];
            for (j = 0; j <= i; j++)
                covariance[i][j] = r * covariance[i][j] + n * deltas[i] * deltas[j];
        }
    }

    /**
     * @return double
     * @param n int
     * @param m int
     */
    public double correlationCoefficient(int n, int m) {
        return covariance[n][m] / Math.sqrt(covariance[n][n] * covariance[m][m]);
    }

    /**
     * @return SymmetricMatrix	covariance matrix
     */
    public SymmetricMatrix covarianceMatrix() {
        double[][] components = new double[average.length][average.length];
        int j;
        for (int i = 0; i < average.length; i++) {
            for (j = 0; j <= i; j++) {
                components[i][j] = covariance[i][j];
                components[j][i] = components[i][j];
            }
        }
        try {
            return SymmetricMatrix.fromComponents(components);
        } catch (DhbNonSymmetricComponents e) {
            return null;
        } catch (DhbIllegalDimension e) {
            return null;
        }
    }

    /**
     * @param sb java.lang.StringBuffer
     */
    protected void printOn(StringBuffer sb) {
        super.printOn(sb);
        for (int i = 0; i < average.length; i++) {
            char separator = '\n';
            for (int j = 0; j <= i; j++) {
                sb.append(separator);
                sb.append(covariance[i][j]);
                separator = ' ';
            }
        }
    }

    public void reset() {
        super.reset();
        int j;
        for (int i = 0; i < average.length; i++) {
            for (j = 0; j <= i; j++)
                covariance[i][j] = 0;
        }
    }

    /**
     * @return double
     * @param n int
     * @param m int
     */
    public double standardDeviation(int n) {
        return Math.sqrt(variance(n));
    }

    /**
     * @return double
     * @param n int
     */
    public double variance(int n) {
        return covariance[n][n] * count / (count - 1);
    }
}