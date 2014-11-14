package mayday.wapiti.experiments.impl.scanarray;

import java.io.File;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.experiments.base.ExperimentSetting;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;
import mayday.wapiti.experiments.impl.microarray.MicroarrayExperiment;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class ScanArrayExperiment extends MicroarrayExperiment {

	protected String FileName;
	protected String fVal, bVal;
	
	public ScanArrayExperiment(TransMatrix transMatrix, String fileName, String fVal, String bVal, 
			AbstractVector redF, AbstractVector redB, AbstractVector greenF, AbstractVector greenB, AbstractVector flags,
			ArrayLayout layout) {
		super( new File(fileName).getName(), "Imported ScanArray file: "+new File(fileName).getName(), transMatrix );
		FileName = fileName;
		initialData = new ScanArrayExperimentData(layout, redF, redB, greenF, greenB, flags);
		this.fVal = fVal;
		this.bVal = bVal;
	}
	
	public String getIdentifier() {		
		return FileName;
	}
	
	protected ExperimentSetting makeSetting(String name) {
		return new ExperimentSetting(this, name); // no further settings
	}
	
	protected ExperimentState makeInitialState() {
		return new ScanArrayExperimentInitialState(this);
	}

	public AbstractExperimentSerializer<ScanArrayExperiment> getSerializer() {
		return new ScanArrayExperimentSerializer();
	}
	
}
