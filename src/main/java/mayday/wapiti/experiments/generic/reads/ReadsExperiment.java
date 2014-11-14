package mayday.wapiti.experiments.generic.reads;

import mayday.core.meta.MIGroup;
import mayday.transkriptorium.data.MappingStore;
import mayday.transkriptorium.meta.MappingStoreMIO;
import mayday.wapiti.experiments.base.AbstractExperiment;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentSetting;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class ReadsExperiment  extends AbstractExperiment {

	protected ExperimentSetting setting;	
	protected ReadsData initialData;
	
	public ReadsExperiment(String name, String sourceDescription, TransMatrix transMatrix) {
		super(sourceDescription, null, transMatrix);
		setInitialState(new ReadsInitialState(this));
		setting = makeSetting(name);
	}
	
	public void setInitialData(ReadsData rd) {
		initialData = rd;
		
		MIGroup mg = transMatrix.getCommonMIGroup("Mapped Reads", MappingStoreMIO._myType);
		
		MappingStore theStore = rd.getFullData();
		MappingStoreMIO msm = new MappingStoreMIO();
		msm.setValue(theStore);
		mg.add(this, msm);
		
		addAnnotation(mg);
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
		return "seqdata+";
	}

	public AbstractExperimentSerializer<ReadsExperiment> getSerializer() {
		return new ReadsExperimentSerializer_2();
	}

}
