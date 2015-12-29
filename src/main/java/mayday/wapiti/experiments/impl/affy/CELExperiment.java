package mayday.wapiti.experiments.impl.affy;

import java.io.File;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.experiments.base.ExperimentSetting;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.impl.microarray.MicroarrayExperiment;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class CELExperiment extends MicroarrayExperiment {

	protected String CELFileName;
	protected CDFData cdf;
	
	public CELExperiment(TransMatrix transMatrix, String fileName, AbstractVector expression, CDFData cdfData) {
		super( new File(fileName).getName(), "Imported CEL file: "+new File(fileName).getName(), transMatrix );
		CELFileName = fileName;
		cdf = cdfData;
		initialData = new CELExperimentData(cdf.getLayout(), expression, cdfData.pm);
	}
	
	public String getIdentifier() {		
		return CELFileName;
	}
	
	protected ExperimentSetting makeSetting(String name) {
		return new ExperimentSetting(this, name); // no further settings
	}
	
	protected ExperimentState makeInitialState() {
		return new CELExperimentInitialState(this);
	}
		
	public CDFData getCDFData() {
		return cdf;
	}
	
	public AbstractExperimentSerializer<CELExperiment> getSerializer() {
		return new CELExperimentSerializer();
	}
	
}
