package mayday.wapiti.transformations.impl.background;

import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.PropertyParticle;
import mayday.wapiti.experiments.properties.channels.BackgroundType;
import mayday.wapiti.experiments.properties.processing.BackgroundCorrected;

public class BackgroundCorrectedExperimentState extends AbstractExperimentState {

	public BackgroundCorrectedExperimentState(ExperimentState previousState) {
		super(previousState);
	}

	public DataProperties getDataProperties() {
		DataProperties dp = inputState.getDataProperties().clone();
		// remove background channel information
 		PropertyParticle pp = dp.getType(BackgroundType.class);
 		// there can only be one background type
 		dp.remove(pp);
 		dp.add(new BackgroundCorrected(), true);
 		return dp;
	}
	
}
