package mayday.wapiti.experiments.properties.datamode;

import mayday.wapiti.experiments.properties.PropertyParticle;

public abstract class AbstractDataMode extends PropertyParticle {

	@Override
	protected boolean compatibleWith(PropertyParticle other) {
		// by definition, data modes are not compatible
		return (!(other instanceof AbstractDataMode));
	}

}
