package mayday.Reveal.data.ld.old;

import java.util.HashSet;

import mayday.Reveal.data.SNV;

@SuppressWarnings("serial")
public class LDBlock extends HashSet<SNV> {
	private int index;
	
	public LDBlock(int index) {
		super();
		this.index = index;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof LDBlock))
			return false;
		LDBlock b = (LDBlock)o;
		if(this == b)
			return true;
		if(this.getIndex() == b.getIndex())
			return true;
		return false;
	}
}
