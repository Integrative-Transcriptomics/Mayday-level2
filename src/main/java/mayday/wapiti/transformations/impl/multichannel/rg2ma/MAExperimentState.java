package mayday.wapiti.transformations.impl.multichannel.rg2ma;

import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.PropertyParticle;
import mayday.wapiti.experiments.properties.channels.BackgroundType;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.experiments.properties.valuetype.RelativeExpression;

public class MAExperimentState extends AbstractExperimentState {

	public MAExperimentState(ExperimentState previousState) {
		super(previousState);
	}

	public DataProperties getDataProperties() {
		DataProperties dp = inputState.getDataProperties().clone();
		// remove background channel information
 		PropertyParticle pp = dp.getType(BackgroundType.class);
 		// there can only be one background type
 		dp.remove(pp);
 		// set type of data
 		dp.add(new RelativeExpression(), true);
 		dp.add(new ChannelCount(2, new String[]{"M (fold-change)","A (average expression)"}) , true);
 		return dp;
	}
	
}
