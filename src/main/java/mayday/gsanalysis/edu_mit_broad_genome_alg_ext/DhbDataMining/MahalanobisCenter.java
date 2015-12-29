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
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbMatrixAlgebra.DhbVector;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbMatrixAlgebra.SymmetricMatrix;

/**
 * This object is used to compute the Mahalanobis distance
 * to a set of data.
 *
 * @author Didier H. Besset
 */
public class MahalanobisCenter {
    private DhbVector center = null;
    private SymmetricMatrix inverseCovariance = null;
    private CovarianceAccumulator accumulator;

    /**
     * Constructor method.
     * @param int dimension of the receiver
     */
    public MahalanobisCenter(int dimension) {
        accumulator = new CovarianceAccumulator(dimension);
    }

    /**
     * Constructor method.
     * @param DhbVector center of the receiver
     */
    public MahalanobisCenter(DhbVector v) {
        accumulator = new CovarianceAccumulator(v.dimension());
        center = v;
        inverseCovariance = SymmetricMatrix.identityMatrix(v.dimension());
    }

    /**
     * Accumulation is delegated to the covariance accumulator.
     * @param v DhbVector	vector of values to accumulate in the receiver
     */
    public void accumulate(DhbVector v) {
        accumulator.accumulate(v);
    }

    /**
     * Computes the parameters of the receiver.
     */
    public void computeParameters() {
        center = accumulator.averageVector();
        inverseCovariance = (SymmetricMatrix)
                accumulator.covarianceMatrix().inverse();
    }

    /**
     * @return double	Mahalanobis distance of the data point from the
     *												center of the receiver.
     * @param dataPoint DhbVector	data point
     */
    public double distanceTo(DhbVector dataPoint) {
        try {
            DhbVector v = dataPoint.subtract(center);
            return v.product(inverseCovariance.product(v));
        } catch (DhbIllegalDimension e) {
            return Double.NaN;
        }
    }

    /**
     * @return long	number of data points inside the receiver
     */
    public long getCount() {
        return accumulator.getCount();
    }

    /**
     * Keep the center and covariance matrix.
     */
    public void reset() {
        accumulator.reset();
    }

    /**
     * @return java.lang.String
     */
    public String toString() {
        return center.toString();
    }
}