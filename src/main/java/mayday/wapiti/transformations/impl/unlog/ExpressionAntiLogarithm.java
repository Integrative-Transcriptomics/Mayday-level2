package mayday.wapiti.transformations.impl.unlog;

import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class ExpressionAntiLogarithm extends AbstractTransformationPlugin {
	
	protected DoubleSetting expBase = new DoubleSetting("Exponentiation base",null,2.0,0.0,null,false,false);
	
	public ExpressionAntiLogarithm() {}

	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps) {
			boolean isFED = FeatureExpressionData.class.isAssignableFrom(e.getDataClass());
			boolean isLED = LocusExpressionData.class.isAssignableFrom(e.getDataClass());
//			boolean isUnlogged = e.getDataProperties().containsType(Unlogged.class) || !e.getDataProperties().containsType(Logged.class); 
			if ((!isFED && !isLED) /* || !isUnlogged*/)
				return false;
		}
		return true;
	}
	
	
	public String getApplicabilityRequirements() {
		return "Requires feature expression data or locus expression data";
	}


	public void compute() {
		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			transMatrix.setIntermediateData(e,
					(ex instanceof LocusExpressionData) 
					? new UnloggedLocusExpressionExperimentData((LocusExpressionData)ex, expBase.getDoubleValue())
					: new UnloggedFeatureExpressionExperimentData((FeatureExpressionData)ex, expBase.getDoubleValue()
					)
			);
		}	
	}

	public Setting getSetting() {
		return expBase;
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		ExperimentState es = new UnloggedExpressionExperimentState(inputState, UnloggedFeatureExpressionExperimentData.class);
		return es;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".UnlogLocusExpression", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Reverse the logarithm computation of expression values", 
		"Anti-Logarithm");
	}
	
	public String getIdentifier() {
		return "Anti-Log "+expBase.getDoubleValue();
	}

}
