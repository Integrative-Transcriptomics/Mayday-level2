package mayday.Reveal.statistics.DifferenceOfProportions;

import mayday.Reveal.statistics.StatisticalTest;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.ChiSquaredDistribution;
import org.apache.commons.math.distribution.ChiSquaredDistributionImpl;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

public class DifferenceOfProportions implements StatisticalTest {

	@Override
	public double test(double[][] table, boolean one_sided) throws MathException {
		
		double a_case = table[0][1];
		double A_case = table[0][0];
		double a_control = table[1][1];
		double A_control = table[1][0];
		
		double N = a_case + a_control + A_case + A_control;
		
		//proportion of the minor allele in the case group
		double p1 = a_case / A_case;
		//proportion of the minor allele in the control group
		double p0 = a_control / A_control;
		//difference of sample proportions
		double d = p1 - p0;
		//pooled sample proportion of the minor allele under the null hypothesis
		double p = (a_case + a_control) / N;
		//estimated standard error of d
		double se_d = Math.sqrt((p*(1-p))/N);
		
		//test statistic
		double Z = d / se_d;
		
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
		return "Difference of Proportions";
	}
}
