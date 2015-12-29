package mayday.wapiti.transformations.impl.background.normexp;

import java.util.ArrayList;

import mayday.core.math.Statistics;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.transformations.impl.background.normexp.NormExp.Parameter;

public class Saddle {
	public static Parameter saddle(AbstractVector x) {
		double mu = 0.0;
		double sigma2 = 0.0;
		double alpha = 0.0;

		// Starting values for parameters mu, alpha and sigma
		double[] quantiles = quantile(x, null);
		if (quantiles[0] == quantiles[3]) {
			return new Parameter(quantiles[0], Double.NEGATIVE_INFINITY,
					Double.NEGATIVE_INFINITY);
		}
		if (quantiles[1] > quantiles[0]) {
			mu = quantiles[1];
		} else {
			if (quantiles[2] > quantiles[0]) {
				mu = quantiles[2];
			} else {
				mu = quantiles[0] + 0.05 * (quantiles[3] - quantiles[0]);
			}
		}
		sigma2 = getSigma2(x, mu);
		alpha = x.mean() - mu;
		if (alpha <= 0)
			alpha = 1e-6;

		Parameter par = new Parameter(mu, Math.log(sigma2) / 2, Math.log(alpha));
		Parameter out = fitSaddleNelderMead(x, par);
		return out;
	}

	private static Parameter fitSaddleNelderMead(AbstractVector x, Parameter par) {
		double abstol = Double.NEGATIVE_INFINITY;
		double intol = 1.490116e-08;
		double alpha = 1.0;
		double beta = 0.5;
		double gamma = 2.0;
		int maxit = 500;

		Parameter out = nmmin(x, par, abstol, intol, alpha, beta, gamma, maxit);
		return out;
	}

	private static Parameter nmmin(AbstractVector x, Parameter par,
			double abstol, double intol, double alpha, double bet, double gamm,
			int maxit) {
		int n = 3;
		double[][] P = new double[n + 1][n + 2];
		double[] Bvec = { par.mu, par.sigma, par.alpha };

		if (maxit <= 0) {
			return par;
		}

		double f = normExpM2loglikSaddle(x, new Parameter(Bvec[0], Bvec[1],
				Bvec[2]));
		int funcount = 1;
		double convtol = intol * (Math.abs(f) + intol);
		int n1 = n + 1;
		int C = n + 2;
		P[n1 - 1][0] = f;

		for (int i = 0; i < n; i++) {
			P[i][0] = Bvec[i];
		}

		int L = 1;
		double size = 0.0;
		double step = 0.0;

		for (int i = 0; i < n; i++) {
			if (0.1 * Math.abs(Bvec[i]) > step) {
				step = 0.1 * Math.abs(Bvec[i]);
			}
		}
		if (step == 0.0) {
			step = 0.1;
		}

		for (int j = 2; j <= n1; j++) {
			for (int i = 0; i < n; i++) {
				P[i][j - 1] = Bvec[i];
			}
			double trystep = step;
			while (P[j - 2][j - 1] == Bvec[j - 2]) {
				P[j - 2][j - 1] = Bvec[j - 2] + trystep;
				trystep *= 10;
			}
			size += trystep;
		}

		double oldsize = size;
		boolean calcvert = true;

		do {
			if (calcvert) {
				for (int j = 0; j < n1; j++) {
					if (j + 1 != L) {
						for (int i = 0; i < n; i++)
							Bvec[i] = P[i][j];
						f = normExpM2loglikSaddle(x, new Parameter(Bvec[0],
								Bvec[1], Bvec[2]));
						funcount++;
						P[n1 - 1][j] = f;
					}
				}
				calcvert = false;
			}

			double VL = P[n1 - 1][L - 1];
			double VH = VL;
			int H = L;

			for (int j = 1; j <= n1; j++) {
				if (j != L) {
					f = P[n1 - 1][j - 1];
					if (f < VL) {
						L = j;
						VL = f;
					}
					if (f > VH) {
						H = j;
						VH = f;
					}
				}
			}

			if (VH <= VL + convtol || VL <= abstol)
				break;

			for (int i = 0; i < n; i++) {
				double temp = -P[i][H - 1];
				for (int j = 0; j < n1; j++) {
					temp += P[i][j];
				}
				P[i][C - 1] = temp / n;
			}

			for (int i = 0; i < n; i++) {
				Bvec[i] = (1.0 + alpha) * P[i][C - 1] - alpha * P[i][H - 1];
			}

			f = normExpM2loglikSaddle(x, new Parameter(Bvec[0], Bvec[1],
					Bvec[2]));
			funcount++;
			double VR = f;

			if (VR < VL) {
				P[n1 - 1][C - 1] = f;
				for (int i = 0; i < n; i++) {
					f = gamm * Bvec[i] + (1 - gamm) * P[i][C - 1];
					P[i][C - 1] = Bvec[i];
					Bvec[i] = f;
				}
				f = normExpM2loglikSaddle(x, new Parameter(Bvec[0], Bvec[1],
						Bvec[2]));
				funcount++;
				if (f < VR) {
					for (int i = 0; i < n; i++) {
						P[i][H - 1] = Bvec[i];
					}
					P[n1 - 1][H - 1] = f;
				} else {
					for (int i = 0; i < n; i++) {
						P[i][H - 1] = P[i][C - 1];
					}
					P[n1 - 1][H - 1] = VR;
				}
			} else {
				if (VR < VH) {
					for (int i = 0; i < n; i++) {
						P[i][H - 1] = Bvec[i];
					}
					P[n1 - 1][H - 1] = VR;
				}

				for (int i = 0; i < n; i++) {
					Bvec[i] = (1 - bet) * P[i][H - 1] + bet * P[i][C - 1];
				}
				f = normExpM2loglikSaddle(x, new Parameter(Bvec[0], Bvec[1],
						Bvec[2]));
				funcount++;

				if (f < P[n1 - 1][H - 1]) {
					for (int i = 0; i < n; i++) {
						P[i][H - 1] = Bvec[i];
					}
					P[n1 - 1][H - 1] = f;
				} else {
					if (VR >= VH) {
						calcvert = true;
						size = 0.0;
						for (int j = 0; j < n1; j++) {
							if (j + 1 != L) {
								for (int i = 0; i < n; i++) {
									P[i][j] = bet * (P[i][j] - P[i][L - 1])
											+ P[i][L - 1];
									size += Math.abs(P[i][j] - P[i][L - 1]);
								}
							}
						}
						if (size < oldsize) {
							oldsize = size;
						} else {
							break;
						}
					}
				}
			}

		} while (funcount <= maxit);

		Parameter out = new Parameter(P[0][L - 1], P[1][L - 1], P[2][L - 1]);
		return out;
	}

	private static double getSigma2(AbstractVector x, double mu) {
		ArrayList<Double> x2 = new ArrayList<Double>();
		for (int i = 0; i < x.size(); i++) {
			if (x.get(i) < mu) {
				x2.add((x.get(i) - mu) * (x.get(i) - mu));
			}
		}
		return Statistics.mean(x2);
	}

	// equal to the quantile function in R
	public static double[] quantile(AbstractVector x, double[] probs) {
		if (probs == null) {
			probs = new double[] { 0.0, 0.05, 0.1, 1.0 };
		}

		int nProbs = probs.length;
		int n = x.size();
		assert (n > 0);

		double[] index = new double[nProbs];
		int[] lo = new int[nProbs];
		int[] hi = new int[nProbs];
		double[] qs = new double[nProbs];

		AbstractVector sorted = x.clone();
		sorted.sort();

		for (int j = 0; j < nProbs; j++) {
			index[j] = 1 + (n - 1) * probs[j];
			lo[j] = (int) Math.floor(index[j]);
			hi[j] = (int) Math.ceil(index[j]);
		}
		// int[] partial = unique(combine(lo, hi));
		int[] i = getIndexGreaterLo(index, lo);

		for (int j = 0; j < lo.length; j++) {
			qs[j] = sorted.get(lo[j] - 1);
		}
		// i = seqAlong(i);
		double[] h = new double[i.length];
		for (int k = 0; k < i.length; k++) {
			h[k] = index[i[k]] - lo[i[k]];
			if (h[k] != 0) {
				qs[i[k]] = (1 - h[k]) * qs[i[k]] + h[k]
						* sorted.get(hi[i[k]] - 1);
			}
		}
		return qs;
	}

	private static int[] getIndexGreaterLo(double[] index, int[] lo) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < index.length; i++) {
			if (index[i] > lo[i]) {
				result.add(i);
			}
		}
		int[] indices = new int[result.size()];
		for (int i = 0; i < indices.length; i++) {
			indices[i] = result.get(i);
		}
		return indices;
	}

	private static double normExpM2loglikSaddle(AbstractVector x, Parameter par) {
		int n = x.size();
		double mu = par.mu;
		double sigma = Math.exp(par.sigma);
		double sigma2 = sigma * sigma;
		double alpha = Math.exp(par.alpha);

		double upperbound1;
		double upperbound2;
		double[] upperbound = new double[n];
		double[] theta = new double[n];
		double k1, k2, k3, k4;
		double err;
		double c0, c1, c2 = sigma2 * alpha;
		double logf;
		double omat, omat2;
		double thetaQuadratic;
		boolean keepRepeating = true;
		boolean[] hasConverged = new boolean[n];
		int nConverged = 0;
		double alpha2 = alpha * alpha;
		double alpha3 = alpha * alpha2;
		double alpha4 = alpha2 * alpha2;
		double dK, ddK, delta;

		for (int i = 0; i < n; i++) {
			err = x.get(i) - mu;
			upperbound1 = Math.max(0.0,
					((err - alpha) / (alpha * Math.abs(err))));
			upperbound2 = err / sigma2;
			upperbound[i] = Math.min(upperbound1, upperbound2);
			c1 = -sigma2 - err * alpha;
			c0 = -alpha + err;
			thetaQuadratic = (-c1 - Math.sqrt(c1 * c1 - 4 * c0 * c2))
					/ (2.0 * c2);
			theta[i] = Math.min(thetaQuadratic, upperbound[i]);
			hasConverged[i] = false;
		}

		int j = 0;
		while (keepRepeating) {
			j++;
			for (int i = 0; i < n; i++) {
				if (hasConverged[i] == false) {
					omat = 1.0 - alpha * theta[i];
					dK = mu + sigma2 * theta[i] + alpha / omat;
					ddK = sigma2 + alpha2 / (omat * omat);
					delta = (x.get(i) - dK) / ddK;
					theta[i] += delta;
					if (j == 1) {
						theta[i] = Math.min(theta[i], upperbound[i]);
					}
					if (Math.abs(delta) < 1e-10) {
						hasConverged[i] = true;
						nConverged++;
					}
				}
			}
			if (nConverged == n || j > 50) {
				keepRepeating = false;
			}
		}

		double loglik = 0.0;
		for (int i = 0; i < n; i++) {
			omat = 1 - alpha * theta[i];
			omat2 = omat * omat;
			k1 = mu * theta[i] + 0.5 * sigma2 * theta[i] * theta[i]
					- Math.log(omat);
			k2 = sigma2 + alpha2 / omat2;
			logf = -0.5 * Math.log(2.0 * Math.PI * k2) - x.get(i) * theta[i]
					+ k1;
			k3 = 2.0 * alpha3 / (omat * omat2);
			k4 = 6.0 * alpha4 / (omat2 * omat2);
			logf += k4 / (8.0 * k2 * k2) - (5.0 * k3 * k3)
					/ (24.0 * k2 * k2 * k2);
			loglik += logf;
		}

		return -2.0 * loglik;
	}
}
