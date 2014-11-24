package mayday.Reveal.data;


/**
 * @author jaeger
 *
 */
public class SNPPair {
	
	public SNP snp1;
	public SNP snp2;
	
	private Integer hash = null;
	
	/**
	 * @param snp1
	 * @param snp2
	 */
	public SNPPair(SNP snp1, SNP snp2) {
		this.snp1 = snp1;
		this.snp2 = snp2;
	}
	
	public boolean equals(Object o) {
		if(o == this)
			return true;
		if(!(o instanceof SNPPair)) {
			return false;
		}
		
		if(((SNPPair)o).snp1.equals(this.snp1) && ((SNPPair)o).snp2.equals(this.snp2)) {
			return true;
		}
		
		if(((SNPPair)o).snp1.equals(this.snp2) && ((SNPPair)o).snp2.equals(this.snp1)) {
			return true;
		}
		
		return false;
	}
	
	public int hashCode() {	
		double pos1 = snp1.getPosition();
		double pos2 = snp2.getPosition();
		
		if(pos1 < pos2) {
			if(hash == null)
				hash = (snp1.getID() + snp2.getID()).hashCode();
		} else {
			if(hash == null)
				hash = (snp2.getID() + snp1.getID()).hashCode();
		}
		
		return hash.intValue();
	}
}
