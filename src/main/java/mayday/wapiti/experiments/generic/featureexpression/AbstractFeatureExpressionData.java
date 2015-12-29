package mayday.wapiti.experiments.generic.featureexpression;

import java.util.HashMap;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;

public class AbstractFeatureExpressionData implements FeatureExpressionData {

	protected AbstractVector[] fg;
	protected AbstractVector[] bg;
	protected AbstractVector flags;
	protected HashMap<String, Double> flagTypes; 
	protected String channelNames[];
	
	public AbstractFeatureExpressionData(AbstractVector fg) {
		this(fg, null);
	}
	
	public AbstractFeatureExpressionData(AbstractVector fg, AbstractVector bg) {
		this(null, null, fg, bg);
	}
	
	public AbstractFeatureExpressionData(AbstractVector flags, HashMap<String, Double> flagTypes, AbstractVector fg, AbstractVector bg) {
		this(flags, flagTypes, fg!=null?new AbstractVector[]{fg}:null, bg!=null?new AbstractVector[]{bg}:null, new String[]{"channel1"});
	}

	public AbstractFeatureExpressionData(AbstractVector flags, HashMap<String, Double> flagTypes, AbstractVector[] fg, AbstractVector[] bg, String[] channelNames) {
		this.flags = flags;
		this.fg = fg!=null?fg:new AbstractVector[0];
		this.bg = bg!=null?bg:new AbstractVector[0];
		this.channelNames = channelNames;
		this.flagTypes = flagTypes;
	}
	
	public AbstractFeatureExpressionData(AbstractFeatureExpressionData afed) {
		this(afed.flags, afed.flagTypes, afed.fg, afed.bg, afed.channelNames);
	}
	
	public AbstractFeatureExpressionData(FeatureExpressionData fed) {		
		this.flags = fed.getFlagVector();
		int c = fed.getNumberOfChannels();
		fg = new AbstractVector[c];
		bg = new AbstractVector[c];
		channelNames = new String[c];
		for (int i=0; i!=c; ++i) {
			fg[i] = fed.getExpressionVector(i);
			bg[i] = fed.getBackgroundVector(i);
			channelNames[i] = fed.getChannelName(i);
		}
		this.flagTypes = fed.getFlagTypes();
	}
	
	public Double getBackground(int channel, String featureName) {
		if (channel<bg.length)
			return bg[channel].get(featureName);
		return null;
	}

	public AbstractVector getBackgroundVector(int channel) {
		if (channel<bg.length)
			return bg[channel];
		return null;
	}

	public Double getExpression(int channel, String featureName) {
		if (channel<fg.length)
			return fg[channel].get(featureName);
		return null;
	}

	public AbstractVector getExpressionVector(int channel) {
		if (channel<fg.length)
			return fg[channel];
		return null;
	}

	public Double getFlag(String featureName) {
		if (flags!=null)
			return flags.get(featureName);
		return null;
	}

	public HashMap<String, Double> getFlagTypes() {
		return flagTypes;
	}

	public AbstractVector getFlagVector() {
		return flags;
	}

	public boolean hasBackground() {
		return bg.length!=0;
	}

	public boolean hasFlags() {
		return flags!=null;
	}

	public String getChannelName(int channel) {
		if (channelNames!=null && channel<channelNames.length)
			return channelNames[channel];
		return null;
	}
	
	public String[] getChannelNames() {
		return channelNames;
	}
	
	public int getNumberOfChannels() {
		return fg.length;
	}

	
	public Iterable<String> featureNames() {		
		return fg[0].getNamesList();
	}
	
	public long getNumberOfFeatures() {
		return fg[0].size();
	}

	public ArrayLayout getArrayLayout() {
		return null;
	}
}
