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

import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbFunctionEvaluation.PolynomialFunction;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbMatrixAlgebra.DhbIllegalDimension;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbMatrixAlgebra.DhbVector;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbMatrixAlgebra.SymmetricMatrix;

/**
 * Polynomial with error estimation
 *
 * @author Didier H. Besset
 */
public class EstimatedPolynomial extends PolynomialFunction {
    /**
     * Error matrix.
     */
    SymmetricMatrix errorMatrix;

    /**
     * Constructor method.
     * @param coeffs double[]
     * @param e double[]	error matrix
     */
    public EstimatedPolynomial(double[] coeffs, SymmetricMatrix e) {
        super(coeffs);
        errorMatrix = e;
    }

    /**
     * @return double
     * @param x double
     */
    public double error(double x) {
        int n = degree() + 1;
        double[] errors = new double[n];
        errors[0] = 1;
        for (int i = 1; i < n; i++)
            errors[i] = errors[i - 1] * x;
        DhbVector errorVector = new DhbVector(errors);
        double answer;
        try {
            answer = errorVector.product(
                    errorMatrix.product(errorVector));
        } catch (DhbIllegalDimension e) {
            answer = Double.NaN;
        }
        ;
        return Math.sqrt(answer);
    }
}