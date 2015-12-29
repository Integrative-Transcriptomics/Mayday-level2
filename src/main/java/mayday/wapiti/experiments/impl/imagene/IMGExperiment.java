package mayday.wapiti.experiments.impl.imagene;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.experiments.base.ExperimentSetting;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;
import mayday.wapiti.experiments.impl.microarray.MicroarrayExperiment;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class IMGExperiment extends MicroarrayExperiment {

	protected String IMGFileNamePrefix;
	protected String[] imgFiles;
	protected String method;
	
	public IMGExperiment(TransMatrix transMatrix, String prefix, String[] imgFiles, String description, String method,
			AbstractVector redF, AbstractVector redB, AbstractVector greenF, AbstractVector greenB, AbstractVector flags,
			ArrayLayout layout) {
		super( prefix, "Imported ImaGene files: "+description, transMatrix );
		IMGFileNamePrefix = prefix;
		this.imgFiles = imgFiles;
		this.method = method;
		initialData = new IMGExperimentData(layout, redF, redB, greenF, greenB, flags);
	}
	
	public String getIdentifier() {		
		return IMGFileNamePrefix;
	}
	
	protected ExperimentSetting makeSetting(String name) {
		return new ExperimentSetting(this, name); // no further settings
	}
	
	protected ExperimentState makeInitialState() {
		return new IMGExperimentInitialState(this);
	}

	@Override
	public AbstractExperimentSerializer<IMGExperiment> getSerializer() {
		return new IMGExperimentSerializer();
	}
	
}
