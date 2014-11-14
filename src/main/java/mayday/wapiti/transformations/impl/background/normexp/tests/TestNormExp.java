package mayday.wapiti.transformations.impl.background.normexp.tests;

import static org.junit.Assert.assertEquals;
import mayday.core.math.DNorm;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.wapiti.transformations.impl.background.normexp.NormExp;
import mayday.wapiti.transformations.impl.background.normexp.NormExp.Parameter;

import org.junit.Test;

public class TestNormExp {
	//Test normExp with method = RMA
	@Test
	public void testNormExpRMA() throws Exception{
		AbstractVector x = new DoubleVector(10);
		fillX(x);
		AbstractVector result = NormExp.normExp(x, NormExp.RMA, null);
		
		assertEquals(67.561424, result.get(0));
		assertEquals(35.561616, result.get(1));
		assertEquals(5.937081, result.get(2));
		assertEquals(4.159845, result.get(3));
		assertEquals(4.360797, result.get(4));
		assertEquals(79.561424, result.get(5));
		assertEquals(19.732171, result.get(6));
		assertEquals(11.366076, result.get(7));
		assertEquals(12.068463, result.get(8));
		assertEquals(7.034195, result.get(9));
	}
	
	//Test normExp with method = RMA
	@Test
	public void testNormExpSaddle() throws Exception{
		AbstractVector x = new DoubleVector(10);
		fillX(x);
		AbstractVector result = NormExp.normExp(x, NormExp.SADDLE, null);
		
		assertEquals(7.600001e+01, result.get(0));
		assertEquals(4.400001e+01, result.get(1));
		assertEquals(7.000007e+00, result.get(2));
		assertEquals(6.597896e-06, result.get(3));
		assertEquals(1.000007e+00, result.get(4));
		assertEquals(8.800001e+01, result.get(5));
		assertEquals(2.800001e+01, result.get(6));
		assertEquals(1.800001e+01, result.get(7));
		assertEquals(1.900001e+01, result.get(8));
		assertEquals(1.000001e+01, result.get(9));
	}
	
	@Test
	public void testFit(){
		AbstractVector x = new DoubleVector(10);
		fillX(x);
		Parameter out = NormExp.fit(x, NormExp.SADDLE);
		assertEquals(5.999993, out.mu);
		assertEquals(-16.116477, out.sigma);
		assertEquals(3.370942, out.alpha);
	}
	
	@Test
	public void testDNorm(){
		double d = DNorm.dnorm(100, 10, 5, false);
		assertEquals(3.517499e-72, d);
	}
	
	private void fillX(AbstractVector x) {
		x.set(0, 82);
		x.set(1, 50);
		x.set(2, 13);
		x.set(3, 6);
		x.set(4, 7);
		x.set(5, 94);
		x.set(6, 34);
		x.set(7, 24);
		x.set(8, 25);
		x.set(9, 16);
	}
}
