package mayday.wapiti.transformations.impl.readexpression;

import java.util.Collection;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.datamode.Logged;
import mayday.wapiti.experiments.properties.processing.Normalized;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class ReadLogUnrestricted extends AbstractTransformationPlugin {
	
	public ReadLogUnrestricted() {}

	public boolean applicableTo(Collection<Experiment> exps) {

		for (Experiment e:exps) {
			boolean isFEE = FeatureExpressionData.class.isAssignableFrom(e.getDataClass());

			if (!isFEE) {
				return false;
			}				
		}

		return true;
	}
	
	
	public String getApplicabilityRequirements() {
		return "Requires feature expression data. Only works on the first channel of multi-channel experiments.";
	}

	public void compute() {

		List<Experiment> exps = transMatrix.getExperiments(this);
		
		for (Experiment e : exps) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			AbstractVector ov = ((FeatureExpressionData)ex).getExpressionVector(0);
			AbstractVector v = ov.clone();
			v.log(2.0);
			double min = v.min(true, true);
			double max = v.max(true, true);
			v.replaceInfinity(max+1, min-1);
			min = v.min(true, true);
			v.add(-min);
			v.replaceNA(0);
			v.setNames(ov);
			ExperimentData ed =  new AbstractFeatureExpressionData(v);
			transMatrix.setIntermediateData(e,ed);
		}
	}
	
	protected ExperimentState makeState(ExperimentState inputState) {
		return new AbstractExperimentState(inputState) {
			public DataProperties getDataProperties() {
				DataProperties in = inputState.getDataProperties();
				DataProperties out = in.clone();
				out.add(new Normalized(), true);
				out.add(new Logged(2), true);
				return out;
			}
		};
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".ReadLogEquivalentUnrestricted", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Maps values logarithmically to [0,infinity[." , 
		"Map logarithmically to positive values");
	}
	
	public String getIdentifier() {
		return "Log Positive";
	}

}
