package mayday.GWAS.data;

import java.util.Arrays;


/**
 * @author jaeger
 *
 */
public class Haplotypes {

	/**
	 * encoding for nucleotide A
	 */
	public static final byte A = 1;
	/**
	 * encoding for nucleotide T
	 */
	public static final byte T = 2;
	/**
	 * encoding for nucleotide C
	 */
	public static final byte C = 3;
	/**
	 * encoding for nucleotide G
	 */
	public static final byte G = 4;
	
	private byte[] allel1;
	private byte[] allel2;
	
	private int c1 = 0;
	private int c2 = 0;
	
	/**
	 * @param numSNPs
	 * @param personID
	 */
	public Haplotypes(int numSNPs) {
		this.allel1 = new byte[numSNPs];
		this.allel2 = new byte[numSNPs];
	}
	
	/**
	 * @param index
	 * @return nucleotide on allel 1 at position index
	 */
	public char getSNPA(int index) {
		return decode(this.allel1[index]);
	}
	
	private char decode(byte b) {
		switch(b) {
		case A: return 'A';
		case T: return 'T';
		case C: return 'C';
		case G: return 'G';
		default: return 'X';
		}
	}
	
	/**
	 * @param index
	 * @return nucleotide on allel 2 at position index
	 */
	public char getSNPB(int index) {
		return decode(this.allel2[index]);
	}
	
	/**
	 * @param snpIndex
	 * @param c
	 */
	public void addSNPA(Integer snpIndex, char c) {
		allel1[c1++] = encode(c);
	}
	
	/**
	 * @param snpIndex
	 * @param c
	 */
	public void addSNPB(Integer snpIndex, char c) {
		allel2[c2++] = encode(c);
	}
	
	private byte encode(char c) {
		switch(c) {
		case 'A': return A;
		case 'T': return T;
		case 'C': return C;
		case 'G': return G;
		default: return -1;
		}
	}
	
	public void setAllel1(byte[] allel1) {
		this.allel1 = allel1;
	}
	
	public void setAllel2(byte[] allel2) {
		this.allel2 = allel2;
	}
	
	public String serialize() {
		StringBuffer b = new StringBuffer();
		b.append(Arrays.toString(allel1));
		b.append("\t");
		b.append(Arrays.toString(allel2));
		return b.toString();
	}
}
