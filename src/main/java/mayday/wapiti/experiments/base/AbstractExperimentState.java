package mayday.wapiti.experiments.base;

import mayday.genetics.advanced.LocusData;
import mayday.wapiti.experiments.properties.DataProperties;

/** Provides an experiment state that is completely unaltered */
public class AbstractExperimentState implements ExperimentState {

	protected ExperimentState inputState;
	
	public AbstractExperimentState(ExperimentState previousState) {
		this.inputState=previousState;
	}
	
	public DataProperties getDataProperties() {
		return inputState.getDataProperties();
	}

	public long getNumberOfFeatures() {
		return inputState.getNumberOfFeatures();
	}

	public long getNumberOfLoci() {
		return inputState.getNumberOfLoci();
	}

	public boolean hasLocusInformation() {
		return inputState.hasLocusInformation();
	}

	public Iterable<String> featureNames() {
		return inputState.featureNames();
	}

	public Class<? extends ExperimentData> getDataClass() {
		return inputState.getDataClass();
	}

	public LocusData getLocusData() {
		return inputState.getLocusData();
	}

}
