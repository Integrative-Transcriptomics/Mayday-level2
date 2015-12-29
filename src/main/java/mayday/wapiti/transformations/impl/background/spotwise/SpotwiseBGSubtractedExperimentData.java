package mayday.wapiti.transformations.impl.background.spotwise;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.ProcessedFeatureExpressionData;

public class SpotwiseBGSubtractedExperimentData extends ProcessedFeatureExpressionData {

	protected double epsilon;
	
	public SpotwiseBGSubtractedExperimentData(FeatureExpressionData input, double epsilon) {
		super(input);
		this.epsilon = epsilon;
	}

	public Double getExpression(int channel, String featureName) {
		Double in = input.getExpression(channel, featureName);
		Double inB = input.getBackground(channel, featureName);
		if (in!=null)
			if (inB!=null)
				return Math.max(epsilon, in-inB);
			else
				return Math.max(in, epsilon);
		return null;
	}
	
	public AbstractVector getBackgroundVector(int channel) {
		return null;
	}

	public boolean hasBackground() {
		return false;
	}
	
	public Double getBackground(int channel, String featureName) {
		return null;
	}

	protected Double process(double input) {
		// never ever called
		return null;
	}
	
}	