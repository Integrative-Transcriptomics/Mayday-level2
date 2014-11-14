package mayday.wapiti.transformations.impl.inter_norm;

import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
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

public class PercentileScaling extends AbstractTransformationPlugin {

	protected HierarchicalSetting mysetting;
	protected DoubleSetting scaleQuantile;
	protected DoubleSetting scaleTarget;
	protected BooleanSetting removeZero,removeNA;

	public PercentileScaling() {
		scaleQuantile = new DoubleSetting("Scale percentile","Define which percentile to use for scaling", .75, 0d, 1d, true, true);
		scaleTarget = new DoubleSetting("Scale target value","Define which value to assign to the scaled percentage", 12d);
		removeZero = new BooleanSetting("Ignore zero values when computing percentiles", 
				"Bullard et al (BCM Bioinformatics 2010) suggest to activate this option.", true);
		removeNA = new BooleanSetting("Ignore NA values when computing percentiles",null, true);
		mysetting = new HierarchicalSetting("Percentile Scaling").addSetting(scaleQuantile).addSetting(scaleTarget).addSetting(removeZero).addSetting(removeNA);
		mysetting.setDescription(
				"Multiplies all values such that the specified percentile is at the specified target value."
				);
	}
	
	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps) {
			boolean isFED = FeatureExpressionData.class.isAssignableFrom(e.getDataClass());
			DataProperties p = e.getDataProperties();
			boolean isOneChannel = !ChannelCount.isMultiChannel(p);
			if (!isFED || !isOneChannel) 
				return false;
		}
		return true;
	}
	
	public String getApplicabilityRequirements() {
		return "Requires single-channel feature expression data";
	}

	public void compute() {
		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			AbstractVector input = ((FeatureExpressionData)ex).getExpressionVector(0);			
						
			double currentValue;
			
			AbstractVector scalingInput = input;
			
			if (removeZero.getBooleanValue()) {
				scalingInput = scalingInput.subset(scalingInput.isEqualTo(0),true); 				
			};
			
			if (removeNA.getBooleanValue()) {
				scalingInput = scalingInput.subset(scalingInput.isNA(),true);				
			}
			currentValue = scalingInput.quantile(scaleQuantile.getDoubleValue(), false);
			
			
			double correctionFactor = scaleTarget.getDoubleValue()/currentValue;
			
			if (Double.isNaN(correctionFactor) || Double.isInfinite(correctionFactor))
				throw new RuntimeException("Cannot apply percentage scaling to experiment "+e.getName()+": Correction factor would be "+correctionFactor);

			System.out.println("Input quantile value: "+currentValue);
			System.out.println("Correction factor:    "+correctionFactor);
			
			AbstractVector output = input.clone();
			output.multiply(correctionFactor);
			
			System.out.println("Output quantile value (all): "+output.quantile(scaleQuantile.getDoubleValue(), false));		
			AbstractVector oq = output;
			if (removeZero.getBooleanValue()) {
				oq = oq.subset(oq.isEqualTo(0),true); 				
			};			
			if (removeNA.getBooleanValue()) {
				oq = oq.subset(oq.isNA(),true);				
			}
			System.out.println("Output quantile value (filtered): "+oq.quantile(scaleQuantile.getDoubleValue(), false));		

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
				MC+".PercentileScaling", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Perform percentile scaling normalization, i.e. multiply all values such that a specified percentile is at a specified target value.", 
		"Percentile Scaling");
	}
	
	public String getIdentifier() {
		return "Perc.Scaling";
	}

}
