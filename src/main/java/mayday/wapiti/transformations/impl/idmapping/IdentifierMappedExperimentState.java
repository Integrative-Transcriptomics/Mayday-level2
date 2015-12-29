package mayday.wapiti.transformations.impl.idmapping;

import java.util.Iterator;

import mayday.core.structures.ReferenceCache;
import mayday.wapiti.containers.identifiermapping.IdentifierMap;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;

public class IdentifierMappedExperimentState extends AbstractExperimentState {

	protected IdentifierMap idmap;
	
	protected static ReferenceCache<Iterable<String>> cache = new ReferenceCache<Iterable<String>>();
	
	public IdentifierMappedExperimentState(ExperimentState previousState, IdentifierMap idmap) {
		super(previousState);
		this.idmap = idmap;
	}
	
	public Iterable<String> featureNames() {
		Iterable<String> it = cache.getCache(idmap, inputState.featureNames());
		
		if (it==null) {
			it = new Iterable<String>() {
				public Iterator<String> iterator() {
					return new Iterator<String>() {
						
						protected Iterator<String> input = inputState.featureNames().iterator();
						
						public boolean hasNext() {
							return input.hasNext();
						}

						public String next() {
							return idmap.map(input.next());
						}

						public void remove() {
							input.remove();
						}
						
					};
				}
			};
			cache.setCache(it, inputState.featureNames());
		}
		
		return it;
	}
	
	public Class<? extends ExperimentData> getDataClass() {
		return FeatureExpressionData.class;
	}


}
