package mayday.wapiti.experiments.generic.featureexpression;

import java.util.HashMap;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.generic.featureexpression.vectors.FeatureBackgroundVector;
import mayday.wapiti.experiments.generic.featureexpression.vectors.FeatureForegroundVector;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;

public abstract class ProcessedFeatureExpressionData implements FeatureExpressionData {

	protected FeatureExpressionData input;
	
	public ProcessedFeatureExpressionData(FeatureExpressionData input) {
		this.input = input;
	}

	protected abstract Double process(double input);


	public Double getExpression(int channel, String featureName) {
		Double in = input.getExpression(channel, featureName);
		if (in!=null)
			return process(in);
		return null;
	}

	public Double getBackground(int channel, String featureName) {
		Double in = input.getBackground(channel, featureName);
		if (in!=null)
			return process(in);
		return null;
	}

	
	// pass-through methods below
	

	public Iterable<String> featureNames() {
		return input.featureNames();
	}

	
	public long getNumberOfFeatures() {
		return input.getNumberOfFeatures();
	}
	
	public AbstractVector getBackgroundVector(int channel) {
		return new FeatureBackgroundVector(this, channel);
	}

	public String getChannelName(int channel) {		
		return input.getChannelName(channel);
	}

	public AbstractVector getExpressionVector(int channel) {
		return new FeatureForegroundVector(this, channel);
	}

	public Double getFlag(String featureName) {
		return input.getFlag(featureName);
	}

	public HashMap<String, Double> getFlagTypes() {
		return input.getFlagTypes();
	}

	public AbstractVector getFlagVector() {
		return input.getFlagVector();
	}

	public int getNumberOfChannels() {
		return input.getNumberOfChannels();
	}

	public boolean hasBackground() {
		return input.hasBackground();
	}

	public boolean hasFlags() {
		return input.hasFlags();
	}
	
	public String[] getChannelNames() {
		return input.getChannelNames();
	}
	

	public ArrayLayout getArrayLayout() {
		return input.getArrayLayout();
	}
	
}	