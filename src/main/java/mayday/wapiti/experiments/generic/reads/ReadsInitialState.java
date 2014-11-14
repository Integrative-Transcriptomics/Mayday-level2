package mayday.wapiti.experiments.generic.reads;

import mayday.genetics.advanced.LocusData;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.valuetype.MappedReads;

public class ReadsInitialState implements ExperimentState {

	protected final ReadsExperiment mappingExperiment;
	
	public ReadsInitialState(ReadsExperiment mappingExperiment) {
		this.mappingExperiment = mappingExperiment;
	}

	public DataProperties getDataProperties() {
		DataProperties f = new DataProperties();
		f.add(new MappedReads());
		return f;
	}

	public long getNumberOfLoci() {
		return ((ReadsData)mappingExperiment.getInitialData()).getReadCount();
	}

	public boolean hasLocusInformation() {
		return true;
	}

	public Iterable<String> featureNames() {
		return null;
	}

	public LocusData getLocusData() {
		return (ReadsData)mappingExperiment.getInitialData();
	}

	public long getNumberOfFeatures() {
		return ((ReadsData)mappingExperiment.getInitialData()).getReadCount();
	}

	public Class<? extends ExperimentData> getDataClass() {
		return mappingExperiment.getInitialData().getClass();
	}
	
	
	
}