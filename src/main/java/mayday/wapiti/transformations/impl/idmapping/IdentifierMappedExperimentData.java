package mayday.wapiti.transformations.impl.idmapping;

import java.util.Iterator;

import mayday.wapiti.containers.identifiermapping.IdentifierMap;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.ProcessedFeatureExpressionData;

public class IdentifierMappedExperimentData extends ProcessedFeatureExpressionData {

	protected IdentifierMap idmap;
	
	public IdentifierMappedExperimentData(FeatureExpressionData input, IdentifierMap idmap) {
		super(input);
		this.idmap = idmap;
	}
	
	public Iterable<String> featureNames() {
		return new Iterable<String>() {
			public Iterator<String> iterator() {
				return new Iterator<String>() {
					
					protected Iterator<String> inputIT = input.featureNames().iterator();
					
					public boolean hasNext() {
						return inputIT.hasNext();
					}

					public String next() {
						return idmap.map(inputIT.next());
					}

					public void remove() {
						inputIT.remove();
					}
					
				};
			}
		};
	}


	public Double getBackground(int channel, String featureName) {
		return input.getBackground(channel, idmap.mapReverse(featureName));
	}

	public Double getExpression(int channel, String featureName) {
		return input.getExpression(channel, idmap.mapReverse(featureName));
	}
	public Double getFlag(String featureName) {
		return input.getFlag(idmap.mapReverse(featureName));
	}

	@Override
	protected Double process(double input) {
		// never used
		return null;
	}


}
