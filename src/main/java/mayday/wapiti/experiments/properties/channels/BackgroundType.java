package mayday.wapiti.experiments.properties.channels;

import mayday.wapiti.experiments.properties.PropertyParticle;

public abstract class BackgroundType extends PropertyParticle {
	
	@Override
	protected boolean compatibleWith(PropertyParticle other) {
		// by default, all background types incompatible with each other
		return (!(other instanceof BackgroundType));
	}

}
