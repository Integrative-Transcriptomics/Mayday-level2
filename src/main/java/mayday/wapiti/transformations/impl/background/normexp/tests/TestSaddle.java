package mayday.wapiti.transformations.impl.background.normexp.tests;

import static org.junit.Assert.*;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.wapiti.transformations.impl.background.normexp.Saddle;

import org.junit.Test;

public class TestSaddle {
	@Test
	public void testQuantile(){
		AbstractVector x = new DoubleVector(10);
		fillX(x);
		double[] qs = Saddle.quantile(x, null);
		//test length
		assertEquals(4, qs.length);
		//testValues
		assertEquals(6.0, qs[0]);
		assertEquals(6.45, qs[1]);
		assertEquals(6.90, qs[2]);
		assertEquals(94.0, qs[3]);
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
