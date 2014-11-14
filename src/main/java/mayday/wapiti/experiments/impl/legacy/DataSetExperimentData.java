package mayday.wapiti.experiments.impl.legacy;

import java.util.HashMap;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.vectors.FeatureForegroundVector;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;

public class DataSetExperimentData implements FeatureExpressionData {

	protected DataSet base;
	protected int experimentIndex;
	
	public DataSetExperimentData(DataSet ds, int index) {
		base = ds;
		experimentIndex = index;
	}
	
	public long getNumberOfFeatures() {
		return base.getMasterTable().getNumberOfProbes();
	}
	
	public Iterable<String> featureNames() {
		return base.getMasterTable().getProbes().keySet();
	}

	public Double getBackground(int channel, String featureName) {
		return null;
	}

	public AbstractVector getBackgroundVector(int channel) {
		return null;
	}

	public Double getExpression(int channel, String featureName) {
		if (channel>0)
			throw new RuntimeException("No channel "+channel+" in DataSetExperimentData");
		if (featureName==null)
			return null;
		Probe pb  = base.getMasterTable().getProbe(featureName);
		if (pb!=null)
			return pb.getValue(experimentIndex);
		return null;
	}

	public AbstractVector getExpressionVector(int channel) {
		return new FeatureForegroundVector(this,channel);
	}

	public Double getFlag(String featureName) {
		return null;
	}

	public HashMap<String, Double> getFlagTypes() {
		return null;
	}

	public AbstractVector getFlagVector() {
		return null;
	}

	public boolean hasBackground() {
		return false;
	}

	public boolean hasFlags() {
		return false;
	}

	public String getChannelName(int channel) {
		return null;
	}

	public int getNumberOfChannels() {
		return 1;
	}

	public String[] getChannelNames() {
		return new String[]{"channel1"};
	}

	public ArrayLayout getArrayLayout() {
		return null;
	}
	
}
