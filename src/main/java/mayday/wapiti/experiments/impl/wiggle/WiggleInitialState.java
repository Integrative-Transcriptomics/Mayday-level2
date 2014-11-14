package mayday.wapiti.experiments.impl.wiggle;

import mayday.genetics.advanced.LocusData;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.valuetype.AbsoluteExpression;

public class WiggleInitialState implements ExperimentState {

	protected final WiggleExperiment wiggleExperiment;
	
	public WiggleInitialState(WiggleExperiment mappingExperiment) {
		this.wiggleExperiment = mappingExperiment;
	}

	public DataProperties getDataProperties() {
		DataProperties f = new DataProperties();
		f.add(new AbsoluteExpression());
		// TODO
		return f;
	}

	public long getNumberOfLoci() {
		return 0;
	}

	public boolean hasLocusInformation() {
		return true;
	}

	public Iterable<String> featureNames() {
		return null;
	}

	public LocusData getLocusData() {
		return (LocusData)((ConcreteWiggleData)wiggleExperiment.getInitialData()).wig;
	}

	public long getNumberOfFeatures() {
		return 0;
	}

	public Class<? extends ExperimentData> getDataClass() {
		return wiggleExperiment.getInitialData().getClass();
	}
	
	
	
}