package mayday.wapiti.experiments.generic.featureexpression;

import mayday.wapiti.experiments.base.AbstractExperiment;
import mayday.wapiti.experiments.base.ExperimentSetting;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.transformations.matrix.TransMatrix;

public abstract class FeatureExpressionExperiment extends AbstractExperiment {

	protected ExperimentSetting setting;
	
	public FeatureExpressionExperiment(String name, String sourceDescription, TransMatrix transMatrix) {
		super(sourceDescription, null, transMatrix);
		setInitialState(makeInitialState());
		setting = makeSetting(name);
	}
	
	protected abstract ExperimentSetting makeSetting(String name);
	
	protected abstract ExperimentState makeInitialState();
	
	public ExperimentSetting getSetting() {
		return setting;
	}	

}
