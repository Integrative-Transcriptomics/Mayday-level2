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

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbMatrixAlgebra.DhbVector;

/**
 * Cluster using Mahalanobis distance.
 *
 * @author Didier H. Besset
 */
public class CovarianceCluster extends Cluster {
    private MahalanobisCenter center = null;

    /**
     * Default constructor method.
     */
    public CovarianceCluster() {
        super();
    }

    /**
     * Constructor method.
     * @param DhbVector center of the receiver
     */
    public CovarianceCluster(DhbVector v) {
        super(v);
    }

    /**
     * Accumulation is delegated to the Mahalanobis center.
     */
    public void accumulate(mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbMatrixAlgebra.DhbVector dataPoint) {
        center.accumulate(dataPoint);
    }

    /**
     * Distance computation is delegated to the Mahalanobis center.
     */
    public double distanceTo(DhbVector dataPoint) {
        return center.distanceTo(dataPoint);
    }

    /**
     * @return long	number of data points accumulated in the receiver
     */
    public long getSampleSize() {
        return center.getCount();
    }

    /**
     * @param v DhbVector	center for the receiver
     */
    public void initialize(DhbVector v) {
        center = new MahalanobisCenter(v);
    }

    /**
     * @return boolean	true if the cluster is in an undefined state.
     */
    public boolean isUndefined() {
        return center == null;
    }

    public void reset() {
        super.reset();
        center.computeParameters();
        center.reset();
    }

    /**
     * @return java.lang.String
     */
    public String toString() {
        return center.toString();
    }
}