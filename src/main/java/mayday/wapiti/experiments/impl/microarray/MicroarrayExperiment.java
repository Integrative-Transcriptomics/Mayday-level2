package mayday.wapiti.experiments.impl.microarray;

import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionExperiment;
import mayday.wapiti.transformations.matrix.TransMatrix;

public abstract class MicroarrayExperiment extends FeatureExpressionExperiment {
	
	protected AbstractFeatureExpressionData initialData;
	
	public MicroarrayExperiment(String name, String sourceDescription, TransMatrix transMatrix) {
		super(name, sourceDescription, transMatrix);
	}
	
	public ExperimentData getInitialData() {
		return initialData;
	}
	
}
