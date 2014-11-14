package mayday.wapiti.experiments.impl.affy;

import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionExperiment;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionInitialState;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.experiments.properties.datamode.Unlogged;
import mayday.wapiti.experiments.properties.processing.Raw;
import mayday.wapiti.experiments.properties.valuetype.AbsoluteExpression;

public class CELExperimentInitialState extends FeatureExpressionInitialState {

	public CELExperimentInitialState(FeatureExpressionExperiment dataSetExperiment) {
		super(dataSetExperiment);
	}

	public DataProperties getDataProperties() {
		DataProperties dp = new DataProperties();
		dp.add(new Raw());
		dp.add(new AbsoluteExpression());
		dp.add(new Unlogged());
		dp.add(new ChannelCount(1, null));
		return dp;
	}
	
}