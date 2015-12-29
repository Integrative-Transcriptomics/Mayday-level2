package mayday.wapiti.transformations.impl.background.normexp;

import mayday.core.math.DNorm;
import mayday.core.math.PNorm;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.wapiti.transformations.impl.rma.bg.RMABackground;

public class NormExp {

	public static final int SADDLE = 0;
	// public static final int MLE = 1;
	public static final int RMA = 2;
	// public static final int RMA75 = 3;

	/** Correct Foreground only Arrays
	 * @param data the uncorrected vector with background already subtracted,i.e. data=RG$R-RG$Rb
	 * @param method either SADDLE or RMA
	 * @param offset is added to the corrected vector. Set to null to disable offset addition
	 * @return the normexp corrected data vector
	 */
	public static AbstractVector normExp(AbstractVector data, int method, Double offset) {
		Parameter par = NormExp.fit(data, method);
		AbstractVector result = NormExp.signal(par, data);
		if (offset!=null)
			result.add(offset);
		return result;
	}
	
	/** Correct Two-Channel Arrays
	 * @param fg the uncorrected foreground data
	 * @param bg the uncorrected background data
	 * @param method either SADDLE or RMA
	 * @param offset is added to the corrected vector. Set to null to disable offset addition
	 * @return the normexp corrected data vector
	 */
	public static AbstractVector normExp(AbstractVector fg, AbstractVector bg, int method, Double offset) {
		AbstractVector data = fg.clone();
		data.subtract(bg);
		return normExp(data, method, offset);
	}
	
	/** Correct Foreground+Background Arrays with the normExp method, with defaults as in the limma bioconductor R package
	 * @param data the uncorrected vector with background already subtracted,i.e. data=RG$R-RG$Rb
	 * @return the normexp corrected data vector
	 */
	public static AbstractVector normExp(AbstractVector data) {
		return normExp(data, SADDLE, null);
	}
	
	/** Correct Foreground+Background Arrays with the normExp method, with defaults as in the limma bioconductor R package
	 * @param fg the uncorrected foreground data
	 * @param bg the uncorrected background data
	 * @return the normexp corrected data vector
	 */
	public static AbstractVector normExp(AbstractVector fg, AbstractVector bg) {
		return normExp(fg, bg, SADDLE, null);
	}

	public static Parameter fit(AbstractVector x, int method)
			throws IllegalArgumentException {

		if (x.size() < 4)
			throw new IllegalArgumentException(
					"ERROR! At least 4 intensity values needed!");

		switch (method) {

		case SADDLE:
			return Saddle.saddle(x);

			// case MLE:
			// TODO implement this!

		case RMA:
			return calculateRMA(x);

			// case RMA75:
			// TODO implement this!

		default:
			System.out.println("Nothing to do!");
			return null;
		}
	}

	private static Parameter calculateRMA(AbstractVector x) {
		double[] param = new double[3];
		RMABackground.bgParameters(x.toArray(), param, x.size(), 1, 0);
		double alpha = -Math.log(param[0]);
		double mu = param[1];
		double sigma = Math.log(param[2]);
		return new Parameter(mu, sigma, alpha);
	}

	protected static AbstractVector signal(Parameter par, AbstractVector x)
			throws IllegalArgumentException {
		double mu = par.mu;
		double sigma = Math.exp(par.sigma);
		double sigma2 = sigma * sigma;
		double alpha = Math.exp(par.alpha);
		double s = -mu - sigma2 / alpha;
		int numEl = x.size();

		if (alpha <= 0.0)
			throw new IllegalArgumentException("Alpha must be positive!");
		if (sigma <= 0.0)
			throw new IllegalArgumentException("Sigma  must be positive!");

		AbstractVector signal = new DoubleVector(numEl);
		for (int i = 0; i < numEl; i++) {
			double xprev = x.get(i);
			double musf = xprev + s;
			double pnlog = PNorm.pnorm(0, musf, sigma, false, true);
			double dnlog = DNorm.dnorm(0, musf, sigma, true);
			double exp = Math.exp(dnlog-pnlog);
			double add = sigma2 * exp;
			double res = musf + add;			
			if (res<1e-6)
				res = 1e-6;
			signal.set(i, res);
		}
		
		return signal;
	}

	public static AbstractVector dnorm(double x, AbstractVector mean, double sd, boolean logged) {
		AbstractVector d = new DoubleVector(mean.size());
		for (int i = 0; i < mean.size(); i++) {
			d.set(i, DNorm.dnorm(x, mean.get(i), sd, logged));
		}
		return d;
	}

	public static AbstractVector pnorm(double x, AbstractVector mean,
			double sd, boolean lowerTail, boolean logged) {
		AbstractVector p = new DoubleVector(mean.size());
		for (int i = 0; i < mean.size(); i++) {
			p.set(i, PNorm.pnorm(x, mean.get(i), sd, lowerTail, logged));
		}
		return p;
	}

	public static class Parameter {
		public double mu = 0.0;
		public double sigma = 0.0;
		public double alpha = 0.0;

		protected Parameter(double mu, double sigma, double alpha) {
			this.mu = mu;
			this.sigma = sigma;
			this.alpha = alpha;
		}

		public String toString() {
			return "mu=" + mu + "\n" + "sigma=" + sigma + "\n" + "alpha="
					+ alpha;
		}
	}
}
