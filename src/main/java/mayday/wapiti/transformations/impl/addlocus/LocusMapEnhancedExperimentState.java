package mayday.wapiti.transformations.impl.addlocus;

import mayday.core.structures.ReferenceCache;
import mayday.genetics.advanced.LocusData;
import mayday.genetics.locusmap.LocusMap;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.ExperimentState;

public class LocusMapEnhancedExperimentState extends AbstractExperimentState {


	private long cachedNoL=-1;
	private LocusMap lm;
	
	// always cache LAST USED instance of LocusData
	private static ReferenceCache<LocusData> cache = new ReferenceCache<LocusData>();

	public LocusMapEnhancedExperimentState(ExperimentState previousState, LocusMap lm) {
		super(previousState);
		this.lm = lm;
	}

	public boolean hasLocusInformation() {
		getNumberOfLoci();
		return cachedNoL!=-1;
	}

	public long getNumberOfLoci() {
		if (cachedNoL==-1)
			if (lm!=null) 
				cachedNoL = lm.numberOfLoci(inputState.featureNames());
			else return 0;
		return cachedNoL;
	}

	public LocusData getLocusData() {
		LocusData loda = cache.getCache(inputState.featureNames(), lm);
		if (loda==null) {
			loda = lm.subsetLocusMap(inputState.featureNames());
			cache.setCache(loda,inputState.featureNames(), lm);
		}
		return loda;
	}

}
