package mayday.gsanalysis.ora.tests;

import mayday.core.math.Binomial;
import mayday.gsanalysis.Enrichment;
import mayday.gsanalysis.ora.GenesetTest;

public class BinomialTest extends GenesetTest {

	public BinomialTest() {
		super();
	}

	@Override
	public void runTest(int n11, int n12, int n21, int n22,Enrichment e) {
		int n=n11+n12;
		double p=((double)(n11+n21))/((double)(n11+n12+n21+n22));
		int x=n11;
		double testScore=binomialValue(p,n,x);
		
		//calculation of p-value
		double sum=0;
		for(int i=0;i<x;i++) {
			sum+=binomialValue(p,n,i);
		}
		double pValue=Math.max(1-sum,0);
		
		e.setPValue(pValue);
		e.setScore(testScore);
		
	}
	
	public double binomialValue(double p, int n, int x) {
		if(x<0 || n<0 || p<0  || p>1) {
			throw new IllegalArgumentException("Illegal input values");
		}
		if(n<x) {
			return 0;
		}
		return Binomial.binomial(n, x) * Math.pow(p, x) * Math.pow(1-p, n-x);
	}
	
	public String toString() {
		return "Binomial test";
	}
		
}
