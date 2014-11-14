package mayday.GWAS.functions;

import mayday.core.structures.maps.BidirectionalHashMap;

public class CodonsAminoacids {
	
	BidirectionalHashMap<Codon, Aminoacid> mapping = new BidirectionalHashMap<Codon, Aminoacid>();
	Codon startCodon = new Codon('A','U','G');
	Codon[] stopCodons = new Codon[]{new Codon('U','G','A'), new Codon('U','A','G'), new Codon('U','A','A')};
	
	public CodonsAminoacids() {
		setupMapping();
	}
	
	public Aminoacid translate(Codon c) {
		return mapping.get(c);
	}
	
	public Codon translate(Aminoacid aa) {
		return mapping.get(aa);
	}
	
	private void setupMapping() {
		//setup aminoacids
		Aminoacid gly = new Aminoacid("Gly", 'G');
		Aminoacid pro = new Aminoacid("Pro", 'P');
		Aminoacid ala = new Aminoacid("Ala", 'A');
		Aminoacid val = new Aminoacid("Val", 'V');
		Aminoacid leu = new Aminoacid("Leu", 'L');
		Aminoacid ile = new Aminoacid("Ile", 'I');
		Aminoacid met = new Aminoacid("Met", 'M');
		Aminoacid cys = new Aminoacid("Cys", 'C');
		Aminoacid phe = new Aminoacid("Phe", 'F');
		Aminoacid tyr = new Aminoacid("Tyr", 'Y');
		Aminoacid trp = new Aminoacid("Trp", 'W');
		Aminoacid his = new Aminoacid("His", 'H');
		Aminoacid lys = new Aminoacid("Lys", 'K');
		Aminoacid arg = new Aminoacid("Arg", 'R');
		Aminoacid gln = new Aminoacid("Gln", 'Q');
		Aminoacid asn = new Aminoacid("Asn", 'N');
		Aminoacid glu = new Aminoacid("Glu", 'E');
		Aminoacid asp = new Aminoacid("Asp", 'D');
		Aminoacid ser = new Aminoacid("Ser", 'S');
		Aminoacid thr = new Aminoacid("Thr", 'T');
		Aminoacid end = new Aminoacid("End", '*');
		
		//set up codons
		Codon ggg = new Codon('G', 'G', 'G');
		Codon gga = new Codon('G', 'G', 'A');
		Codon ggc = new Codon('G', 'G', 'C');
		Codon ggu = new Codon('G', 'G', 'U');
		Codon ccu = new Codon('C', 'C', 'U');
		Codon cca = new Codon('C', 'C', 'A');
		Codon ccg = new Codon('C', 'C', 'G');
		Codon ccc = new Codon('C', 'C', 'C');
		Codon gca = new Codon('G', 'C', 'A');
		Codon gug = new Codon('G', 'U', 'G');
		Codon guu = new Codon('G', 'U', 'U');
		Codon guc = new Codon('G', 'U', 'C');
		Codon cuu = new Codon('C', 'U', 'U');
		Codon cuc = new Codon('C', 'U', 'C');
		Codon cua = new Codon('C', 'U', 'A');
		Codon cug = new Codon('C', 'U', 'G');
		Codon uug = new Codon('U', 'U', 'G');
		Codon uua = new Codon('U', 'U', 'A');
		Codon auu = new Codon('A', 'U', 'U');
		Codon auc = new Codon('A', 'U', 'C');
		Codon aua = new Codon('A', 'U', 'A');
		Codon aug = new Codon('A', 'U', 'G');
		Codon ugu = new Codon('U', 'G', 'U');
		Codon ugc = new Codon('U', 'G', 'C');
		Codon uuc = new Codon('U', 'U', 'C');
		Codon uuu = new Codon('U', 'U', 'U');
		Codon uau = new Codon('U', 'A', 'U');
		Codon uac = new Codon('U', 'A', 'C');
		Codon ugg = new Codon('U', 'G', 'G');
		Codon cau = new Codon('C', 'A', 'U');
		Codon cac = new Codon('C', 'A', 'C');
		Codon aag = new Codon('A', 'A', 'G');
		Codon aaa = new Codon('A', 'A', 'A');
		Codon aga = new Codon('A', 'G', 'A');
		Codon agg = new Codon('A', 'G', 'G');
		Codon cga = new Codon('C', 'G', 'A');
		Codon cgg = new Codon('C', 'G', 'G');
		Codon cgc = new Codon('C', 'G', 'C');
		Codon cgu = new Codon('C', 'G', 'U');
		Codon caa = new Codon('C', 'A', 'A');
		Codon cag = new Codon('C', 'A', 'G');
		Codon aac = new Codon('A', 'A', 'C');
		Codon aau = new Codon('A', 'A', 'U');
		Codon gag = new Codon('G', 'A', 'G');
		Codon gaa = new Codon('G', 'A', 'A');
		Codon gac = new Codon('G', 'A', 'C');
		Codon gau = new Codon('G', 'A', 'U');
		Codon agc = new Codon('A', 'G', 'C');
		Codon agu = new Codon('A', 'G', 'U');
		Codon ucu = new Codon('U', 'C', 'U');
		Codon uca = new Codon('U', 'C', 'A');
		Codon ucc = new Codon('U', 'C', 'C');
		Codon ucg = new Codon('U', 'C', 'G');
		Codon aca = new Codon('A', 'C', 'A');
		Codon acg = new Codon('A', 'C', 'G');
		Codon acu = new Codon('A', 'C', 'U');
		Codon acc = new Codon('A', 'C', 'C');
		Codon uga = new Codon('U', 'G', 'A');
		Codon uag = new Codon('U', 'A', 'G');
		Codon uaa = new Codon('U', 'A', 'A');
		Codon gcu = new Codon('G', 'C', 'U');
		Codon gcg = new Codon('G', 'C', 'G');
		Codon gcc = new Codon('G', 'C', 'C');
		Codon gua = new Codon('G', 'U', 'A');
		
		//put codon and aminoacid together
		mapping.put(agg, arg);
		mapping.put(aga, arg);
		mapping.put(agc, ser);
		mapping.put(agu, ser);
		mapping.put(aag, lys);
		mapping.put(aaa, lys);
		mapping.put(aac, asn);
		mapping.put(aau, asn);
		mapping.put(aca, thr);
		mapping.put(acg, thr);
		mapping.put(acc, thr);
		mapping.put(acu, thr);
		mapping.put(aug, met);
		mapping.put(aua, ile);
		mapping.put(auc, ile);
		mapping.put(auu, ile);
		mapping.put(cga, arg);
		mapping.put(cgg, arg);
		mapping.put(cgc, arg);
		mapping.put(cgu, arg);
		mapping.put(caa, gln);
		mapping.put(cag, gln);
		mapping.put(cau, his);
		mapping.put(cac, his);
		mapping.put(cca, pro);
		mapping.put(ccu, pro);
		mapping.put(ccc, pro);
		mapping.put(ccg, pro);
		mapping.put(cua, leu);
		mapping.put(cuu, leu);
		mapping.put(cuc, leu);
		mapping.put(cug, leu);
		mapping.put(ugg, trp);
		mapping.put(uga, end);
		mapping.put(ugu, cys);
		mapping.put(ugc, cys);
		mapping.put(uag, end);
		mapping.put(uaa, end);
		mapping.put(uau, tyr);
		mapping.put(uac, tyr);
		mapping.put(uca, ser);
		mapping.put(ucu, ser);
		mapping.put(ucg, ser);
		mapping.put(ucc, ser);
		mapping.put(uug, leu);
		mapping.put(uua, leu);
		mapping.put(uuc, phe);
		mapping.put(uuu, phe);
		mapping.put(ggg, gly);
		mapping.put(ggc, gly);
		mapping.put(gga, gly);
		mapping.put(ggu, gly);
		mapping.put(gag, glu);
		mapping.put(gaa, glu);
		mapping.put(gac, asp);
		mapping.put(gau, asp);
		mapping.put(gca, ala);
		mapping.put(gcu, ala);
		mapping.put(gcg, ala);
		mapping.put(gcc, ala);
		mapping.put(gua, val);
		mapping.put(gug, val);
		mapping.put(guu, val);
		mapping.put(guc, val);
	}

	public class Codon {
		public char a,b,c;
		
		public Codon(char a, char b, char c) {
			// automatically translate T to U
			// T is not allowed in RNA sequences
			this.a = a == 'T' ? 'U' : a;
			this.b = b == 'T' ? 'U' : b;
			this.c = c == 'T' ? 'U' : c;
		}
		
		public boolean equals(Object o) {
			if(!(o instanceof Codon))
				return false;
			Codon codon = (Codon)o;
			
			return codon.a == a && codon.b == b && codon.c == c;
		}
		
		public int hashCode() {
			return (a + "" + b + "" + c).hashCode();
		}
	}
	
	public class Aminoacid {
		
		public String threeLetter;
		public char oneLetter;
		
		public Aminoacid(String threeLetter, char oneLetter) {
			this.threeLetter = threeLetter;
			this.oneLetter = oneLetter;
		}
		
		public boolean equals(Object o) {
			if(!(o instanceof Aminoacid))
				return false;
			Aminoacid ac = (Aminoacid)o;
			return ac.threeLetter.equals(threeLetter) && ac.oneLetter == oneLetter;
		}
		
		public int hashCode() {
			return (oneLetter + "" + threeLetter).hashCode();
		}
	}

	public boolean isStartCodon(Codon c) {
		return c.equals(startCodon);
	}

	public boolean isStopCodon(Codon c) {
		boolean isStop = false;
		for(Codon c2 : stopCodons)
			if(c.equals(c2)) {
				isStop = true;
				break;
			}
		return isStop;
	}

	public boolean isStopCodon(String modifiedCodonA) {
		if(modifiedCodonA.length() != 3)
			return false;
		
		char a = modifiedCodonA.charAt(0);
		char b = modifiedCodonA.charAt(1);
		char c = modifiedCodonA.charAt(2);
		
		if(isStopCodon(new Codon(a,b,c)))
			return true;
		
		return false;
	}

	public boolean isStartCodon(String codon) {
		if(codon.length() != 3)
			return false;
		
		char a = codon.charAt(0);
		char b = codon.charAt(1);
		char c = codon.charAt(2);
		
		if(isStartCodon(new Codon(a,b,c)))
			return true;
		
		return false;
	}
}
