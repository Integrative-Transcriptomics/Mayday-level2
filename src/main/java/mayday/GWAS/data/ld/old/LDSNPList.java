package mayday.GWAS.data.ld.old;

import java.util.Collections;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class LDSNPList extends LinkedList<LDSNP> {

	public boolean add(LDSNP s) {
		boolean b = super.add(s);
		
		if(b) {
			Collections.sort(this);
		}
		
		return b;
	}
}
