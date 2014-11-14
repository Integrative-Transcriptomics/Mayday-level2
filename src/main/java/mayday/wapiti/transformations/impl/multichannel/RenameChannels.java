package mayday.wapiti.transformations.impl.multichannel;

import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class RenameChannels extends AbstractTransformationPlugin {

	protected StringSetting channel1, channel2;
	protected HierarchicalSetting mySetting;

	public RenameChannels() {}
	
	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps) {
			boolean isFED = FeatureExpressionData.class.isAssignableFrom(e.getDataClass());
			boolean isTwoChan = ChannelCount.getChannelCount(e.getDataProperties())==2;
			if (!isFED || !isTwoChan)
				return false;
		}

		return true;
	}
	
	public void updateSettings(Collection<Experiment> experiments) {
		Experiment e = experiments.iterator().next();
		ChannelCount c = (ChannelCount)e.getDataProperties().getType(ChannelCount.class);
		if (c!=null) {
			channel1.setDescription("Set a new name for \""+c.getNames()[0]+"\"");
			channel2.setDescription("Set a new name for \""+c.getNames()[1]+"\"");
		}	
	}

	
	public String getApplicabilityRequirements() {
		return "Requires two-channel feature expression data.";
	}

	public Setting getSetting() {
		if (mySetting==null) {
			channel1 = new StringSetting("Channel 1",null,"ch 1");
			channel2 = new StringSetting("Channel 2",null,"ch 2");
			mySetting = new HierarchicalSetting("Rename channels").addSetting(channel1).addSetting(channel2);
		}
		return mySetting;
	}
		
	public void compute() {
		getSetting();

		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			FeatureExpressionData fed = ((FeatureExpressionData)ex);
			AbstractFeatureExpressionData afed = new AbstractFeatureExpressionData(fed);
			afed.getChannelNames()[0] = channel1.getStringValue();
			afed.getChannelNames()[1] = channel2.getStringValue();
			transMatrix.setIntermediateData(e,afed);
		}	
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		ExperimentState es = new AbstractExperimentState(inputState) {
			public DataProperties getDataProperties() {
				DataProperties dp = inputState.getDataProperties().clone();
		 		dp.add(new ChannelCount(2, new String[]{channel1.getStringValue(), channel2.getStringValue()}) , true);		 		
		 		return dp;
			}
		};
		return es;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".RenameChannels", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Assign new names to the channels of a two-channel experiment", 
		"Rename channels");
	}
	
	public String getIdentifier() {
		return "Ch.Rename";
	}

}
