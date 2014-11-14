package mayday.gsanalysis.ora.tests;

import mayday.core.math.Binomial;
import mayday.gsanalysis.Enrichment;
import mayday.gsanalysis.ora.GenesetTest;

public class FisherTest extends GenesetTest{

	public FisherTest() {
	}
	
	@Override
	public void runTest(int n11, int n12, int n21, int n22,Enrichment e) {
		int n_1=n11+n21;
		int n1_=n11+n12;
		int n2_=n21+n22;
		
		double testScore = fisherValue(n11,n12,n21,n22);
		
		//calculation of p-value
		double sum=0;
		for(int n11new=0;n11new<=Math.min(n_1, n1_);n11new++) {
			int n12new=n1_-n11new;
			int n21new=n_1-n11new;
			int n22new=n2_-n21new;
			double currentValue=fisherValue(n11new,n12new,n21new,n22new);
			if(currentValue<=testScore) {
				sum+=currentValue;
			}
		}
		double pValue=Math.min(sum,1);
		e.setPValue(pValue);
		e.setScore(testScore);;
	}
	
	public double fisherValue(int n11, int n12, int n21, int n22) {
		double numerator=Binomial.logFactorial(n11+n12)+Binomial.logFactorial(n21+n22)+Binomial.logFactorial(n11+n21)+Binomial.logFactorial(n12+n22);
		double denominator=Binomial.logFactorial(n11+n12+n21+n22)+Binomial.logFactorial(n11)+Binomial.logFactorial(n12)+Binomial.logFactorial(n21)+Binomial.logFactorial(n22);
		return Math.exp(numerator-denominator);
	}
	
	
	public String toString() {
		return "Fisher test";
	}
	
	
	

}
