package mayday.GWAS.statistics.HWE;

import java.util.Arrays;

import org.apache.commons.math.MathException;

import mayday.GWAS.statistics.StatisticalTest;

/**
 * Exact test for HWE based on Wigginton et al. 2005
 * @author GJ
 *
 */
public class HWETest implements StatisticalTest {

	@Override
	public String getName() {
		return "HWE";
	}

	@Override
	public double test(double[][] table, boolean one_sided) throws MathException {
		double AA_case = table[0][0];
		double Aa_case = table[0][1];
		double aa_case = table[0][2];
		double AA_control = table[1][0];
		double Aa_control = table[1][1];
		double aa_control = table[1][2];
		
		double AA = AA_case + AA_control;
		double Aa = Aa_case + Aa_control;
		double aa = aa_case + aa_control;
		
		int obs_hom1 = AA >= aa ? (int)Math.rint(AA) : (int)Math.rint(aa);
		int obs_hom2 = AA < aa ? (int)Math.rint(AA) : (int)Math.rint(aa);
		int obs_hets = (int)Math.rint(Aa);
		
		int n1 = 2 * obs_hom1 + obs_hets;
		
		if(n1 == 0) {
			return 1;
		}
		
		int obs_homc = obs_hom1 < obs_hom2 ? obs_hom2 : obs_hom1;
		int obs_homr = obs_hom1 < obs_hom2 ? obs_hom1 : obs_hom2;
		
		int rare_copies = 2 * obs_homr + obs_hets;
		int genotypes = obs_hets + obs_homc + obs_homr;
		
		double[] het_probs = new double[(int)rare_copies + 1];
		Arrays.fill(het_probs, 0.);
	
		int mid = rare_copies * (2 * genotypes - rare_copies) / (2 * genotypes);
		
		//check to ensure that midpoint and rare alleles have same parity
		int check = (rare_copies & 1) ^ (mid & 1); 
		if(check == 1) {
			mid++;
		}
		
		int curr_hets = mid;
		int curr_homr = (rare_copies - mid) / 2;
		int curr_homc = genotypes - curr_hets - curr_homr;
		
		het_probs[mid] = 1.0;
		double sum = het_probs[mid];
		
		for(curr_hets = mid; curr_hets > 1; curr_hets -= 2) {
			het_probs[curr_hets - 2] = het_probs[curr_hets] * curr_hets * (curr_hets - 1) / (4 * (curr_homr + 1) * (curr_homc + 1));
			sum += het_probs[curr_hets - 2];
			curr_homr++;
			curr_homc++;
		}
		
		curr_hets = mid;
		curr_homr = (rare_copies - mid) / 2;
		curr_homc = genotypes - curr_hets - curr_homr;
		
		for(curr_hets = mid; curr_hets <= rare_copies - 2; curr_hets +=2) {
			het_probs[curr_hets + 2] = het_probs[curr_hets] * 4 * curr_homr * curr_homc / ((curr_hets + 2) * (curr_hets + 1));
			sum += het_probs[curr_hets + 2];
			curr_homr--;
			curr_homc--;
		}
		
		for(int i = 0; i <= rare_copies; i++) {
			het_probs[i] /= sum;
		}
		
		double p_hwe = 0.;
		
		for(int i = 0; i <= rare_copies; i++) {
			if(het_probs[i] > het_probs[obs_hets])
				continue;
			p_hwe += het_probs[i];
		}
		
		p_hwe = p_hwe > 1.0 ? 1.0 : p_hwe;
		return p_hwe;
	}
}
