package mayday.wapiti.transformations.impl.multichannel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.channels.BackgroundType;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.experiments.properties.channels.FlagsPresent;
import mayday.wapiti.experiments.properties.datamode.Logged;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class DyeSwap extends AbstractTransformationPlugin {

//	protected HashMap<Experiment, String> cNames = new HashMap<Experiment, String>();
	
	// must map to initialdata to accomodate schizo experiments
	protected HashMap<ExperimentData, StringSetting> cShortNames = new HashMap<ExperimentData, StringSetting>();
	
	protected StringSetting cShort1, cShort2;
	protected HierarchicalSetting mySetting;
	
	public DyeSwap() {}
	
	public boolean applicableTo(Collection<Experiment> exps) {
		
		if (exps.size()!=2)
			return false;
		
		long fC = -1; 
		
		for (Experiment e:exps) {
			boolean isFED = FeatureExpressionData.class.isAssignableFrom(e.getDataClass());
			boolean isMulti = ChannelCount.isMultiChannel(e.getDataProperties());
			boolean isUnlogged = !Logged.isLogged(e.getDataProperties());
			if (!isFED || !isMulti || !isUnlogged)
				return false;
			long nextFC = e.getNumberOfFeatures();
			if (fC!=-1 && nextFC!=fC) 
				return false;
			fC = nextFC;
		}

		return true;
	}
	
	public void updateSettings(Collection<Experiment> experiments) {
		LinkedList<Experiment> ex = new LinkedList<Experiment>(experiments);
		Collections.sort(ex);
		
		Experiment e1 = ex.get(0);
		Experiment e2 = ex.get(1);
		
		ChannelCount c1 = (ChannelCount)e1.getDataProperties().getType(ChannelCount.class);
		ChannelCount c2 = (ChannelCount)e2.getDataProperties().getType(ChannelCount.class);

		HashMap<Experiment, String> cNames = new HashMap<Experiment, String>();
		cNames.put(e1,"summary of "+e1.getName()+"("+c1.getNames()[0]+ ") and "+e2.getName()+"("+c2.getNames()[1]+")");			
		cNames.put(e2,"summary of "+e1.getName()+"("+c1.getNames()[1]+ ") and "+e2.getName()+"("+c2.getNames()[0]+")");

		cShort1.setDescription("Specify a name for the "+cNames.get(e1));
		cShort2.setDescription("Specify a name for the "+cNames.get(e2));
		
		cShortNames.put(e1.getInitialData(), cShort1);
		cShortNames.put(e2.getInitialData(), cShort2);
	}

	
	public String getApplicabilityRequirements() {
		return "Requires multi-channel, unlogged feature expression data. All input experiments need to have the same number of features.";
	}


	public Setting getSetting() {
		if (mySetting==null) {
			cShort1 = new StringSetting("Name for Channel 1","","channel 1");
			cShort2 = new StringSetting("Name for Channel 2","","channel 2");
			mySetting = new HierarchicalSetting("Dye-Swap").addSetting(cShort1).addSetting(cShort2);
		}
		return mySetting;
	}
	
	public void compute() {
		LinkedList<Experiment> ex = new LinkedList<Experiment>(transMatrix.getExperiments(this));
		Collections.sort(ex);
		
		Experiment e1 = ex.get(0);
		Experiment e2 = ex.get(1);
				
		FeatureExpressionData fed1 = (FeatureExpressionData)transMatrix.getIntermediateData(e1);
		FeatureExpressionData fed2 = (FeatureExpressionData)transMatrix.getIntermediateData(e2);
		
		transMatrix.setIntermediateData(e1, new DyeSwapFeatureExpressionExperimentData(fed1,fed2,0,1,cShortNames.get(e1.getInitialData()).getStringValue()));
		transMatrix.setIntermediateData(e2, new DyeSwapFeatureExpressionExperimentData(fed1,fed2,1,0,cShortNames.get(e2.getInitialData()).getStringValue()));
	}

	@Override
	public ExperimentState getExperimentState(Experiment e) {
		ExperimentState es = states.get(e);
		if (es==null) {
			// use the transmatrix from the experiment so that this works with checked_clone in TransMatrix
			ExperimentState inputState = e.getTransMatrix().getInputState(this,e);
			final String cName = cShortNames.get(e.getInitialData()).getStringValue();
			es = new AbstractExperimentState(inputState) {
				public DataProperties getDataProperties() {
					DataProperties dp = inputState.getDataProperties().clone();
			 		dp.add(new ChannelCount(1, new String[]{cName}) , true);
			 		dp.remove(dp.getType(BackgroundType.class));
			 		dp.remove(dp.getType(FlagsPresent.class));
			 		return dp;
				}
			};
			states.put(e,es);
		}
		return es;
	}
		
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".DyeSwap", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Performs dye-swap normalization on two two-channel arrays.", 
		"Dye-Swap normalization");
	}
	
	public String getIdentifier(Experiment e) {
		String name = cShortNames.get(e.getInitialData()).getStringValue();
		return "DSwap: \""+name+"\"";
	}

	@Override
	protected ExperimentState makeState(ExperimentState inputState) {
		// never called
		return null;
	}

}
