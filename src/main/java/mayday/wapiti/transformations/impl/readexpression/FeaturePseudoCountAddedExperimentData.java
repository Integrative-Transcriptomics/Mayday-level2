package mayday.wapiti.transformations.impl.readexpression;

import java.util.HashMap;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;

public class FeaturePseudoCountAddedExperimentData implements FeatureExpressionData {

	protected FeatureExpressionData input;
	protected int pseudo;
	
	public FeaturePseudoCountAddedExperimentData(FeatureExpressionData input, int pseudo) {
		this.input = input;
		this.pseudo=pseudo;
	}

	@Override
	public Iterable<String> featureNames() {
		return input.featureNames();
	}

	@Override
	public long getNumberOfFeatures() {
		return input.getNumberOfFeatures();
	}

	@Override
	public boolean hasBackground() {
		return input.hasBackground();
	}

	@Override
	public boolean hasFlags() {
		return input.hasFlags();
	}

	@Override
	public int getNumberOfChannels() {
		return input.getNumberOfChannels();
	}

	@Override
	public Double getExpression(int channel, String featureName) {
		return input.getExpression(channel, featureName)+pseudo;
	}

	@Override
	public Double getBackground(int channel, String featureName) {
		return input.getBackground(channel, featureName)+pseudo;
	}

	@Override
	public Double getFlag(String featureName) {
		return input.getFlag(featureName);
	}

	@Override
	public AbstractVector getExpressionVector(int channel) {
		AbstractVector av = input.getExpressionVector(channel);
		av.add(pseudo);
		return av;
	}

	@Override
	public AbstractVector getBackgroundVector(int channel) {
		AbstractVector av = input.getBackgroundVector(channel);
		av.add(pseudo);
		return av;	}

	@Override
	public AbstractVector getFlagVector() {
		return input.getFlagVector();
	}

	@Override
	public HashMap<String, Double> getFlagTypes() {
		return input.getFlagTypes();
	}

	@Override
	public String getChannelName(int channel) {
		return input.getChannelName(channel);
	}

	@Override
	public String[] getChannelNames() {
		return input.getChannelNames();
	}

	@Override
	public ArrayLayout getArrayLayout() {
		return input.getArrayLayout();
	}

	
}	