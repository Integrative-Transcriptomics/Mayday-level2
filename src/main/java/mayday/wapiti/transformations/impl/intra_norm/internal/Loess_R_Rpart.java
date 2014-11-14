package mayday.wapiti.transformations.impl.intra_norm.internal;

import mayday.core.math.Statistics;
import mayday.core.structures.linalg.vector.DoubleVector;

/** This is the loessFit function from R, but only for the case where no weights are supplied by the user.
 * Missing values and infinite values ARE NOT handled! */
public class Loess_R_Rpart {

	protected static double[] asDouble(int[] x) {
		double[] ret = new double[x.length];
		for (int i=0; i!=x.length; ++i)
			ret[i] = x[i];
		return ret;
	}

	protected static double range(double[] in) {
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		boolean hasVal=false;
		for (double d : in) {
			if (!Double.isNaN(d)) {
				min = Math.min(min, d);
				max = Math.max(max, d);
				hasVal=true;
			}
		}
		if (!hasVal)
			return Double.NaN; 
		return (max-min);			
	}

	protected static double[] applyOrder(double[] in , int[] order) {
		double[] ret = new double[in.length];
		for (int o=0; o!=order.length; ++o) {
			ret[o] = in[order[o]];
		}
		return ret;
	}

	/** Loess fit
	 * @param x independent variable
	 * @param y dependent variable to be fitted
	 * @return double[2][], with [0][] being the fitted values and [1][] being the residuals
	 */
	public static double[][] loessFit( double[] x, double[] y) {
		return loessFit(x,y, 0.3, 0.01, 4);
	}
	
	/** Loess fit
	 * @param x independent variable
	 * @param y dependent variable to be fitted
	 * @param span the size of the neighborhood to use
	 * @param iterations the number of iterations to perform
	 * @return double[2][], with [0][] being the fitted values and [1][] being the residuals
	 */	
	public static double[][] loessFit( double[] x, double[] y, double span, int iterations ) {
		return loessFit(x,y,span, 0.01, iterations);
	}

	protected static double[][] loessFit( double[] xobs, double[] yobs, double span, double bin, int iterations ) {
		int nobs = yobs.length;
		double[][] out = new double[2][];
		if (nobs > 0) {
			int[] o = Statistics.order(xobs);
			int[] oo = Statistics.order(asDouble(o));
			int iter = iterations-1;			
			double delta = bin * range(xobs);
			double[] ys = new double[nobs];
			double[] rw = new double[nobs];
			double[] res = new double[nobs];
			Loess_R_Cpart.lowess(
					applyOrder(xobs,o), applyOrder(yobs,o), nobs, span, iter, delta, ys, rw, res
			);
			double[] smoothy = applyOrder(ys, oo);
			out[0] = smoothy;
			DoubleVector tmp = new DoubleVector(yobs).clone();
			tmp.subtract(new DoubleVector(smoothy));
			out[1] = tmp.toArrayUnpermuted();			
		}
		return out;
	}

}
