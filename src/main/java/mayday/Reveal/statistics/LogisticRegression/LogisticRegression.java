package mayday.Reveal.statistics.LogisticRegression;

import mayday.Reveal.statistics.StatisticalTest;

import org.apache.commons.math.MathException;

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
