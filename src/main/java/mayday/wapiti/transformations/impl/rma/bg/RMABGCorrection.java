package mayday.wapiti.transformations.impl.rma.bg;

import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.experiments.properties.datamode.Logged;
import mayday.wapiti.experiments.properties.processing.BackgroundCorrected;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class RMABGCorrection extends AbstractTransformationPlugin {
	
	public RMABGCorrection() {}

	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps) {
			boolean isLogged = Logged.isLogged(e.getDataProperties());
			boolean isFEE = FeatureExpressionData.class.isAssignableFrom(e.getDataClass());
			boolean oneChannel = !ChannelCount.isMultiChannel(e.getDataProperties());
			if (isLogged || !isFEE || !oneChannel)
				return false;
		}
		return true;
	}
	
	
	public String getApplicabilityRequirements() {
		return "Requires single-channel unlogged feature expression data. ";
	}

	public void compute() {

		for (Experiment e : transMatrix.getExperiments(this)) {
			
			ExperimentData ex = transMatrix.getIntermediateData(e);
			
			AbstractVector v = ((FeatureExpressionData)ex).getExpressionVector(0);
			AbstractVector normalized = doBackgroundCorrection(v); 
			
			transMatrix.setIntermediateData(e, new AbstractFeatureExpressionData(normalized));
		}
	}
	
	protected AbstractVector doBackgroundCorrection(AbstractVector uncorrected) {
		double[] data = uncorrected.toArray();
		double[] param = new double[3];
		RMABackground.bgParameters(data, param, data.length, 1, 0);
		RMABackground.bgAdjust(data, param, data.length, 1, 0);
		DoubleVector ret = new DoubleVector(data);
		ret.setNamesDirectly(uncorrected);
		return ret;
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		return new AbstractExperimentState(inputState) {
			public DataProperties getDataProperties() {
				DataProperties in = inputState.getDataProperties();
				DataProperties out = in.clone();
				out.add(new BackgroundCorrected(), true);
				return out;
			}
		};
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".RMABGCorrection", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Applies the RMA background correction", 
		"RMA Background correction");
	}
	
	public String getIdentifier() {
		return "RMA:BG";
	}

}
