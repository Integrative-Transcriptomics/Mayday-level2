package mayday.wapiti.transformations.impl.unlog;

import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.ProcessedFeatureExpressionData;

public class UnloggedFeatureExpressionExperimentData extends ProcessedFeatureExpressionData {

	protected double base;
	
	public UnloggedFeatureExpressionExperimentData(FeatureExpressionData input, double base) {
		super(input);
		this.base = base;
	}
	
	protected Double process(double in) {
		double i = Math.pow(base, in);
		return i;
	}
	
}	