package mayday.Reveal.data.meta;

import mayday.Reveal.data.Gene;
import mayday.Reveal.data.SNP;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class SingleLocusResult extends LocusResult<SNP, SingleLocusResult.Statistics> {
	
	private Gene g;
	
	/**
	 * minimal p value over all snps
	 */
	public double minPValue = Double.MAX_VALUE;
	
	/**
	 * @param g
	 */
	public SingleLocusResult(Gene g) {
		this.g = g;
	}
	
	/**
	 * @return gene name
	 */
	public String getGeneName() {
		return this.g.getName();
	}
	
	/**
	 * @author jaeger
	 *
	 */
	public class Statistics {
		public Double beta;
		public Double se;
		public Double r2;
		public Double t;
		public Double p;
		
		/**
		 * @param beta
		 * @param se
		 * @param r2
		 * @param t
		 * @param p
		 */
		public Statistics(Double beta, Double se, Double r2, Double t, Double p) {
			this.beta = beta;
			this.se = se;
			this.r2 = r2;
			this.t = t;
			this.p = p;
			
			if(!p.equals(Double.NaN))
				if(Double.compare(p, minPValue) < 0) {
					minPValue = p;
				}
		}
	}

	@Override
	public String serialize() {
		StringBuffer serial = new StringBuffer();
		int count = 0;
		for(SNP s : keySet()) {
			if(s == null)
				continue;
			count++;
			serial.append(s.getID());
			serial.append("\t");
			Statistics stat = get(s);
			serial.append(stat.beta);
			serial.append("\t");
			serial.append(stat.se);
			serial.append("\t");
			serial.append(stat.r2);
			serial.append("\t");
			serial.append(stat.t);
			serial.append("\t");
			serial.append(stat.p);
			if(count != size())
				serial.append("\n");
		}
		return serial.toString();
	}
}
