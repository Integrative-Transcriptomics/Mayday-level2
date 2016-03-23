package mayday.Reveal.statistics.CochranArmitageTrendTest;

import mayday.Reveal.statistics.StatisticalTest;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

public class CochranArmitageTrendTest implements StatisticalTest {
	
	/*
	 * the armitage trend test allows for three different scoring systems
	 */
	public static final int CO_DOMINANT_SCORING = 0;
	public static final int DOMINANT_SCORING = 1;
	public static final int RECESSIVE_SCORING = 2;
	
	private int scoringSystem;
	
	//default scoring system is co-dominant
	public CochranArmitageTrendTest() {
		this.scoringSystem = CO_DOMINANT_SCORING;
	}
	
	//user defined scoring system
	public CochranArmitageTrendTest(int scoring) {
		this.scoringSystem = scoring;
	}

	/*
	 * the table is assumed to be a 2x3 contingency table
	 */
	@Override
	public double test(double[][] table, boolean one_sided) throws Exception {
		/*
		 * check wether there is a difference in allele frequency
		 * if there is no difference, then p = 1
		 * and no test is necessary
		 */
		if(!checkAlleleFrequencies(table)) {
			return 1;
		}
		
		//row sums
		double N_cases = sum(table[0]);
		double N_controls = sum(table[1]);
		
		//col sums
		double N_AA = table[0][0] + table[1][0];
		double N_Aa = table[0][1] + table[1][1];
		double N_aa = table[0][2] + table[1][2];
		
		//overall sum
		double N = N_cases + N_controls;
		
		double x_AA, x_Aa, x_aa;
		
		switch(scoringSystem) {
		case CO_DOMINANT_SCORING:
			x_AA = 0;
			x_Aa = 1;
			x_aa = 2;
			break;
		case DOMINANT_SCORING:
			x_AA = 0;
			x_Aa = 1;
			x_aa = 1;
			break;
		case RECESSIVE_SCORING:
			x_AA = 0;
			x_Aa = 0;
			x_aa = 1;
			break;
		default: //co-dominant scoring
			x_AA = 0;
			x_Aa = 1;
			x_aa = 2;
		}
		
		double p_value = 0;
		
		double AA_case = table[0][0];
		double Aa_case = table[0][1];
		double aa_case = table[0][2];
		double AA_control = table[1][0];
		double Aa_control = table[1][1];
		double aa_control = table[1][2];
		
		double U = (1d / N) 
				*(x_AA * (N_controls * AA_case - N_cases * AA_control) 
				+ x_Aa * (N_controls * Aa_case - N_cases * Aa_control)
				+ x_aa * (N_controls * aa_case - N_cases * aa_control));
		
		double varU = ((N_cases * N_controls) / Math.pow(N,3))
				* ((N * (  x_AA*x_AA*N_AA
						+ x_Aa*x_Aa*N_Aa
						+ x_aa*x_aa*N_aa))
				- (Math.pow((x_AA*N_AA + x_Aa*N_Aa + x_aa*N_aa), 2)));
		
//		System.out.println("U " + U);
//		System.out.println("VarU " + varU);
		
		double Z_T = U / Math.sqrt(varU);
		
//		System.out.println("Z_U " + Z_T);
//		System.out.println("Z_U^2 " + Z_T*Z_T);
		
		if(one_sided) {
			NormalDistribution dist = new NormalDistribution(0, 1);
			//one-sided alternative, that the minor allele is positively associated with the disease of interest
			p_value = 1 - dist.cumulativeProbability(Z_T);
			
			System.out.println(p_value);
			
		} else {
			ChiSquaredDistribution dist = new ChiSquaredDistribution(1);
			//two-sided alternative
			p_value = 1 - dist.cumulativeProbability(Math.pow(Z_T,2));
		}
		
		return p_value;
	}
	
	private boolean checkAlleleFrequencies(double[][] table) {
		double AA_case = table[0][0];
		double Aa_case = table[0][1];
		double aa_case = table[0][2];
		
		double AA_control = table[1][0];
		double Aa_control = table[1][1];
		double aa_control = table[1][2];
		
		//all cases are AA and all controls are AA
		if(AA_case != 0 && Aa_case == 0 && aa_case == 0) {
			if(AA_control != 0 && Aa_control == 0 && aa_control == 0) {
				return false;
			}
		}
		
		//all cases are Aa and all controls are Aa
		if(AA_case == 0 && Aa_case != 0 && aa_case == 0) {
			if(AA_control == 0 && Aa_control != 0 && aa_control == 0) {
				return false;
			}
		}
		
		/*
		 * all controls are aa cannot happen since 'a' would then be the major allele
		 * and the first case would hold!
		 */
		
		return true;
	}
	
	//calculate sum of vector elements
	private double sum(double[] values) {
		double sum = 0;
		for(double v : values)
			sum+=v;
		return sum;
	}
	
	//define the scoring system that should be used for the statistical test
	public void setScoringSystem(int scoring) {
		this.scoringSystem = scoring;
	}

	@Override
	public String getName() {
		return "Cochran-Armitage Trend Test";
	}
//	
//	public static void main(String[] args) throws MathException {
//		CochranArmitageTrendTest test = new CochranArmitageTrendTest(CochranArmitageTrendTest.CO_DOMINANT_SCORING);
//		boolean one_sided = false;
//		double[][] table = {{90,70,90},{26,26,23}};
//		test.test(table, one_sided);
//	}
}
