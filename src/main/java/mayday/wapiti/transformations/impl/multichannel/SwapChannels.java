package mayday.wapiti.transformations.impl.multichannel;

import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.ProcessedFeatureExpressionData;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class SwapChannels extends AbstractTransformationPlugin {

	public SwapChannels() {}
	
	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps) {
			boolean isFED = FeatureExpressionData.class.isAssignableFrom(e.getDataClass());
			boolean isTwoChan = ChannelCount.getChannelCount(e.getDataProperties())==2;
			if (!isFED || !isTwoChan)
				return false;
		}
	
		return true;
	}
	
	public String getApplicabilityRequirements() {
		return "Requires two-channel feature expression data.";
	}


	public Setting getSetting() {
		return null;
	}
		
	public void compute() {
		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			FeatureExpressionData fed = ((FeatureExpressionData)ex);
			ProcessedFeatureExpressionData pfed = new ProcessedFeatureExpressionData(fed) {
				
				public Double getExpression(int channel, String featureName) {
					return input.getExpression(1-channel, featureName);
				}

				public Double getBackground(int channel, String featureName) {
					return input.getBackground(1-channel, featureName);
				}
				
				public String[] getChannelNames() {
					String[] channelNames = new String[2];
					channelNames[0] = input.getChannelName(1);
					channelNames[1] = input.getChannelName(0);
					return channelNames;
				}
				
				public String getChannelName(int channel) {		
					return input.getChannelName(1-channel);
				}
				
				protected Double process(double input) {
					// never called
					return null;
				}
			};

			transMatrix.setIntermediateData(e,pfed);
		}	
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		ExperimentState es = new AbstractExperimentState(inputState) {
			public DataProperties getDataProperties() {
				DataProperties dp = inputState.getDataProperties().clone();
				ChannelCount cc = dp.getType(ChannelCount.class);
		 		dp.add(new ChannelCount(2, new String[]{cc.getNames()[1], cc.getNames()[0]}) , true);		 		
		 		return dp;
			}
		};
		return es;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".SwapChannels", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Exchanges the first and second channel of a two-channel experiment.", 
		"Swap channels");
	}
	
	public String getIdentifier() {
		return "Swap Ch.";
	}

}
