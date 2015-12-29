package mayday.wapiti.experiments.generic.mapping;

import mayday.genetics.advanced.LocusData;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.valuetype.MappedReads;

public class MappingInitialState implements ExperimentState {

	protected final MappingExperiment mappingExperiment;
	
	public MappingInitialState(MappingExperiment mappingExperiment) {
		this.mappingExperiment = mappingExperiment;
	}

	public DataProperties getDataProperties() {
		DataProperties f = new DataProperties();
		f.add(new MappedReads());
		return f;
	}

	public long getNumberOfLoci() {
		return ((MappingData)mappingExperiment.getInitialData()).getReadCount();
	}

	public boolean hasLocusInformation() {
		return true;
	}

	public Iterable<String> featureNames() {
		return null;
	}

	public LocusData getLocusData() {
		return (MappingData)mappingExperiment.getInitialData();
	}

	public long getNumberOfFeatures() {
		return ((MappingData)mappingExperiment.getInitialData()).getReadCount();
	}

	public Class<? extends ExperimentData> getDataClass() {
		return mappingExperiment.getInitialData().getClass();
	}
	
	
	
}