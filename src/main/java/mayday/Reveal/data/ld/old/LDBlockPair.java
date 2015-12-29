package mayday.Reveal.data.ld.old;

public class LDBlockPair {
	private LDBlock block1;
	private LDBlock block2;
	
	public LDBlockPair(LDBlock block1, LDBlock block2) {
		this.block1 = block1;
		this.block2 = block2;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof LDBlockPair))
			return false;
		LDBlockPair b = (LDBlockPair)o;
		if(this == b)
			return true;
		if(this.block1.equals(b.block1) && this.block2.equals(b.block2) ||
				this.block1.equals(b.block2) && this.block2.equals(b.block1))
			return true;
		return false;
	}
	
	public int hashCode() {
		int h1 = block1.hashCode();
		int h2 = block2.hashCode();
		return (h1 * 17) ^ h2;
	}
}
