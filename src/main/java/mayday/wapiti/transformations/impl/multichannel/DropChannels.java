package mayday.wapiti.transformations.impl.multichannel;

import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.ExtendableObjectSelectionSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class DropChannels extends AbstractTransformationPlugin {

	protected ExtendableObjectSelectionSetting<String> keptChannel;

	protected final String Desc = "Select which channel you wish to keep.";
	
	public DropChannels() {}
	
	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps) {
			boolean isFED = FeatureExpressionData.class.isAssignableFrom(e.getDataClass());
			boolean isMulti = ChannelCount.isMultiChannel(e.getDataProperties());
			if (!isFED || !isMulti)
				return false;
		}		
		return true;
	}
	
	public void updateSettings(Collection<Experiment> experiments) {
		Experiment e = experiments.iterator().next();
		ChannelCount c = (ChannelCount)e.getDataProperties().getType(ChannelCount.class);
		if (c!=null) {
			String[] channels = c.getNames();
			keptChannel.updatePredefined(channels);
		}
	}

	
	public String getApplicabilityRequirements() {
		return "Requires multi-channel feature expression data.";
	}

	public Setting getSetting() {
		if (keptChannel==null) {
			keptChannel = new ExtendableObjectSelectionSetting<String>("Keep channel",Desc,0,new String[]{""});
			keptChannel.setLayoutStyle(ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS);
		}
		return keptChannel;
	}
		
	public void compute() {
		int c;
		getSetting();
		c = keptChannel.getSelectedIndex();
		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			String n = ((FeatureExpressionData)ex).getChannelName(c);
			transMatrix.setIntermediateData(e, 
					new AbstractFeatureExpressionData(null,null,new AbstractVector[]{((FeatureExpressionData)ex).getExpressionVector(c)},null,new String[]{n})
			);
		}	
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		ExperimentState es = new AbstractExperimentState(inputState) {
			public DataProperties getDataProperties() {
				DataProperties dp = inputState.getDataProperties().clone();
		 		dp.add(new ChannelCount(1, new String[]{"selected channel"}) , true);		 		
		 		return dp;
			}
		};
		return es;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".DropChannels", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Select experiment channel.", 
		"Select one channel (discard the rest)");
	}
	
	public String getIdentifier() {
		if (keptChannel!=null && keptChannel.getSelectedIndex()>-1)
			return "Channel: \""+keptChannel.getPredefinedValues()[keptChannel.getSelectedIndex()]+"\"";
		return "Channel";
	}

}
