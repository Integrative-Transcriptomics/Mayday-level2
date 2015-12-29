package mayday.wapiti.transformations.impl.summarizefeatures;

import mayday.wapiti.containers.featuresummarization.IFeatureSummarizationMap;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;

public class FeatureSummarizedExperimentState extends AbstractExperimentState {

	protected IFeatureSummarizationMap idmap;
	
	public FeatureSummarizedExperimentState(ExperimentState previousState, IFeatureSummarizationMap idmap) {
		super(previousState);
		this.idmap = idmap;
	}
	
	public Iterable<String> featureNames() {
		return idmap.featureNames();
	}
	
	@Override
	public long getNumberOfFeatures() {
		return idmap.featureNames().size();
	}
	
	public Class<? extends ExperimentData> getDataClass() {
		return FeatureExpressionData.class;
	}


}
