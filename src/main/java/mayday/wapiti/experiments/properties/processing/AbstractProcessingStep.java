package mayday.wapiti.experiments.properties.processing;

import mayday.wapiti.experiments.properties.PropertyParticle;

public abstract class AbstractProcessingStep extends PropertyParticle {

	@Override
	protected boolean compatibleWith(PropertyParticle other) {
		// by default, all processing states are incompatible with each other
		return (!(other instanceof AbstractProcessingStep));
	}
	
}
