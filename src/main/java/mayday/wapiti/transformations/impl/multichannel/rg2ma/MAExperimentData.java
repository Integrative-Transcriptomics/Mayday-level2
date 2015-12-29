package mayday.wapiti.transformations.impl.multichannel.rg2ma;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.ProcessedFeatureExpressionData;

public class MAExperimentData extends ProcessedFeatureExpressionData {

	protected boolean isLogged;
	protected int c1,c2;
	
	public MAExperimentData(FeatureExpressionData input, boolean isLogged, int c1, int c2) {
		super(input);
		this.isLogged=isLogged;
		this.c1=c1;
		this.c2=c2;
	}
	
	public Double getExpression(int channel, String featureName) {
		if (channel==0) {
			Double inA = input.getExpression(c1, featureName);
			Double inB = input.getExpression(c2, featureName);
			if (inA!=null && inB!=null)
				return isLogged?
						inA-inB : inA/inB;
		} else if (channel==1) {
			Double inA = input.getExpression(c1, featureName);
			Double inB = input.getExpression(c2, featureName);
			if (inA!=null && inB!=null)
				return isLogged?
						(inA+inB)/2.0 : Math.sqrt(inA*inB);
		}
		return null;
	}

	public Double getBackground(int channel, String featureName) {
		return null;
	}
	
	public AbstractVector getBackgroundVector(int channel) {
		return null;
	}

	public boolean hasBackground() {
		return false;
	}

	protected Double process(double input) {
		// never ever called
		return null;
	}
	
	public int getNumberOfChannels() {
		return 2;
	}
	
	public String getChannelName(int channel) {
		if (channel==0)
			return "M";
		if (channel==1)
			return "A";
		return null;
	}
}	