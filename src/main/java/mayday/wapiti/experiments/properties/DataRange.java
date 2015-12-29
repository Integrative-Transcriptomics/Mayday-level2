package mayday.wapiti.experiments.properties;

import mayday.wapiti.experiments.properties.valuetype.MappedReads;

public class DataRange extends PropertyParticle {

	protected double min, max;
	
	@Override
	protected boolean compatibleWith(PropertyParticle other) {
		// this is NOT compatible with the mapped reads value type
		if (other instanceof MappedReads)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "("+min+","+max+")";
	}

}
