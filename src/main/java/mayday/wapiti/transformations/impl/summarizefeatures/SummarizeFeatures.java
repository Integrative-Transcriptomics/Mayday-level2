package mayday.wapiti.transformations.impl.summarizefeatures;

import java.util.Collection;
import java.util.List;

import mayday.core.math.average.IAverage;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.AveragingSetting;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.wapiti.containers.featuresummarization.FeatureSummarizationSetting;
import mayday.wapiti.containers.featuresummarization.IFeatureSummarizationMap;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class SummarizeFeatures extends AbstractTransformationPlugin {

	public FeatureSummarizationSetting summarization = new FeatureSummarizationSetting();
	public AveragingSetting avg = new AveragingSetting();
	
	public HierarchicalSetting mySetting = new HierarchicalSetting("Summarize features").addSetting(avg).addSetting(summarization);

	public SummarizeFeatures() {}
	
	public SummarizeFeatures(IFeatureSummarizationMap theMap) {
		summarization.setFeatureSummarizationMap(theMap);
	}

	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps) {
			if (!FeatureExpressionData.class.isAssignableFrom(e.getDataClass()))
				return false;
			if (ChannelCount.isMultiChannel(e.getDataProperties()))
				return false;
		} 
		return true;
	}
	
	
	public String getApplicabilityRequirements() {
		return "Requires single-channel feature expression data";
	}


	public void compute() {
		
		IFeatureSummarizationMap fm = summarization.getFeatureSummarizationMap();
		IAverage averager = avg.getSummaryFunction(); 
		
		for (Experiment e : transMatrix.getExperiments(this)) {
			
			FeatureExpressionData ex = (FeatureExpressionData)transMatrix.getIntermediateData(e);
			
			
			DoubleVector newColumn = new DoubleVector(fm.featureNames().size());
			int i=0;
			for (String n : fm.featureNames()) {
				newColumn.setName(i, n);
				List<String> subfeatures = fm.getSubFeatures(n);
				double[] f = new double[subfeatures.size()];				
				for (int j=0; j!=subfeatures.size(); ++j) {
					Double d = ex.getExpression(0,subfeatures.get(j));
					if (d==null)
						d = Double.NaN;
					f[j] = d; 
				}
				double nv = averager.getAverage(f, true);  // TODO always ignore NaN?
				newColumn.set(i, nv);
				++i;
			}
			
			transMatrix.setIntermediateData(e, new AbstractFeatureExpressionData(newColumn));
		}		
	}

	public Setting getSetting() {
		return mySetting;
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		return new FeatureSummarizedExperimentState(inputState, summarization.getFeatureSummarizationMap());
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".FeatureSummarization", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Create summary features (e.g. ProbeSets) from sets of features (e.g. Probes)", 
				"Summarize Features");
	}
	
	public String getIdentifier() {
		return "Summary";
	}
	
	

}
