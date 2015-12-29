package mayday.Reveal.data.ld;

import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.SNVPair;
import mayday.clustering.qt.algorithm.clustering.QTPAdList;
import mayday.clustering.qt.algorithm.clustering.QTPPair;

public class LDAdList extends QTPAdList {

	private SNVList snps;
	private LDResults ldResults;
	private double threshold;
	
	public LDAdList(SNVList snps, LDResults ldResults, double threshold) {
		super(null, snps.size(), null);
		this.ldResults = ldResults;
		this.snps = snps;
		this.threshold = threshold;
	}
	
	public synchronized void add(int i, int j) {
		SNV a = snps.get(i);
		SNV b = snps.get(j);
		
		Double r2 = ldResults.get(new SNVPair(a, b));
		//no r2 value available -> these two SNPs are not in LD
		if(r2 == null)
			return;
		else if (Double.compare(r2, threshold) > 0) {
			//add probe j to list i
			double dist = 1- r2;
			int index = getInsertionIndex(this.adList.get(i), dist);
			this.adList.get(i).add(index, new QTPPair(j, dist));
			//add probe i to list j
			index = getInsertionIndex(this.adList.get(j), dist);
			this.adList.get(j).add(index, new QTPPair(i, dist));
		}
	}
}
