package mayday.wapiti.transformations.impl.intra_norm.internal;

import mayday.core.math.Statistics;

/** This is a translation of the R base source file src/library/stats/src/lowess.c 
 * Changes are:
 * - moved from 1-based indexing to zero based
 * - lowest has a return type of bool instead of modifying a bool pointer
 * - I inferred parameter const'ness and modified functions accordingly, using ArrayFromC to emulate pointer arithmetic
 * **/

public class Loess_R_Cpart {

	private static double fsquare(double x)
	{
		return x * x;
	}

	private static double fcube(double x)
	{
		return x * x * x;
	}
	
	static boolean lowest(double[] x, double[] y, int n, double xs, ArrayFromC ys, int nleft, int nright, ArrayFromC w, boolean userw, double[] rw)
	{
		int nrt, j;
		double a, b, c, h, h1, h9, r, range;
		boolean ok;
		
		range = x[n-1]-x[0];
		h = Math.max(xs-x[nleft], x[nright]-xs);
		h9 = 0.999*h;
		h1 = 0.001*h;

		/* sum of weights */

		a = 0.;
		j = nleft;
		while (j < n) {

			/* compute weights */
			/* (pick up all ties on right) */

			w.set(j,0.);
			r = Math.abs(x[j] - xs);
			if (r <= h9) {
				if (r <= h1)
					w.set(j, 1.);
				else
					w.set(j, fcube(1.-fcube(r/h)));
				if (userw)
					w.mult(j, rw[j]);
				a += w.get(j);
			}
			else if (x[j] > xs)
				break;
			j = j+1;
		}

		/* rightmost pt (may be greater */
		/* than nright because of ties) */

		nrt = j-1;
		if (a <= 0.)
			ok = false;
		else {
			ok = true;

			/* weighted least squares */
			/* make sum of w[j] == 1 */

			for(j=nleft ; j<=nrt ; j++)
				w.div(j, a);
			if (h > 0.) {
				a = 0.;

				/*  use linear fit */
				/* weighted center of x values */

				for(j=nleft ; j<=nrt ; j++)
					a += w.get(j) * x[j];
				b = xs - a;
				c = 0.;
				for(j=nleft ; j<=nrt ; j++)
					c += w.get(j)*fsquare(x[j]-a);
				if (Math.sqrt(c) > 0.001*range) {
					b /= c;

					/* points are spread out */
					/* enough to compute slope */

					for(j=nleft; j <= nrt; j++)
						w.mult(j, (b*(x[j]-a) + 1.));
				}
			}
			ys.set(0,0);
			for(j=nleft; j <= nrt; j++)
				ys.add(0, w.get(j) * y[j]);
		}
		return ok;
	}

	static void clowess(double[] x, double[] y, int n, double f, int nsteps, double delta, ArrayFromC ys, ArrayFromC rw, ArrayFromC res)
	{
		int i, iter, j, last, nleft, nright, ns;
		boolean ok;
		double alpha, c1, c9, cmad, cut, d1, d2, denom, r, sc;

		if (n < 2) {
			ys.set(0, y[0]); return;
		}

		/* at least two, at most n points */
		ns = Math.max(2, Math.min(n, (int)(f*n + 1e-7)));

		/* robustness iterations */

		iter = 1;
		while (iter <= nsteps+1) {
			nleft = 0;
			nright = ns-1;
			last = 0;	/* index of prev estimated point */
			i = 0;		/* index of current point */

			for(;;) {
				if (nright < n-1) {

					/* move nleft,  nright to right */
					/* if radius decreases */

					d1 = x[i] - x[nleft];
					d2 = x[nright+1] - x[i];

					/* if d1 <= d2 with */
					/* x[nright+1] == x[nright], */
					/* lowest fixes */

					if (d1 > d2) {

						/* radius will not */
						/* decrease by */
						/* move right */

						nleft++;
						nright++;
						continue;
					}
				}

				/* fitted value at x[i] */
				ok = lowest(x, y, n, x[i], ys.newReference(i),
						nleft, nright, res, iter>1, rw.arr);
				if (!ok) ys.set(i, y[i]);

				/* all weights zero */
				/* copy over value (all rw==0) */

				if (last < i-1) {
					denom = x[i]-x[last];

					/* skipped points -- interpolate */
					/* non-zero - proof? */

					for(j = last+1; j < i; j++) {
						alpha = (x[j]-x[last])/denom;
						ys.set(j , alpha*ys.get(i) + (1.-alpha)*ys.get(last));
					}
				}

				/* last point actually estimated */
				last = i;

				/* x coord of close points */
				cut = x[last]+delta;
				for (i = last+1; i < n; i++) {
					if (x[i] > cut)
						break;
					if (x[i] == x[last]) {
						ys.set(i, ys.get(last));
						last = i;
					}
				}
				i = Math.max(last+1, i-1);
				if (last >= n-1)
					break;
						
			}
			/* residuals */
			for(i = 0; i < n; i++)
				res.set(i , y[i] - ys.get(i));

			/* overall scale estimate */
			sc = 0.;
			for(i = 0; i < n; i++) sc += Math.abs(res.get(i));
			sc /= n;

			/* compute robustness weights */
			/* except last time */

			if (iter > nsteps)
				break;

			for(i = 0 ; i < n ; i++)
				rw.set(i, Math.abs(res.get(i)));
			
			/* Compute   cmad := 6 * median(rw[], n)  ---- */
			cmad = 6.*Statistics.median(rw.arr);

			if(cmad < 1e-7 * sc) /* effectively zero */
				break;
			
			c9 = 0.999*cmad;
			c1 = 0.001*cmad;
			for(i = 0 ; i < n ; i++) {
				r = Math.abs(res.get(i));
				if (r <= c1)
					rw.set(i, 1.);
				else if (r <= c9)
					rw.set(i, fsquare(1.-fsquare(r/cmad)));
				else
					rw.set(i,0);
			}
			iter++;
		}
	}

	/** Performs lowess on vectors x,y, modifies values in ys, rw, res */
	public static void lowess(double[] x, double[] y, int n, double f, int nsteps, double delta, double[] ys, double[] rw, double[] res)	{
		clowess(x, y, n, f, nsteps, delta, new ArrayFromC(ys), new ArrayFromC(rw), new ArrayFromC(res));
	}

}
