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
 * Abstract data server for data mining.
 *
 * @author Didier H. Besset
 */
public abstract class AbstractDataServer {
    /**
     * Constructor method.
     */
    public AbstractDataServer() {
        super();
    }

    /**
     * Closes the stream of data.
     */
    public abstract void close();

    /**
     * Opens the stream of data.
     */
    public abstract void open();

    /**
     * @return DhbVector	next data point found on the stream
     * @exception java.io.EOFException when no more data point can be found.
     */
    public abstract DhbVector read() throws java.io.EOFException;

    /**
     * Rewind the stream of data.
     */
    public abstract void reset();
}