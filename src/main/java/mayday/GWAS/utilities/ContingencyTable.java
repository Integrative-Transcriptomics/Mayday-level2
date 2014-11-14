package mayday.GWAS.utilities;

import java.util.ArrayList;

import mayday.GWAS.data.Haplotypes;
import mayday.GWAS.data.HaplotypesList;
import mayday.GWAS.data.Subject;

public class ContingencyTable {
	/*
	 * returns a 2x2 contigency table in the following format
	 * 
	 * cases:		#A	#a
	 * controls:	#A	#a
	 * 
	 * A is the major allele
	 */
	public static double[][] get2x2ContingencyTable(ArrayList<Subject> cases, ArrayList<Subject> controls, HaplotypesList haplotypes, int snpIndex) {
		int A_case = 0, a_case = 0;
		int A_control = 0, a_control = 0;
		
		int countA_cases = 0, countB_cases = 0;
		int countA_controls = 0, countB_controls = 0;
		
		char major = 0;
		
		//compute allele frequencies for case group
		for(int i = 0; i < cases.size(); i++) {
			Subject subject = cases.get(i);
			Haplotypes hts = haplotypes.get(subject.getIndex());
			char A = hts.getSNPA(snpIndex);
			char B = hts.getSNPB(snpIndex);
			
			/*
			 * define one of the alleles as major
			 * (this may change later)
			 */
			if(i == 0) {
				major = A;
			}
			
			if(A != B) {
				countA_cases++;
				countB_cases++;
			} else if(A == major) {
				countA_cases++;
			} else {
				countB_cases++;
			}
		}
		
		for(int i = 0; i < controls.size(); i++) {
			Subject subject = controls.get(i);
			Haplotypes hts = haplotypes.get(subject.getIndex());
			char A = hts.getSNPA(snpIndex);
			char B = hts.getSNPB(snpIndex);
			
			/*
			 * define one of the alleles as major
			 * (this may change later)
			 */
			if(i == 0) {
				major = A;
			}
			
			if(A != B) {
				countA_controls++;
				countB_controls++;
			} else if(A == major) {
				countA_controls++;
			} else {
				countB_controls++;
			}
		}
		
		/*
		 * the major allele is the one with higher frequency in the control group!
		 * (switch if necessary)
		 */
		if(countA_controls >= countB_controls) {
			A_control = countA_controls;
			a_control = countB_controls;
			A_case = countA_cases;
			a_case = countB_cases;
		} else {
			A_control = countB_controls;
			a_control = countA_controls;
			A_case = countB_cases;
			a_case = countA_cases;
		}
		
		//create table and return
		double[][] table = new double[][]{{A_case, a_case},{A_control, a_control}};
		return table;
	}
	
	/*
	 * returns a 2x3 contingency table in the following format
	 * cases:		#AA	#Aa	#aa
	 * controls:	#AA	#Aa	#aa
	 * 
	 * A is the major allele
	 */
	public static double[][] get2x3ContingencyTable(ArrayList<Subject> cases, ArrayList<Subject> controls, HaplotypesList haplotypes, int snpIndex) {
		int AA_case = 0, AA_control = 0;
		int Aa_case = 0, Aa_control = 0;
		int aa_case = 0, aa_control = 0;
		
		int countAA_cases = 0, countAB_cases = 0, countBB_cases = 0;
		int countAA_controls = 0, countAB_controls = 0, countBB_controls = 0;
		
		char major = 0;
		
		//count allele frequencies for the case group
		for(int i = 0; i < cases.size(); i++) {
			Subject subject = cases.get(i);
			Haplotypes hts = haplotypes.get(subject.getIndex());
			char A = hts.getSNPA(snpIndex);
			char B = hts.getSNPB(snpIndex);
			
			/*
			 * define one of the alleles as major allele
			 * this may change later!
			 */
			if(i == 0) { 
				major = A;
			}
			
			if(A != B) {
				countAB_cases++;
			} else if(A == major) {
				countAA_cases++;
			} else {
				countBB_cases++;
			}
		}
		
		//count allele frequencies for the control group
		for(int i = 0; i < controls.size(); i++) {
			Subject subject = controls.get(i);
			Haplotypes hts = haplotypes.get(subject.getIndex());
			char A = hts.getSNPA(snpIndex);
			char B = hts.getSNPB(snpIndex);
			
			/*
			 * define one of the alleles as major allele
			 * this may change later!
			 */
			if(i == 0) { 
				major = A;
			}
			
			if(A != B) {
				countAB_controls++;
			} else if(A == major) {
				countAA_controls++;
			} else {
				countBB_controls++;
			}
		}
		
		/*
		 * the major allele is the one with higher frequency in the control group!
		 * (switch if necessary)
		 */
		if(countAA_cases >= countBB_cases) {
			AA_control = countAA_controls;
			Aa_control = countAB_controls;
			aa_control = countBB_controls;
			AA_case = countAA_cases;
			Aa_case = countAB_cases;
			aa_case = countBB_cases;
		} else {
			AA_control = countBB_controls;
			Aa_control = countAB_controls;
			aa_control = countAA_controls;
			AA_case = countBB_cases;
			Aa_case = countAB_cases;
			aa_case = countAA_cases;
		}
		
		//create table and return
		double[][] table = new double[][]{{AA_case, Aa_case, aa_case},{AA_control, Aa_control, aa_control}}; 
		
		return table;
	}
}
