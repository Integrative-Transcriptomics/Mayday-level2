package mayday.wapiti.transformations.impl.log;

import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.datamode.Logged;

public class LoggedExpressionExperimentState extends AbstractExperimentState {

	protected Class<? extends ExperimentData> clazz;
	protected double logBase;

	public LoggedExpressionExperimentState(ExperimentState previousState, double logBase, Class<? extends ExperimentData> clazz) {
		super(previousState);
		this.clazz = clazz;
		this.logBase = logBase;
	}

	public Class<? extends ExperimentData> getDataClass() {
		return clazz;
	}
	
	public DataProperties getDataProperties() {
		DataProperties dp = inputState.getDataProperties().clone();
 		dp.add(new Logged(logBase), true);
 		return dp;
	}
	
}
