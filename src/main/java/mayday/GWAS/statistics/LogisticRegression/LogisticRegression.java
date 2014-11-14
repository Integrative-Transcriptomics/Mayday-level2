package mayday.GWAS.statistics.LogisticRegression;

import org.apache.commons.math.MathException;

import mayday.GWAS.statistics.StatisticalTest;

public class LogisticRegression implements StatisticalTest {

	@Override
	public String getName() {
		return "Logistic Regression";
	}

	@Override
	public double test(double[][] table, boolean one_sided) throws MathException {
		//TODO
		return 0;
	}
}
