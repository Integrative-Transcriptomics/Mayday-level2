package mayday.Reveal.data;

import java.util.Set;

/**
 * @author jaeger
 *
 */
public class GenePair {
	public Gene gene1;
	public Gene gene2;
	
	/**
	 * @param gene1
	 * @param gene2
	 */
	public GenePair(Gene gene1, Gene gene2) {
		this.gene1 = gene1;
		this.gene2 = gene2;
	}
	
	private boolean contains(Gene g) {
		if(gene1.equals(g) || gene2.equals(g)) {
			return true;
		}
		return false;
	}
	
	public boolean equals(Object o) {
		if(o == this)
			return true;
		if(!(o instanceof GenePair)) {
			return false;
		}
		
		boolean a = ((GenePair)o).gene1.equals(gene1) && ((GenePair)o).gene2.equals(gene2);
		boolean b = ((GenePair)o).gene1.equals(gene2) && ((GenePair)o).gene2.equals(gene1);
		
		return a || b;
	}
	
	public int hashCode() {
		String name1 = gene1.getName();
		String name2 = gene2.getName();
		String both = name1+name2;
		
		if(name1.compareTo(name2) > 0)
			both = name2+name1;
		
		return both.hashCode();
	}

	/**
	 * @param genes
	 * @return true if both genes of this paor are contained in the set
	 */
	public boolean containedIn(Set<Gene> genes) {
		boolean contains = false;
		boolean oneFound = false;
		for(Gene g: genes) {
			if(contains(g)) {
				if(oneFound) {
					contains = true;
				} else {
					oneFound = true;
				}
			}
		}
		return contains;
	}
}
