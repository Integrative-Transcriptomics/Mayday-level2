package mayday.wapiti.transformations.impl.multichannel;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;

public class DyeSwapFeatureExpressionExperimentData extends AbstractFeatureExpressionData {

	protected int c1, c2;
	protected FeatureExpressionData input1, input2;
	protected String cName;
	
	public DyeSwapFeatureExpressionExperimentData(FeatureExpressionData input1, FeatureExpressionData input2, int c1, int c2, String cName) {
		super((AbstractVector)null);
		this.input1 = input1;
		this.input2 = input2;
		this.c1 = c1;
		this.c2 = c2;
		this.cName = cName;
	}
	
	public Double getExpression(int channel, String featureName) {
		if (channel!=0)
			throw new RuntimeException("Nonexistent channel selected");

		Double in1 = input1.getExpression(c1, featureName);
		Double in2 = input2.getExpression(c2, featureName);
		if (in1==null || in2==null)
			return null;
		
		double out = (in1+in2)/2; 
		
		return out;
	}

	public AbstractVector getExpressionVector(int channel) {
		if (channel!=0)
			throw new RuntimeException("Nonexistent channel selected");

		AbstractVector ex1 = input1.getExpressionVector(c1);
		AbstractVector ex2 = input2.getExpressionVector(c2);
		
		AbstractVector out = ex1.clone();
		out.add(ex2);
		out.divide(2);
		
		return out;
	}
	
	public int getNumberOfChannels() {
		return 1;
	}

	
	public Iterable<String> featureNames() {		
		return input1.featureNames();
	}
	
	public long getNumberOfFeatures() {
		return input1.getNumberOfFeatures();
	}
	
}	