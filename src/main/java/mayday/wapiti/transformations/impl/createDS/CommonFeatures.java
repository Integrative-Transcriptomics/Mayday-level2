package mayday.wapiti.transformations.impl.createDS;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class CommonFeatures extends AbstractTransformationPlugin {

	protected Set<String> commonFeatures = null;
	
	public CommonFeatures() {}
	
	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps) {
			if (FeatureExpressionData.class.isAssignableFrom(e.getDataClass())) {
				if (ChannelCount.isMultiChannel(e.getDataProperties()))
					return false;				
			} else {
				return false;
			}
		}		

		return true;
	}
	
	public void updateSettings(Collection<Experiment> experiments) {
		// gather feature set
		Set<Iterable<String>> knownIterables = new HashSet<Iterable<String>>();

		for (Experiment e : experiments) {
			Iterable<String> features = e.featureNames();
			if (commonFeatures == null) {
				commonFeatures = iterableToSet(features);
			} else {
				if (!knownIterables.contains(features)) {
					Set<String> newSet = iterableToSet(e.featureNames());					
					commonFeatures.retainAll(newSet);					
				}
			}
			knownIterables.add(features);
		}
		
	}

	
	public String getApplicabilityRequirements() {
		return "Requires single-channel locus expression data.";
	}


	public void compute() {

		List<Experiment> exps = transMatrix.getExperiments(this);
		
		// add probes
		String[] featureNames = commonFeatures.toArray(new String[0]);
		DoubleMatrix expressionMatrix = new DoubleMatrix(featureNames.length, exps.size(), false);
		expressionMatrix.setRowNames(featureNames);
		
		for (int e=0; e!=exps.size(); ++e) {		
			ExperimentData ed = transMatrix.getIntermediateData(exps.get(e));
			FeatureExpressionData ex = (FeatureExpressionData)ed;
			for (int p=0; p!=featureNames.length; ++p) {
				Double d = ex.getExpression(0,featureNames[p]);
				if (d==null)
					d = Double.NaN;
				expressionMatrix.setValue(p, e, d);
			}
			transMatrix.setIntermediateData(exps.get(e), new AbstractFeatureExpressionData(expressionMatrix.getColumn(e)));
		}
	}
	
	protected Set<String> iterableToSet(Iterable<String> it) {
		Set<String> newSet = new TreeSet<String>();
		for (String fn : it)
			newSet.add(fn);
		return newSet;
	}
	
	public Setting getSetting() {
		return null;
	}

	@Override
	protected ExperimentState makeState(ExperimentState inputState) {
		final Collection<String> cf = commonFeatures;
		return new AbstractExperimentState(inputState) {
			@Override
			public Iterable<String> featureNames() {
				return cf;
			}
			@Override
			public long getNumberOfFeatures() {
				return cf.size();
			}
		};
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".CommonFeatures", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Creates a set of common features for a number of experiments", 
		"Use common features");
	}
	
	public String getIdentifier() {
		return "Common Features";
	}
	

}
