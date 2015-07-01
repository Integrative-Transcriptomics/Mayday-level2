package mayday.Reveal.data.meta;

import java.util.HashMap;
import java.util.List;

import mayday.Reveal.data.Gene;
import mayday.Reveal.data.GenePair;
import mayday.Reveal.data.SNVPair;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class TwoLocusResult extends LocusResult<GenePair, List<SNVPair>>{

	private Gene g;
	
	public int getMaxSNPPairs() {
		int max = 0;
		for(GenePair gp : keySet()) {
			if(!gp.gene1.equals(gp.gene2)) {
				int size = get(gp).size();
				if(size > max) {
					max = size;
				}
			}
		}
		return max;
	}
	
	/**
	 * mapping for the statistics
	 */
	public HashMap<GenePair, List<Statistics>> statMapping = new HashMap<GenePair, List<Statistics>>();
	
	/**
	 * @param g
	 */
	public TwoLocusResult(Gene g) {
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
		public Double stat;
		public Double p;
		
		/**
		 * @param beta
		 * @param stat
		 * @param p
		 */
		public Statistics(Double beta, Double stat, Double p) {
			this.beta = beta;
			this.stat = stat;
			this.p = p;
		}
	}

	@Override
	public String serialize() {
		StringBuffer serial = new StringBuffer();
		int count = 0;
		for(GenePair gp : this.keySet()) {
			count++;
			serial.append(">>");
			serial.append(gp.gene1 + "," + gp.gene2);
			serial.append("\n");
			List<SNVPair> snpPairs = get(gp);
			List<Statistics> stats = statMapping.get(gp);
			for(int i = 0; i < snpPairs.size(); i++) {
				SNVPair sp = snpPairs.get(i);
				Statistics stat = stats.get(i);
				serial.append(sp.snp1.getID());
				serial.append("\t");
				serial.append(sp.snp2.getID());
				serial.append("\t");
				serial.append(stat.beta);
				serial.append("\t");
				serial.append(stat.stat);
				serial.append("\t");
				serial.append(stat.p);
				if(i != (snpPairs.size()-1))
					serial.append("\n");
			}
			if(count != size())
				serial.append("\n");
		}
		
		return serial.toString();
	}
}