package mayday.wapiti.experiments.properties.channels;

import java.util.HashMap;

import mayday.wapiti.experiments.properties.PropertyParticle;

public class FlagsPresent extends PropertyParticle {

	protected HashMap<String, Double> flagTypes;
	
	public FlagsPresent(HashMap<String, Double> flagTypes) {
		this.flagTypes = flagTypes;
	}
	
	public HashMap<String, Double> getFlagTypes() {
		return flagTypes;
	}
	
	@Override
	public String toString() {
		return "has flag data";
	}

	@Override
	protected boolean compatibleWith(PropertyParticle other) {
		return true;
	}
	
}
