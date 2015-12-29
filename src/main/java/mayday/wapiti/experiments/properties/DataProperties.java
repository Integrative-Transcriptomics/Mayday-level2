package mayday.wapiti.experiments.properties;

import java.util.Iterator;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class DataProperties extends LinkedList<PropertyParticle> {

	public boolean containsType(Class<? extends PropertyParticle> spclass) {
		for (PropertyParticle sp : this)
			if (spclass.isAssignableFrom(sp.getClass()))
				return true;
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public <X extends PropertyParticle> X getType(Class<X> spclass) {
		for (PropertyParticle sp : this)
			if (spclass.isAssignableFrom(sp.getClass()))
				return (X)sp;
		return (X)null;		
	}
	
	public boolean add(PropertyParticle p) {
		return add(p, false);
	}
	
	public boolean add(PropertyParticle p, boolean force) {
		Iterator<PropertyParticle> ip = this.iterator();
		while (ip.hasNext())
			if (!ip.next().compatible(p))
				if (force)
					ip.remove();
				else
					return false;		
		return super.add(p);
	}
	
	public DataProperties clone() {
		DataProperties dp = new DataProperties();
		dp.addAll(this);
		return dp;
	}
	
	
}


