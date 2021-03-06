package mayday.Reveal.statistics.ChiSquaredTest;

import mayday.Reveal.statistics.StatisticalTest;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * @author jaeger
 *
 */
public class Chi2Test implements StatisticalTest {

	@Override
	public double test(double[][] table, boolean one_sided)
			throws Exception {
	
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
			NormalDistribution dist = new NormalDistribution(0, 1);
			p_val = 1 - dist.cumulativeProbability(Math.sqrt(Z2));
		} else {
			ChiSquaredDistribution dist = new ChiSquaredDistribution(1);
			p_val = 1 - dist.cumulativeProbability(Z2);
		}
		
		if(p_val < 0) {
			p_val = 0;
		}
		
		if(p_val > 1) {
			p_val = 1;
		}
		
		if(Double.isNaN(p_val)) {
			p_val = 1;
		}
		
		return p_val;
	}

	@Override
	public String getName() {
		return "ChiSq-Test";
	}
}
