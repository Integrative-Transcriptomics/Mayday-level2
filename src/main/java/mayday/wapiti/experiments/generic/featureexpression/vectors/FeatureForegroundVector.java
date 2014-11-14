package mayday.wapiti.experiments.generic.featureexpression.vectors;

import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;

public class FeatureForegroundVector extends AbstractFeatureVector {

	protected int channel;
	
	public FeatureForegroundVector(FeatureExpressionData data, int channel) {
		super(data);
		this.channel = channel;
	}
	
	@Override
	protected double get0(int i) {
		Double d = data.getExpression(channel,getName0(i));
		if (d==null)
			d = Double.NaN;
		return d;
	}


}
