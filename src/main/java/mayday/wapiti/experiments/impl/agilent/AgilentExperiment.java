package mayday.wapiti.experiments.impl.agilent;

import java.io.File;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.experiments.base.ExperimentSetting;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;
import mayday.wapiti.experiments.impl.microarray.MicroarrayExperiment;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class AgilentExperiment extends MicroarrayExperiment {

	protected String FileName;
	
	public AgilentExperiment(TransMatrix transMatrix, String fileName, 
			AbstractVector redF, AbstractVector redB, AbstractVector greenF, AbstractVector greenB,
			ArrayLayout layout) {
		super( new File(fileName).getName(), "Imported Agilent file: "+new File(fileName).getName(), transMatrix );
		FileName = fileName;
		initialData = new AgilentExperimentData(layout, redF, redB, greenF, greenB, null);
	}
	
	public String getIdentifier() {		
		return FileName;
	}
	
	protected ExperimentSetting makeSetting(String name) {
		return new ExperimentSetting(this, name); // no further settings
	}
	
	protected ExperimentState makeInitialState() {
		return new AgilentExperimentInitialState(this);
	}

	public AbstractExperimentSerializer<AgilentExperiment> getSerializer() {
		return new AgilentExperimentSerializer();
	}
	
}
