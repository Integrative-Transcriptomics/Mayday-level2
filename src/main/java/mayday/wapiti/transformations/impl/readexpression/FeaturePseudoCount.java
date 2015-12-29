package mayday.wapiti.transformations.impl.readexpression;

import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.IntSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class FeaturePseudoCount extends AbstractTransformationPlugin {
	
	protected IntSetting pseudo = new IntSetting("Pseudocounts to add",null,1,1,null,true,false);
	
	public FeaturePseudoCount() {}

	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps)
			if (!(FeatureExpressionData.class.isAssignableFrom(e.getDataClass())))
				return false;
		return true;
	}
	
	public String getApplicabilityRequirements() {
		return "Requires feature expression.";
	}

	public void compute() {		
		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			transMatrix.setIntermediateData(e, 
					new FeaturePseudoCountAddedExperimentData((FeatureExpressionData)ex, pseudo.getIntValue())
			);
		}	
	}

	public Setting getSetting() {
		return pseudo;
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		return inputState;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".AddPseudoCountOnFeatures", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Add a pseudo count of one (unit) to each feature. This allows, e.g. to take the logarithm afterwards.", 
		"Add pseudo count");
	}
	
	public String getIdentifier() {
		return "+pseudo";
	}

}
