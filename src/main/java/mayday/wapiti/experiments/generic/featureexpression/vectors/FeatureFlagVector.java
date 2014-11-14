package mayday.wapiti.experiments.generic.featureexpression.vectors;

import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;

public class FeatureFlagVector extends AbstractFeatureVector {

	public FeatureFlagVector(FeatureExpressionData data) {
		super(data);
	}
	
	@Override
	protected double get0(int i) {
		Double d = data.getFlag(getName0(i));
		if (d==null)
			d = Double.NaN;
		return d;
	}

}
