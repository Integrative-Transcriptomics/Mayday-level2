package mayday.wapiti.transformations.impl.createDS;

import mayday.genetics.advanced.LocusData;
import mayday.genetics.locusmap.LocusMap;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.channels.ChannelCount;

public class LocusToFeatureExpressionState extends AbstractExperimentState {

	protected LocusMap featureMap;
	
	public LocusToFeatureExpressionState(ExperimentState locusExprState, LocusMap featureMap) {
		super(locusExprState);
		this.featureMap=featureMap;
	}
	
	public DataProperties getDataProperties() {
		DataProperties dp = inputState.getDataProperties().clone();
		dp.add(new ChannelCount(1, null));
		return dp;
	}

	public long getNumberOfFeatures() {
		return featureMap.size();
	}

	public long getNumberOfLoci() {
		return featureMap.size();//inputState.getNumberOfLoci();
	}

	public boolean hasLocusInformation() {
		return true;//inputState.hasLocusInformation();
	}

	public Iterable<String> featureNames() {
		return featureMap.keySet();
	}

	public Class<? extends ExperimentData> getDataClass() {
		return FeatureExpressionData.class;
	}

	public LocusData getLocusData() {
		return featureMap;
	}
	
}