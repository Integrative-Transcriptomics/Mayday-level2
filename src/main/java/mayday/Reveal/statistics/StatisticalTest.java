package mayday.Reveal.statistics;

import org.apache.commons.math.MathException;

public interface StatisticalTest {

	public String getName();
	
	public double test(double[][] table, boolean one_sided) throws MathException;
	
}
