package mayday.wapiti.transformations.impl.rma.bg;

import mayday.core.math.PNorm;

public class RMABackground {
	
	static double sqrt2 = Math.sqrt(2.0);
	static double sqrt2pi = Math.sqrt(2*Math.PI);
	static double sqrt2pi_inverse = 1.0/Math.sqrt(2*Math.PI);
	
	/*
	 * find the max of x
	 */
	protected static int findMaxIndex(double[] x, int length) {
		double max = x[0];
		int index = 0;
		for (int i = 0; i < length; i++) {
			if (x[i] > max) {
				max = x[i];
				index = i;
			}
		}
		return index;
	}

	/*
	 * estimate the sigma parameter given vector MM, value of maximum of density
	 * of MM, dimension of MM matrix and column of interest
	 */
	protected static double getSD(double[] MM, double MMmax, int rows, int cols,
			int column) {
		double tmpsum = 0.0;
		int numtop = 0;

		for (int i = 0; i < rows; i++) {
			int index = column * rows + i;
			if (MM[index] < MMmax) {
				double tmp = MM[index] - MMmax;
				tmpsum += tmp*tmp;
				numtop++;
			}
		}
		double sigma = Math.sqrt(tmpsum / (numtop - 1)) * sqrt2;
		return sigma;
	}

	/*
	 * 
	 */
	protected static double maxDensity(double[] z, int rows, int cols, int column) {
		int npts = 16384;
		double[] x = new double[rows];
		double[] densX = new double[npts];
		double[] densY = new double[npts];
		double maxX;
		int maxIndex;

		for (int i = 0; i < rows; i++) {
			x[i] = z[column * rows + i];
		}

		WeightedKernelDensity.kernelDensity(x, rows, densY, densX, npts);
		
		maxIndex = findMaxIndex(densY, npts);
		maxX = densX[maxIndex];

		return maxX;
	}

	/*
	 * Compute the standard normal distribution function mu = 0, sd = 1
	 */
	protected static double Phi(double x) {
		return PNorm.getDistribution(x, false);
	}

	/*
	 * compute the standard normal density
	 */
	protected static double phi(double x) {
		return sqrt2pi_inverse * Math.exp(-0.5 * x * x);
	}

	/*
	 * assume: param[0] = alpha, param[1] = mu, param[2] = sigma
	 */
	public static void bgAdjust(double[] PM, double[] param, int rows,
			int cols, int column) {
		double f1 = - param[1] - param[0] * param[2]* param[2];
		for (int i = 0; i < rows; i++) {	
			int index = column * rows + i;
			double a = PM[index] + f1;
			double f2 = a / param[2];
			PM[index] = a + param[2] * phi(f2) / Phi(f2);
		}
	}

	/*
	 * estimate the alpha parameter given vector PM value of maximum of density
	 * of PM, dimension of MM matrix and column of interest using method
	 * proposed in affy2
	 */
	protected static double getAlpha(double[] PM, double PMmax, int length) {
		for (int i = 0; i < length; i++) {
			PM[i] -= PMmax;
		}
		double alpha = maxDensity(PM, length, 1, 0);
		alpha = 1.0 / alpha;
		return alpha;
	}

	/*
	 * estimate the parameters for the background parameters estimates are the
	 * same as those given by affy in bg.correct.rma (Version 1.1 release of
	 * affy)
	 */
	public static void bgParameters(double[] PM, double[] param,
			int rows, int cols, int column) {
		double PMmax = maxDensity(PM, rows, cols, column);

		int nLess = 0, nMore = 0;
		double[] tmpLess = new double[rows];
		double[] tmpMore = new double[rows];

		for (int i = 0; i < rows; i++) {
			int index = column * rows + i;
			if (PM[index] < PMmax) {
				tmpLess[nLess] = PM[index];
				nLess++;
			}
		}

		PMmax = maxDensity(tmpLess, nLess, 1, 0);

		double sd = getSD(PM, PMmax, rows, cols, column);

		for (int i = 0; i < rows; i++) {
			int index = column * rows + i;
			if (PM[index] > PMmax) {
				tmpMore[nMore] = PM[index];
				nMore++;
			}
		}

		double alpha = getAlpha(tmpMore, PMmax, nMore);

		param[0] = alpha;
		param[1] = PMmax;
		param[2] = sd;
	}
}
