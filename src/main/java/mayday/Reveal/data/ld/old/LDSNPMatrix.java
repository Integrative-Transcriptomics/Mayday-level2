package mayday.Reveal.data.ld.old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;

public class LDSNPMatrix {

	private List<LDSNPList> list;
	private HashMap<SNV, Integer> snpToIndex;
	
	public LDSNPMatrix(SNVList snps) {
		list = new ArrayList<LDSNPList>();
		snpToIndex = new HashMap<SNV, Integer>();
		
		for(int i = 0; i < snps.size(); i++) {
			list.add(new LDSNPList());
			snpToIndex.put(snps.get(i), i);
		}
	}
	
	public void addSNPPair(int i, SNV a, int j, SNV b, double r2) {
		LDSNPList la = list.get(i);
		LDSNPList lb = list.get(j);
		
		la.add(new LDSNP(b,r2));
		lb.add(new LDSNP(a, r2));
	}
	
	public void mergeLists(SNV a, SNV b) {
		int aI = snpToIndex.get(a);
		int bI = snpToIndex.get(b);
		
		LDSNPList la = list.get(aI);
		LDSNPList lb = list.get(bI);
		
		LDSNPList res = intersect(la, lb);
		
		
	}
	
	private LDSNPList intersect(LDSNPList a, LDSNPList b) {
		LDSNPList r = new LDSNPList();
		
		for(LDSNP s : a) {
			if(b.contains(s)) {
				r.add(s);
			}
		}
		
		return r;
	}
}
