package mayday.Reveal.statistics;

public interface StatisticalTest {

	public String getName();
	
	public double test(double[][] table, boolean one_sided) throws Exception;
	
}
