package mayday.wapiti.transformations.impl.summarizeloci;

import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;
import mayday.wapiti.experiments.properties.DataProperties;

public class SummaryExperimentState extends AbstractExperimentState {

	protected DataProperties myDP;
	
	public SummaryExperimentState(ExperimentState previousState, DataProperties dp) {
		super(previousState);
		myDP = dp;
	}

	public Class<? extends ExperimentData> getDataClass() {
		return LocusExpressionData.class;
	}
	
	public DataProperties getDataProperties() {
		if (myDP!=null)
			return myDP;
		return super.getDataProperties();
	}
	
}
