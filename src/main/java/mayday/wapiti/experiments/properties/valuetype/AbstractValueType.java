package mayday.wapiti.experiments.properties.valuetype;

import mayday.wapiti.experiments.properties.PropertyParticle;

public abstract class AbstractValueType extends PropertyParticle {

	@Override
	protected boolean compatibleWith(PropertyParticle other) {
		return (!(other instanceof AbstractValueType));
	}

}
