package mayday.wapiti.experiments.generic.mapping;

import mayday.wapiti.experiments.base.AbstractExperiment;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentSetting;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class MappingExperiment extends AbstractExperiment {

	protected ExperimentSetting setting;	
	protected MappingData initialData;
	
	public MappingExperiment(String name, String sourceDescription, TransMatrix transMatrix) {
		super(sourceDescription, null, transMatrix);
		setInitialState(new MappingInitialState(this));
		setting = makeSetting(name);
	}
	
	public void setInitialData(MappingData md) {
		initialData = md;
	}
	
	protected ExperimentSetting makeSetting(String name) {
		return new ExperimentSetting(this, name);
	}
	
	public ExperimentSetting getSetting() {
		return setting;
	}	
	
	public ExperimentData getInitialData() {
		return initialData;
	}

	public String getIdentifier() {
		return "seqdata";
	}

	public AbstractExperimentSerializer<MappingExperiment> getSerializer() {
		return new MappingExperimentSerializer_v2();
	}

}
