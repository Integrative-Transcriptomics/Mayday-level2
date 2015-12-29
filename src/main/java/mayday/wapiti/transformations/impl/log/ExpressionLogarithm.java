package mayday.wapiti.transformations.impl.log;

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

public class ExpressionLogarithm extends AbstractTransformationPlugin {
	
	protected DoubleSetting logBase = new DoubleSetting("Logarithm base",null,2.0,0.0,null,false,false);
	
	public ExpressionLogarithm() {}
	
	public ExpressionLogarithm(double logBase) {
		this.logBase.setDoubleValue(logBase);
	}

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
					? new LoggedLocusExpressionExperimentData((LocusExpressionData)ex, logBase.getDoubleValue())
					: new LoggedFeatureExpressionExperimentData((FeatureExpressionData)ex, logBase.getDoubleValue()
					)
			);
		}	
	}

	public Setting getSetting() {
		return logBase;
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		ExperimentState es = new LoggedExpressionExperimentState(inputState, logBase.getDoubleValue(), LoggedFeatureExpressionExperimentData.class);
		return es;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".LogLocusExpression", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Compute the logarithm of raw expression values", 
		"Logarithm");
	}
	
	public String getIdentifier() {
		return "Log "+logBase.getDoubleValue();
	}

}
