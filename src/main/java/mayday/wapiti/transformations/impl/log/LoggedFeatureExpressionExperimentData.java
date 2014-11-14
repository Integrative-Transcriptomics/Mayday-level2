package mayday.wapiti.transformations.impl.log;

import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.ProcessedFeatureExpressionData;

public class LoggedFeatureExpressionExperimentData extends ProcessedFeatureExpressionData {

	protected double baseLog;
	
	public LoggedFeatureExpressionExperimentData(FeatureExpressionData input, double logbase) {
		super(input);
		this.baseLog = 1.0/Math.log(logbase);
	}
	
	protected Double process(double in) {
		double i = Math.log(in)*baseLog;
		if (Double.isInfinite(i))
			i = Double.NaN;
		return i;
	}
	
}	