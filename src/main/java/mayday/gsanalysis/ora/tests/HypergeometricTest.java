package mayday.gsanalysis.ora.tests;

import mayday.core.math.Binomial;
import mayday.gsanalysis.Enrichment;
import mayday.gsanalysis.ora.GenesetTest;

public class HypergeometricTest extends GenesetTest{

	public HypergeometricTest() {
		super();
	}

	@Override
	public void runTest(int n11, int n12, int n21, int n22,Enrichment e) {
		int n_1=n11+n21;
		int n_2=n12+n22;
		int n1_=n11+n12;
		int n2_=n21+n22;
		int x=n11;
		double testScore=hypergeometricValue(n_1,n_2,n1_,n2_,x);
		//long time1=System.currentTimeMillis();
		
		//calculation of p-value
		double sum=0;
		for(int i=0;i<x;i++) {
			sum+=hypergeometricValue(n_1,n_2,n1_,n2_,i);
		}
		double pValue=Math.max(1-sum,0);
		
		//long time2= System.currentTimeMillis();
		e.setPValue(pValue);
		e.setScore(testScore);
		//long time3=System.currentTimeMillis();
		
		//System.out.println("1: " + x + " " + max + " " + (time2-time1));
		
	}
	
	//input: row and column sums of the contingency table, upper left element of the table
	public double hypergeometricValue(int n_1, int n_2, int n1_, int n2_,int x) {
		if((n_1+n_2 != n1_+n2_) || n_1<0  || n_2<0 || n1_<0 || n2_<0 || x<0) {
			System.out.println(n_1 + " " + n_2 + " " + n1_ + " " + n2_ + " " + x);
			throw new IllegalArgumentException("Error in row or column sums or x");
		}
		if(x>Math.min(n_1,n1_)||(n1_-x>n_2)) {
			return 0;
		}
		double numerator=Binomial.logBinomial(n_1,x) + Binomial.logBinomial(n_2,n1_-x);
		double denominator=Binomial.logBinomial(n1_+n2_,n1_);
		return Math.exp(numerator-denominator);
		
		
	}

	
	public String toString() {
		return "Hypergeometric test";
	}
	
	
	
	
}
