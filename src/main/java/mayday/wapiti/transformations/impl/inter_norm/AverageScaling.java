package mayday.wapiti.transformations.impl.inter_norm;

import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.AveragingSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.experiments.properties.processing.Normalized;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class AverageScaling extends AbstractTransformationPlugin {

	protected AveragingSetting averager;
	protected BooleanSetting doScale, doCenter;
	protected HierarchicalSetting mysetting;

	public AverageScaling() {
		averager = new AveragingSetting();
		doCenter = new BooleanSetting("Apply centering","When selected, the distribution of values is centered so that the average is 0.", true);
		averager.setDescription("Select which value to substract during normalization");
		doScale = new BooleanSetting("Apply scaling","When selected, the distribution of values is scaled so that the standard deviation is 1.", true);
		mysetting = new HierarchicalSetting("Average Scaling").addSetting(doCenter).addSetting(averager).addSetting(doScale);
	}
	
	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps) {
			boolean isFED = FeatureExpressionData.class.isAssignableFrom(e.getDataClass());
			DataProperties p = e.getDataProperties();
			boolean isOneChannel = !ChannelCount.isMultiChannel(p);
//			boolean isLogged = Logged.isLogged(p);
			if (!isFED || !isOneChannel) // || !isLogged)
				return false;
		}
		return true;
	}
	
	public String getApplicabilityRequirements() {
		return "Requires single-channel feature expression data";
	}

	public void compute() {
		if (!doCenter.getBooleanValue() && !doScale.getBooleanValue()) {
			System.out.println("AverageScaling was asked to do nothing. OK. Nevermind then.");
			return;
		}
		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			AbstractVector input = ((FeatureExpressionData)ex).getExpressionVector(0);
			AbstractVector output = input.clone();
			if (doCenter.getBooleanValue()) {
				double avg = averager.getSummaryFunction().getAverage(input, true);
				output.add(-avg);
			}
			if (doScale.getBooleanValue())
				output.divide(output.sd(true));
			transMatrix.setIntermediateData(e, new AbstractFeatureExpressionData(output));
		}	
	}

	public Setting getSetting() {
		return mysetting;
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		return new AbstractExperimentState(inputState) {
			public DataProperties getDataProperties() {
				DataProperties dp = inputState.getDataProperties().clone();	
		 		dp.add(new Normalized(), true);
		 		return dp;
			}
		};
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".AverageScaling", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Perform scaling normalization", 
		"Scaling Normalization");
	}
	
	public String getIdentifier() {
		return "Scaling";
	}

}
