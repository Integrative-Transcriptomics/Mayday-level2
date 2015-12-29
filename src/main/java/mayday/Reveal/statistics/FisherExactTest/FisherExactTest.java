package mayday.Reveal.statistics.FisherExactTest;

import mayday.Reveal.statistics.StatisticalTest;

import org.apache.commons.math.MathException;

public class FisherExactTest implements StatisticalTest {

	@Override
	public String getName() {
		return "Fisher's Exact Test";
	}

	@Override
	public double test(double[][] table, boolean one_sided) throws MathException {
		
		double a_case = table[0][1];
		double A_case = table[0][0];
		double a_control = table[1][1];
		double A_control = table[1][0];
		
		double p_val = 0;
		
		if(one_sided) {
			if(A_control > a_control)
				return fisherTestOneSided(A_case, a_case, A_control, a_control);
			else
				return fisherTestOneSided(a_case, A_case, a_control, A_control);
		} else {
			if(A_control > a_control) {
				double observed = fisherCurrentConfig(A_case, a_case, A_control, a_control);
				p_val = fisherTestOneSided(A_case, a_case, A_control, a_control);
				return p_val + fisherTestTwoSided(A_case+1, a_case-1, A_control-1, a_control+1, observed);
			} else {
				double observed = fisherCurrentConfig(a_case, A_case, a_control, A_control);
				p_val = fisherTestOneSided(a_case, A_case, a_control, A_control);
				return p_val + fisherTestTwoSided(a_case+1, A_case-1, a_control-1, A_control+1, observed);
			}
		}
	}
	
	private double fisherCurrentConfig(double A_case, double a_case, double A_control, double a_control) {
		double N_case = A_case + a_case;
		double N_control = A_control + a_control;
		
		double N_A = A_case + A_control;
		double N_a = a_case + a_control;
		
		double N = a_case + A_case + a_control + A_control;
		
		double topTerm = logFac10(N_case) + logFac10(N_control) + logFac10(N_A) +logFac10(N_a);
		double bottomTerm = logFac10(N) + logFac10(A_case) + logFac10(a_case) + logFac10(A_control) + logFac10(a_control);
		
		double p = Math.pow(10, topTerm - bottomTerm);
		
		return p;
	}
	
	private double fisherTestOneSided(double A_case, double a_case, double A_control, double a_control) {
		double p = fisherCurrentConfig(A_case, a_case, A_control, a_control);
		if(A_case > 0) {
			return p + fisherTestOneSided(A_case-1, a_case+1, A_control+1, a_control-1);
		} else {
			return p;
		}
	}
	
	private double fisherTestTwoSided(double A_case, double a_case, double A_control, double a_control, double observed) {
		double p = fisherCurrentConfig(A_case, a_case, A_control, a_control);
		
		if(p >= observed)
			p = 0;
		
		if(a_case > 0) {
			return p + fisherTestTwoSided(A_case+1, a_case-1, A_control-1, a_control+1, observed);
		} else {
			return p;
		}
	}
	
	private double logFac10(double value) {
		double logFac = 0;
		for(double i = value; i > 1; i--) {
			double log = Math.log10(i);
			logFac += log;
		}
		return logFac;
	}
	
//	public static void main(String[] args) throws MathException {
//		FisherExactTest t = new FisherExactTest();
//		
//		double[][] table = new double[][]{{2,3},{6,4}};
//		double p = t.test(table, true);
//		
//		System.out.println(p);
//	}
}
