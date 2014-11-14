package mayday.GWAS.statistics.ChiSquaredTest;

import mayday.GWAS.statistics.StatisticalTest;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.ChiSquaredDistribution;
import org.apache.commons.math.distribution.ChiSquaredDistributionImpl;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

/**
 * @author jaeger
 *
 */
public class Chi2Test implements StatisticalTest {

	@Override
	public double test(double[][] table, boolean one_sided)
			throws MathException {
	
		double a_case = table[0][1];
		double A_case = table[0][0];
		double a_control = table[1][1];
		double A_control = table[1][0];
		
		double N = a_case + A_case + a_control + A_control;
		
		double N_case = A_case + a_case;
		double N_control = A_control + a_control;
		
		double N_A = A_case + A_control;
		double N_a = a_case + a_control;
		
		double topTerm = Math.pow(A_case*a_control - a_case*A_control, 2) * N;
		double bottomTerm = N_case * N_control * N_A * N_a;
		
		double Z2 = topTerm / bottomTerm;
		
		double p_val = 0;
		
		if(one_sided) {
			NormalDistribution dist = new NormalDistributionImpl(0, 1);
			p_val = 1 - dist.cumulativeProbability(Math.sqrt(Z2));
		} else {
			ChiSquaredDistribution dist = new ChiSquaredDistributionImpl(1);
			p_val = 1 - dist.cumulativeProbability(Z2);
		}
		
		return p_val;
	}

	@Override
	public String getName() {
		return "Chi²-Test";
	}
}
