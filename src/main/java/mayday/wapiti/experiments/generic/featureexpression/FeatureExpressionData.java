package mayday.wapiti.experiments.generic.featureexpression;

import java.util.HashMap;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;


/** This is the base type for things like microarray expression data */

public interface FeatureExpressionData extends ExperimentData {

	public Iterable<String> featureNames();
	public long getNumberOfFeatures();		
	
	public boolean hasBackground();
	public boolean hasFlags();
	public int getNumberOfChannels();
	
	public Double getExpression(int channel, String featureName);
	public Double getBackground(int channel, String featureName);
	public Double getFlag(String featureName);	
	
	public AbstractVector getExpressionVector(int channel);	
	public AbstractVector getBackgroundVector(int channel);	
	public AbstractVector getFlagVector();	
	
	public HashMap<String, Double> getFlagTypes();
	
	public String getChannelName(int channel);
	public String[] getChannelNames();
	
	public ArrayLayout getArrayLayout();
	
}
