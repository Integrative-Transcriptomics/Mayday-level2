package mayday.wapiti.experiments.generic.featureexpression.vectors;

import java.util.ArrayList;
import java.util.List;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;

public abstract class AbstractFeatureVector extends AbstractVector {

	protected List<String> featureNames;
	protected FeatureExpressionData data;
	
	public AbstractFeatureVector(FeatureExpressionData data) {
		this.data=data;
		Iterable<String> is = data.featureNames();
		if (is instanceof List<?>)
			featureNames = (List<String>)is;
		else {
			featureNames = new ArrayList<String>();
			for (String s : is)
				featureNames.add(s);
		}
	}
	
	@Override
	protected String getName0(int i) {
		return featureNames.get(i);
	}

	protected void set0(int i, double v) {
		// NO CAN DO //TODO exception?
	}

	protected void setName0(int i, String name) {
		// NO CAN DO
	}

	@Override
	public int size() {
		return featureNames.size();
	}

}
