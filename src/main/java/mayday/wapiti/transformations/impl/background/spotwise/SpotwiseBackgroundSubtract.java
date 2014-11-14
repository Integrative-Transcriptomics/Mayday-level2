package mayday.wapiti.transformations.impl.background.spotwise;

import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.properties.channels.SpotWiseBG;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;
import mayday.wapiti.transformations.impl.background.BackgroundCorrectedExperimentState;

public class SpotwiseBackgroundSubtract extends AbstractTransformationPlugin {

	protected DoubleSetting minimalForeground = new DoubleSetting("Minimum foreground value",
			"Specifiy the minimal value (epsilon) for a feature after background correction.\nThe corrected value will be max(epsilon, fg-bg).",0.1);
	

	public SpotwiseBackgroundSubtract() {}
	
	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps) {
			boolean isFED = FeatureExpressionData.class.isAssignableFrom(e.getDataClass());
			boolean hasBackground = e.getDataProperties().getType(SpotWiseBG.class)!=null; 
			if (!isFED || !hasBackground)
				return false;
		}
		return true;
	}
	
	public String getApplicabilityRequirements() {
		return "Requires feature expression data with a spotwise background channel.";
	}

	public void compute() {
		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			transMatrix.setIntermediateData(e, 
					new SpotwiseBGSubtractedExperimentData((FeatureExpressionData)ex, minimalForeground.getDoubleValue())
			);
		}	
	}

	public Setting getSetting() {
		return minimalForeground;
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		ExperimentState es = new BackgroundCorrectedExperimentState(inputState);
		return es;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".SpotwiseBG", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Perform spot-wise background subtraction", 
		"Background Subtraction");
	}
	
	public String getIdentifier() {
		return "FG-BG";
	}

}
