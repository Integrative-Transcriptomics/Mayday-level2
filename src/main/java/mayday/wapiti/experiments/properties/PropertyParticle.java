package mayday.wapiti.experiments.properties;


public abstract class PropertyParticle implements Comparable<PropertyParticle>{
	
	public abstract String toString();
	
	public boolean compatible(PropertyParticle other) {
		return compatibleWith(other) && other.compatibleWith(this); // symmetry needed here!
	}
	
	protected abstract boolean compatibleWith(PropertyParticle other);

	public int compareTo(PropertyParticle o) {
		return o.getClass().getCanonicalName().compareTo(this.getClass().getCanonicalName());
	}
	
	public boolean equals(PropertyParticle o) {
		return compareTo(o)==0;
	}
	
	public int hashCode() {
		return getClass().hashCode();
	}
	
//	public static StateParticle commonAncestor(StateParticle... states) {
//		
//	}

}
