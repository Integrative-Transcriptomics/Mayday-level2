package mayday.wapiti.experiments.generic.featureexpression;

import mayday.genetics.advanced.LocusData;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;

public abstract class FeatureExpressionInitialState implements ExperimentState {

	protected final FeatureExpressionExperiment featureExpressionExperiment;
	
	public FeatureExpressionInitialState(FeatureExpressionExperiment namedEntityExperiment) {
		this.featureExpressionExperiment = namedEntityExperiment;
	}

	public long getNumberOfLoci() {
		return 0;
	}

	public boolean hasLocusInformation() {
		return false;
	}

	public Iterable<String> featureNames() {
		return ((FeatureExpressionData)featureExpressionExperiment.getInitialData()).featureNames();
	}

	public LocusData getLocusData() {
		return null;
	}

	public long getNumberOfFeatures() {
		return ((FeatureExpressionData)featureExpressionExperiment.getInitialData()).getNumberOfFeatures();
	}

	public Class<? extends ExperimentData> getDataClass() {
		return featureExpressionExperiment.getInitialData().getClass();
	}
	
	
	
}