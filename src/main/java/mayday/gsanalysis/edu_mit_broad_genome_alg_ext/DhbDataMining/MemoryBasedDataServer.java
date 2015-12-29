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
 * Data server containing data in memory for simulation purposes.
 *
 * @author Didier H. Besset
 */
public class MemoryBasedDataServer extends AbstractDataServer {
    private int index;
    private DhbVector[] dataPoints;

    /**
     * Constructor method (for internal use only)
     */
    protected MemoryBasedDataServer() {
        super();
    }

    /**
     * @param points DhbVector[] supplied data points
     * (must not be changed after creation)
     */
    public MemoryBasedDataServer(DhbVector[] points) {
        dataPoints = points;
    }

    /**
     * Nothing to do
     */
    public void close() {
    }

    /**
     * Nothing to do
     */
    public void open() {
    }

    /**
     * @return edu.mit.broad.genome.alg.ext.DhbMatrixAlgebra.DhbVector	next data point
     * @exception java.io.EOFException no more data.
     */
    public DhbVector read() throws java.io.EOFException {
        if (index >= dataPoints.length)
            throw new java.io.EOFException();
        return dataPoints[index++];
    }

    /**
     * Data index is reset
     */
    public void reset() {
        index = 0;
    }
}