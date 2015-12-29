package mayday.gsanalysis;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class Geneset implements Comparable<Geneset>{
	protected String name;
	protected Set<String> genes;
	protected boolean removed=false;
	
	public Geneset(String name) {
		this.name=name;
		genes = new TreeSet<String>();
	}
	
	public Geneset(String name, Set<String> genes) {
		this.name=name;
		this.genes=genes;
	}
	
	public void addGene(String name) {
		genes.add(name);
	}
	
	public void addGenes(Collection<String> names) {
		genes.addAll(names);
	}
	
	public void removeGene(String name) {
		genes.remove(name);
	}
	
	public void removeGenes(Collection<String> names) {
		genes.removeAll(names);
	}

	public void removeAllGenes() {
		genes.clear();
	}
	public String getName() {
		return name;
	}
	
	public Set<String> getGenes() {
		return genes;
	}

	@Override
	public int compareTo(Geneset g) {
		return getName().compareTo(g.getName());
	}
	
	public boolean getRemoved() {
		return removed;
	}
	
	public void setRemoved(boolean r) {
		removed = r;
	}
	
}
