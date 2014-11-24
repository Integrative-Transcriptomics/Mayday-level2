package mayday.Reveal.statistics.OddsRatio;

import mayday.Reveal.statistics.StatisticalTest;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.ChiSquaredDistribution;
import org.apache.commons.math.distribution.ChiSquaredDistributionImpl;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

public class OddsRatio implements StatisticalTest {

	@Override
	public double test(double[][] table, boolean one_sided) throws MathException {
		
		double a_case = table[0][1];
		double A_case = table[0][0];
		double a_control = table[1][1];
		double A_control = table[1][0];
		
		/*
		 * check wether there is a difference in allele frequency
		 * if there is no difference, then p = 1
		 * and no test is necessary
		 */
		if(!checkAlleleFrequencies(table)) {
			return 1;
		}
		
		//proportion of the minor allele in the case group
		double p1 = a_case / A_case;
		//proportion of the minor allele in the control group
		double p0 = a_control / A_control;
		//odds of the minor allele in the case group
		double odds1 = p1 / (1 - p1);
		//odds of the minor allele in the control group
		double odds0 = p0 / (1 - p0);
		//sample odds ratio
		//to avoid skewness of the asymptotic distribution of odds, the log(odds) is considered
		double log_odds = Math.log(odds1 / odds0);
		//estimated standard error of the log(odds)
		double se_log_odds = Math.sqrt(1/a_case + 1/A_case + 1/a_control + 1/A_control);
		//test statistic
		double Z = log_odds / se_log_odds;
		
		//the return value
		double p_value = 0;
				
		//one-sided test
		if(one_sided) {
			//for the one sided test the test statistic follows approximately the standard normal distribution
			//under the null hypothesis
			NormalDistribution std_norm_dist = new NormalDistributionImpl(0,1);
			//Prob(N(0,1) >= Z)
			p_value = 1 - std_norm_dist.cumulativeProbability(Z);
		} else { //two-sided test
			//for the two-sided case the test based on Z is equivalent to the test based on Z^2, which
			//approximately follows a chi^2 distribution with 1 degree of freedom
			ChiSquaredDistribution chiSq_dist = new ChiSquaredDistributionImpl(1);
			//Prob(Chi^2 >= Z^2)
			p_value = 1 - chiSq_dist.cumulativeProbability(Z*Z);
		}
		
		return p_value;
	}
	
	private boolean checkAlleleFrequencies(double[][] table) {
		double A_case = table[0][0];
		double a_case = table[0][1];
		
		double A_control = table[1][0];
		double a_control = table[1][1];
		
		//all cases are AA and all controls are AA
		if(A_case != 0 && a_case == 0) {
			if(A_control != 0 && a_control == 0) {
				return false;
			}
		}
		
		/*
		 * all controls are aa cannot happen since 'a' would then be the major allele
		 * and the first case would hold!
		 */
		
		return true;
	}

	@Override
	public String getName() {
		return "Odds Ratio";
	}
}
