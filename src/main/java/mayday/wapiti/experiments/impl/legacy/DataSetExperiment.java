package mayday.wapiti.experiments.impl.legacy;

import mayday.core.DataSet;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentSetting;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionExperiment;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class DataSetExperiment extends FeatureExpressionExperiment {

	protected DataSetExperimentData initialData;
	
	public DataSetExperiment(TransMatrix transMatrix, DataSet ds, int index) {
		super(  /*name*/ 	/*	ds.getName()+": "+*/ ds.getMasterTable().getExperimentName(index), 
				/*sourcedesc */ "Column "+index+" (\""+ds.getMasterTable().getExperimentName(index)+"\") from DataSet "+ds.getName(),
				transMatrix);
		initialData = new DataSetExperimentData(ds,index);
	}
	
	public String getIdentifier() {		
		return initialData.base.getName()+"["+initialData.experimentIndex+"]";
	}

	public ExperimentData getInitialData() {
		return initialData;
	}

	@Override
	protected ExperimentState makeInitialState() {
		return new DataSetExperimentInitialState(this);
	}

	@Override
	protected ExperimentSetting makeSetting(String name) {
		return new DataSetExperimentSetting(this, name);
	}

	public AbstractExperimentSerializer<FeatureExpressionExperiment> getSerializer() {
		return new DataSetExperimentSerializer();
	}
	

	
}
