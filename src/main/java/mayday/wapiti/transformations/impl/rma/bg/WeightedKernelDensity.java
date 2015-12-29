package mayday.wapiti.transformations.impl.rma.bg;

import java.util.Arrays;


public class WeightedKernelDensity {
	/*
	 * discretize the data, but have modified algorithm to put weights on each
	 * observation
	 * 
	 * double[] x - the data int x - length of x double[] w - weight for each
	 * one of x, length is nx double xlow - minimum value in x dimension double
	 * xhigh - maximum value in x dimension double[] y - on output will contain
	 * descretation scheme of data int ny - length of y
	 */

	public static void weightedMassdist(double[] x, int nx, double[] w, double xlow,
			double xhigh, double[] y, int ny) {
		double fx, xdelta, xmass, xpos;
		int ix, ixmax, ixmin;

		ixmin = 0;
		ixmax = ny - 2;
		xmass = 0.0;
		xdelta = (xhigh - xlow) / (ny - 1);

		for (int i = 0; i < ny; i++) {
			y[i] = 0.0;
		}

		for (int i = 0; i < nx; i++) {
			xmass += w[i];
		}

		xmass = 1.0 / xmass;
		System.out.println("xmass: " + xmass);

		for (int i = 0; i < nx; i++) {
			if (!Double.isInfinite(x[i]) && !Double.isNaN(x[i])) {
				xpos = (x[i] - xlow) / xdelta;
				ix = (int) Math.floor(xpos);
				fx = xpos - ix;
				if (ixmin <= ix && ix <= ixmax) {
					y[ix] += w[i] * (1 - fx);
					y[ix + 1] += w[i] * fx;
				} else if (ix == -1) {
					y[0] += w[i] * fx;
				} else if (ix == ixmax + 1) {
					y[ix] += w[i] * (1 - fx);
				}
			}
		}

		for (int i = 0; i < ny; i++) {
			y[i] *= xmass;
		}
	}

	/*
	 * discretize the data, does not put user defined weights on each
	 * observation
	 * 
	 * double[] x - the data int nx - length of x double[] w - weight for each
	 * one of x, length is nx double xlow - minimum value in x dimension double
	 * xhigh - maximum value in x dimension double[] y on output will contain
	 * discretation scheme of data int ny - length of y
	 */
	public static void unweightedMassdist(double[] x, int nx, double xlow,
			double xhigh, double[] y, int ny) {
		double fx, xdelta, xpos, xmi;
		int ix, ixmax, ixmin;

		ixmin = 0;
		ixmax = ny - 2;
		xdelta = (xhigh - xlow) / (ny - 1);
		xmi = 1.0 / (double) nx;

		for (int i = 0; i < ny; i++) {
			y[i] = 0.0;
		}

		for (int i = 0; i < nx; i++) {
			if (!Double.isInfinite(x[i]) && !Double.isNaN(x[i])) {
				xpos = (x[i] - xlow) / xdelta;
				ix = (int) Math.floor(xpos);
				fx = xpos - ix;
				if (ixmin <= ix && ix <= ixmax) {
					y[ix] += (1 - fx) * xmi;
					y[ix + 1] += fx * xmi;
				} else if (ix == -1) {
					y[0] += fx * xmi;
				} else if (ix == ixmax + 1) {
					y[ix] += (1 - fx) * xmi;
				}
			}
		}
	}

	public static void fftDensityConvolve(double[] y, double[] kords, int n) {
		Complex[] yComp = new Complex[n];
		Complex[] kordsComp = new Complex[n];
		for (int i = 0; i < n; i++) {
			yComp[i] = new Complex(y[i], 0.0);
			kordsComp[i] = new Complex(kords[i], 0.0);
		}

		Complex[] conv = FFT.conjconvolve(yComp, kordsComp);

		for (int i = 0; i < n; i++) {
			kords[i] = conv[i].re();
		}
	}

	public static void kernelize(double[] kords, int n, double bw) {
		double a = bw * Math.sqrt(5.0);
		for (int i = 0; i < n; i++) {
			if (Math.abs(kords[i]) < a) {
				kords[i] = (3.0 / 4.0)
						* (1.0 - (Math.abs(kords[i]) / a)
								* (Math.abs(kords[i]) / a)) / a;
			} else {
				kords[i] = 0.0;
			}
		}
	}

	/*
	 * compute the standard deviation of a data vector
	 */
	public static double computeSD(double[] x, int length) {
		double sum = 0.0, sum2 = 0.0;
		for (int i = 0; i < length; i++) {
			sum += x[i];
		}

		sum = sum / (double) length;

		for (int i = 0; i < length; i++) {
			sum2 += (x[i] - sum) * (x[i] - sum);
		}

		return Math.sqrt(sum2 / (double) (length - 1));
	}

	/*
	 * compute the kernel bandwidth
	 * 
	 * double[] x - data vector, int length - length of x, double iqr - IQR of x
	 */
	public static double bandwidth(double[] x, int length, double iqr) {
		double hi, lo;
		hi = computeSD(x, length);

		if (hi > iqr) {
			lo = iqr / 1.34;
		} else {
			lo = hi;
		}

		if (lo == 0) {
			if (hi != 0) {
				lo = hi;
			} else if (Math.abs((double) x[1]) != 0.) {
				lo = Math.abs((double) x[1]);
			} else {
				lo = 1.0;
			}
		}

		return (0.9 * lo * Math.pow((double) length, -0.2));
	}

	/*
	 * linear interpolate v given x and y
	 */
	public static double linearInterpolateHelper(double v, double[] x, double[] y,
			int n) {
		int i, j, ij;

		i = 0;
		j = n - 1;

		if (v < x[i])
			return y[0];
		if (v > x[j])
			return y[n - 1];

		/* find the correct interval by bisection */
		while (i < j - 1) { /* x[i] <= v <= x[j] */
			ij = (i + j) / 2; /* i+1 <= ij <= j-1 */
			if (v < x[ij])
				j = ij;
			else
				i = ij;
			/* still i < j */
		}
		/* interpolation */
		if (v == x[j])
			return y[j];
		if (v == x[i])
			return y[i];
		/* impossible: if(x[j] == x[i]) return y[i]; */
		return y[i] + (y[j] - y[i]) * ((v - x[i]) / (x[j] - x[i]));
	}

	/*
	 * given x and y, interpolate linearly at xout putting the results in yout
	 */
	public static void linearInterpolate(double[] x, double[] y, double[] xout,
			double[] yout, int length) {
		for (int i = 0; i < length; i++) {
			yout[i] = linearInterpolateHelper(xout[i], x, y, length);
		}
	}

	/*
	 * the following function assumes that data (x) is sorted.
	 */

	public static double IQR(double[] x, int length) {
		double lowindex, highindex;
		double lowfloor, highfloor;
		double lowceil, highceil;
		double lowH, highH;

		double qslow, qshigh;

		lowindex = (double) (length - 1) * 0.25;
		highindex = (double) (length - 1) * 0.75;

		lowfloor = Math.floor(lowindex);
		highfloor = Math.floor(highindex);

		lowceil = Math.ceil(lowindex);
		highceil = Math.ceil(highindex);

		qslow = x[(int) lowfloor];
		qshigh = x[(int) highfloor];

		lowH = lowindex - lowfloor;
		highH = highindex - highfloor;

		if (lowH > 1e-10) {
			qslow = (1.0 - lowH) * qslow + lowH * x[(int) lowceil];
		}
		if (highH > 1e-10) {
			qshigh = (1.0 - highH) * qshigh + highH * x[(int) highceil];
		}

		return qshigh - qslow;
	}

	/*
	 * implements Rs density function with n=nout and kernel="epanechnikov".
	 */
	public static void kernelDensity(double[] x, int nxxx, double[] output,
			double[] outputX, int nout) {
		int nx = nxxx;
		int n = nout;
		int n2 = 2 * n;

		double lo, up, iqr, bw, from, to;
		double[] kords = new double[n2];
		double[] buffer = x;
		double[] y = new double[n2];
		double[] xords = new double[n];
		double cut = 3.;

		Arrays.sort(buffer);
		// sort(buffer, buffer + nx);

		iqr = IQR(buffer, nx);
		bw = bandwidth(x, nx, iqr);

		from = buffer[0] - cut * bw;
		to = buffer[nx - 1] + cut * bw;

		lo = from - 4 * bw;
		up = to + 4 * bw;

		unweightedMassdist(x, nx, lo, up, y, n);

		kords = calculateKords(up, lo, n);

		kernelize(kords, kords.length, bw);
		fftDensityConvolve(y, kords, n2);

		seqInt(xords, lo, up, n);
		seqInt(outputX, from, to, n);

		// to get the results that agree with R, we need to do linear
		// interpolation
		linearInterpolate(xords, kords, outputX, output, n);
	}

	/*
	 * kords <- seq.int(0, 2*(up-lo), length.out = 2L * n) 
	 * kords[(n + 2):(2 * n)] <- -kords[n:2]
	 */
	public static double[] calculateKords(double up, double lo, int n) {
		double[] kords = new double[2 * n];
		seqInt(kords, 0, 2 * (up - lo), 2 * n);
		for (int i = n + 1; i < 2 * n; i++) {
			kords[i] = -1.0 * kords[2 * n - i];
		}
		return kords;
	}

	/*
	 * seq.int function as in R
	 */
	public static double[] seqInt(double[] data, double from, double to, int length) {
		assert (length == data.length);
		for (int i = 0; i < length; i++) {
			data[i] = (double) i / (double) (length - 1) * (to - from) + from;
		}
		return data;
	}
}
