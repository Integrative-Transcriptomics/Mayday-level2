package mayday.Reveal.data.ld.old;

import mayday.Reveal.data.SNV;

public class LDSNP implements Comparable<LDSNP> {

	private SNV s;
	private double r2;
	
	public LDSNP(SNV s, double r2) {
		this.s = s;
		this.r2 = r2;
	}
	
	public SNV getSNP() {
		return this.s;
	}
	
	public double getR2() {
		return this.r2;
	}

	@Override
	public int compareTo(LDSNP s2) {
		return Double.compare(r2, s2.r2);
	}
	
	public boolean equals(LDSNP s2) {
		return s.equals(s2.s);
	}
	
	public int hashCode() {
		return s.hashCode();
	}
}
