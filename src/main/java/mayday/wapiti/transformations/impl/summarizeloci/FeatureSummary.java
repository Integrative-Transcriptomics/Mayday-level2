package mayday.wapiti.transformations.impl.summarizeloci;

import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.typed.AveragingSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class FeatureSummary extends AbstractTransformationPlugin {

	protected AveragingSetting mySetting = new AveragingSetting();

	public FeatureSummary() {}

	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps) {
			if (!(FeatureExpressionData.class.isAssignableFrom(e.getDataClass())) || !e.hasLocusInformation())
				return false;
			if (ChannelCount.isMultiChannel(e.getDataProperties()))
				return false;
		}
		return true;
	}
	
	public String getApplicabilityRequirements() {
		return "Requires single-channel feature expression data with locus information";
	}


	public void compute() {		
		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			transMatrix.setIntermediateData(e, 
					new FeatureSummaryExperimentData(e, (FeatureExpressionData)ex, mySetting)
			);
		}	}

	public AveragingSetting getSetting() {
		return mySetting;
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		ExperimentState es = new SummaryExperimentState(inputState, null);
		return es;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".CombineFeatures", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Combine several features into one expression level for a given locus.", 
		"Combine Features");
	}

}
