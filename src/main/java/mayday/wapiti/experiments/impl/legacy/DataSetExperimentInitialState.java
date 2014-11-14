package mayday.wapiti.experiments.impl.legacy;

import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionExperiment;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionInitialState;
import mayday.wapiti.experiments.properties.DataProperties;

public class DataSetExperimentInitialState extends FeatureExpressionInitialState {

	public DataSetExperimentInitialState(FeatureExpressionExperiment dataSetExperiment) {
		super(dataSetExperiment);
	}

	public DataProperties getDataProperties() {
		return ((DataSetExperimentSetting)(featureExpressionExperiment.getSetting())).getDataProperties();
	}
	
}