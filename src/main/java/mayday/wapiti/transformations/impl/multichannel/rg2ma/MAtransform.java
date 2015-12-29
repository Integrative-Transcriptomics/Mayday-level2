package mayday.wapiti.transformations.impl.multichannel.rg2ma;

import java.util.Collection;

import javax.swing.JLabel;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.ExtendableObjectSelectionSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.experiments.properties.datamode.Logged;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class MAtransform extends AbstractTransformationPlugin {

	protected ExtendableObjectSelectionSetting<String> channel;
	protected HierarchicalSetting mySetting;
	
	protected static final String Desc = "Select which channel is used for the A in\nX=A-B resp. X=A/B.";
	
	public MAtransform() {}
	
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
			channel.updatePredefined(channels);
			if (channel.getSelectedIndex()==-1)
				channel.setSelectedIndex(0);
		}		
	}

	
	public String getApplicabilityRequirements() {
		return "Requires multi-channel feature expression data. Will only use the first two channels of each experiment.";
	}


	public Setting getSetting() {
		if (mySetting==null) {
			mySetting = new HierarchicalSetting("MA transformation");
			mySetting.addSetting(new ComponentPlaceHolderSetting("info",new JLabel("Performs MA transformation, but does not apply the logarithm.")));
			channel = new ExtendableObjectSelectionSetting<String>("First channel",Desc,0,new String[]{""});
			channel.setLayoutStyle(ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS);
			mySetting.addSetting(channel);
		}
		return mySetting;
	}
	
	public void setTransMatrix(TransMatrix t) {
		super.setTransMatrix(t);
	}
	
	public void compute() {
		int c1,c2;
		getSetting();
		c1 = channel.getSelectedIndex();
		c2 = 1-c1;
		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);			
			boolean logged = Logged.isLogged(transMatrix.getInputState(this, e).getDataProperties()); 
			transMatrix.setIntermediateData(e, 
					new MAExperimentData((FeatureExpressionData)ex, logged, c1, c2)
			);
		}	
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		ExperimentState es = new MAExperimentState(inputState);
		return es;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".MATransform", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Compute fold-change from two-channel data", 
		"MA Transformation (fold-change)");
	}
	
	public String getIdentifier() {
		return "MA";
	}

}
