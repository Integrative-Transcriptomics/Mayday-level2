package mayday.Reveal.data;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author jaeger
 *
 */
public class HaplotypesList {
	
	
	private Haplotypes[] haplotypes;
	int counter = 0;
	
	/**
	 * @param capacity
	 */
	public HaplotypesList(int capacity) {
		this.haplotypes = new Haplotypes[capacity];
	}
	
	public void add(Haplotypes haplotypes) {
		this.haplotypes[counter++] = haplotypes;
	}
	
	public void remove(int index) {
		this.haplotypes[index] = null;
	}
	
	public Haplotypes get(int index) {
		return this.haplotypes[index];
	}
	
	public int size() {
		return this.haplotypes.length;
	}

	public void serialize(BufferedWriter bw) throws IOException {
		bw.append(String.valueOf(size()));
		bw.append("\n");
		for(int i = 0; i < haplotypes.length; i++) {
			Haplotypes h = haplotypes[i];
			bw.append(h.serialize());
			bw.append("\n");
		}
	}
}
