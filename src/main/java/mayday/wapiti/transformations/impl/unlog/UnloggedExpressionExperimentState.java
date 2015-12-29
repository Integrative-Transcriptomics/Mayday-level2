package mayday.wapiti.transformations.impl.unlog;

import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.datamode.Unlogged;

public class UnloggedExpressionExperimentState extends AbstractExperimentState {

	protected Class<? extends ExperimentData> clazz;

	public UnloggedExpressionExperimentState(ExperimentState previousState, Class<? extends ExperimentData> clazz) {
		super(previousState);
		this.clazz = clazz;
	}

	public Class<? extends ExperimentData> getDataClass() {
		return clazz;
	}
	
	public DataProperties getDataProperties() {
		DataProperties dp = inputState.getDataProperties().clone();
 		dp.add(new Unlogged(), true);
 		return dp;
	}
	
}
