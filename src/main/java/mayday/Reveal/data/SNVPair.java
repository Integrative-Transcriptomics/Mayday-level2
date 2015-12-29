package mayday.Reveal.data;


/**
 * @author jaeger
 *
 */
public class SNVPair {
	
	public SNV snp1;
	public SNV snp2;
	
	private Integer hash = null;
	
	/**
	 * @param snp1
	 * @param snp2
	 */
	public SNVPair(SNV snp1, SNV snp2) {
		this.snp1 = snp1;
		this.snp2 = snp2;
	}
	
	public boolean equals(Object o) {
		if(o == this)
			return true;
		if(!(o instanceof SNVPair)) {
			return false;
		}
		
		if(((SNVPair)o).snp1.equals(this.snp1) && ((SNVPair)o).snp2.equals(this.snp2)) {
			return true;
		}
		
		if(((SNVPair)o).snp1.equals(this.snp2) && ((SNVPair)o).snp2.equals(this.snp1)) {
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
