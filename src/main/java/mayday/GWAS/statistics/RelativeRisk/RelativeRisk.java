package mayday.GWAS.statistics.RelativeRisk;

import mayday.GWAS.statistics.StatisticalTest;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.ChiSquaredDistribution;
import org.apache.commons.math.distribution.ChiSquaredDistributionImpl;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

public class RelativeRisk implements StatisticalTest {

	@Override
	public double test(double[][] table, boolean one_sided) throws MathException {
		
		double a_case = table[0][1];
		double A_case = table[0][0];
		double a_control = table[1][1];
		double A_control = table[1][0];
		
		double N1 = a_case + A_case;
		double N0 = a_control + A_control;
		
		//proportion of the minor allele in the case group
		double p1 = a_case / A_case;
		//proportion of the minor allele in the control group
		double p0 = a_control / A_control;
		//calculate the relative risk
		//to avoid skewness in the asymptotic distribution of p, the log(p) is considered
		double log_p = Math.log(p1 / p0);
		//estimated standard error of log_p
		double se_log_p = Math.sqrt(((1-p1)/(N1*p1)) + ((1-p0)/(N0*p0)));
		//test statistic
		double Z = log_p / se_log_p;
		
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

	@Override
	public String getName() {
		return "Relative Risk";
	}
}
