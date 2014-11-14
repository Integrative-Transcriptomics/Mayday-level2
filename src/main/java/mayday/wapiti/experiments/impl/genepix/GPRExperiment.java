package mayday.wapiti.experiments.impl.genepix;

import java.io.File;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.experiments.base.ExperimentSetting;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;
import mayday.wapiti.experiments.impl.microarray.MicroarrayExperiment;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class GPRExperiment extends MicroarrayExperiment {

	protected String GPRFileName;
	protected String fVal, bVal;
	
	public GPRExperiment(TransMatrix transMatrix, String fileName, String fVal, String bVal, 
			AbstractVector redF, AbstractVector redB, AbstractVector greenF, AbstractVector greenB, AbstractVector flags,
			ArrayLayout layout) {
		super( new File(fileName).getName(), "Imported GenePix file: "+new File(fileName).getName(), transMatrix );
		GPRFileName = fileName;
		initialData = new GPRExperimentData(layout, redF, redB, greenF, greenB, flags);
		this.fVal = fVal;
		this.bVal = bVal;
	}
	
	public String getIdentifier() {		
		return GPRFileName;
	}
	
	protected ExperimentSetting makeSetting(String name) {
		return new ExperimentSetting(this, name); // no further settings
	}
	
	protected ExperimentState makeInitialState() {
		return new GPRExperimentInitialState(this);
	}

	public AbstractExperimentSerializer<GPRExperiment> getSerializer() {
		return new GPRExperimentSerializer();
	}
	
}
