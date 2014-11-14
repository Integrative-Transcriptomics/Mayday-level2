package mayday.wapiti.experiments.properties.processing;

import mayday.wapiti.experiments.properties.PropertyParticle;


public class Raw extends AbstractProcessingStep {

	public String toString() {
		return "raw";
	}
	
	@Override
	protected boolean compatibleWith(PropertyParticle other) {
		return (!(other instanceof AbstractProcessingStep));
	}

}
