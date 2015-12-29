package mayday.gsanalysis.ora.tests;

import mayday.gsanalysis.Enrichment;
import mayday.gsanalysis.edu_mit_broad_genome_alg_ext.DhbStatistics.ChiSquareDistribution;
import mayday.gsanalysis.ora.GenesetTest;

public class ChiSquareTest extends GenesetTest{

	public ChiSquareTest() {
		super();
	}

	@Override
	public void runTest(int n11, int n12, int n21, int n22,Enrichment e) {
		double testScore = chiSquareValue(n11,n12,n21,n22);
		ChiSquareDistribution c = new ChiSquareDistribution(1);
		double pValue = 1-c.distributionValue(testScore);
		
		e.setPValue(pValue);
		e.setScore(testScore);
	}
	
	//formula given by Yates
	public double chiSquareValue(int n11, int n12, int n21, int n22) {
		double N=n11+n12+n21+n22;
		
		if((Math.abs(n11*n22 - n12*n21)-N/2 == 0) || (n11+n12==0) || (n21+n22==0) || (n11+n21==0) || (n12+n22==0)) {
			return 0;
		}
		
		double denominator=Math.log(n11+n12)+Math.log(n21+n22)+Math.log(n11+n21)+Math.log(n12+n22);
		double numerator=Math.log(N) + 2 * Math.log(Math.abs(Math.abs(n11*n22 - n12*n21)-N/2));
		return Math.exp(numerator-denominator);
	}
	
	public String toString() {
		return "Chi square test";
	}
	
//	public static void main(String[] args) {
//		ChiSquareTest f = new ChiSquareTest();
//		Enrichment e = f.runTest(new Geneset("a"),1, 7, 1495, 21276);
//		System.out.println(e.getPValue() + " " + e.getScore());
//		System.out.println(f.chiSquareValue(1, 7, 1495, 21276));
//	}

}
