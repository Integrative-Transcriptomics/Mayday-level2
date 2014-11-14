package mayday.wapiti.transformations.impl.addlocus;

import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.genetics.locusmap.LocusMapSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class AddLocusData extends AbstractTransformationPlugin {

	public LocusMapSetting mySetting = new LocusMapSetting();

	public AddLocusData() {
	}

	public boolean applicableTo(Collection<Experiment> exps) {
		// works only on NamedEntityExperiments
		for (Experiment e:exps)
			if (!FeatureExpressionData.class.isAssignableFrom(e.getDataClass()) || e.hasLocusInformation())
				return false;
		return true;
	}
	
	public String getApplicabilityRequirements() {
		return "Requires feature expression data without locus information.";
	}


	public void compute() {		
		// this transform does not change the experimental data
	}

	public LocusMapSetting getSetting() {
		return mySetting;
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		return new LocusMapEnhancedExperimentState(inputState, mySetting.getLocusMap());
	}
	

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".AddLocus", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Add external Locus information to experiments", 
		"Add Locus");
	}

}
