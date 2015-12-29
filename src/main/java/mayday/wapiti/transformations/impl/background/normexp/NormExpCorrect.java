package mayday.wapiti.transformations.impl.background.normexp;

import java.util.Collection;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.channels.BackgroundType;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.experiments.properties.processing.BackgroundCorrected;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class NormExpCorrect extends AbstractTransformationPlugin {

	protected IntSetting channel1;
	protected IntSetting channel2;
	protected RestrictedStringSetting method;
	protected HierarchicalSetting mySetting;
	
	public NormExpCorrect() {}
	
	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps) {
			boolean isFED = FeatureExpressionData.class.isAssignableFrom(e.getDataClass());
			boolean hasChannelInfo = e.getDataProperties().getType(ChannelCount.class)!=null;
			if (!isFED || !hasChannelInfo)
				return false;
		}
		return true;
	}
	
	public String getApplicabilityRequirements() {
		return "Requires feature expression data.";
	}


	public Setting getSetting() {
		if (mySetting==null) {					
			mySetting = new HierarchicalSetting("NormExp normalization");
			method = new RestrictedStringSetting("Method",null,0,new String[]{"Saddle","RMA"});
			mySetting.addSetting(method);
		}
		return mySetting;
	}
	
	public void setTransMatrix(TransMatrix t) {
		super.setTransMatrix(t);
	}
	
	public void compute() {
		getSetting();
		int indexMethod = method.getSelectedIndex()==0 ? NormExp.SADDLE : NormExp.RMA;
		
		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			
			AbstractVector[] res = new AbstractVector[((FeatureExpressionData)ex).getNumberOfChannels()];
			String[] names = new String[res.length];
			
			for (int i=0; i!=res.length; ++i) {
				AbstractVector fg = ((FeatureExpressionData)ex).getExpressionVector(i); // will be changed
				AbstractVector bg = ((FeatureExpressionData)ex).getBackgroundVector(i); // will be removed
				if (bg!=null) {
					res[i] = NormExp.normExp(fg, bg, indexMethod, null);
				} else {
					res[i] = NormExp.normExp(fg, indexMethod, null);
				}
				names[i] = ((FeatureExpressionData)ex).getChannelName(i)+" (normExp corrected)";
				res[i].setNames(fg);
			}
			
			HashMap<String,Double> flagTypes = ((FeatureExpressionData)ex).getFlagTypes();
			AbstractVector flags = ((FeatureExpressionData)ex).getFlagVector();
			transMatrix.setIntermediateData(e, new AbstractFeatureExpressionData(
					flags,
					flagTypes,
					res,
					null,
					names
			));
		}	
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		ExperimentState es = new AbstractExperimentState(inputState) {
			public DataProperties getDataProperties() {
				DataProperties dp = inputState.getDataProperties().clone();
				BackgroundType bt = dp.getType(BackgroundType.class);
				if (bt!=null)
					dp.remove(bt);
				ChannelCount c = dp.getType(ChannelCount.class);
				String[] names = c.getNames().clone();
				for (int i=0; i!=names.length;++i)
					names[i] = names[i]+" (normExp corrected)";
				c = new ChannelCount(c.getCount(), names);
				dp.add(c, true);
		 		dp.add(new BackgroundCorrected(), true);
		 		return dp;
			}
		};
		return es;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".NormExp", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Applies normexp correction", 
		"NormExp Background Correction");
	}
	
	public String getIdentifier() {
		return "NormExp";
	}

}
