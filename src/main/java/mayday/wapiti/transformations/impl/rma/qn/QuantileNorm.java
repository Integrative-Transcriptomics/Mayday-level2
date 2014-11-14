package mayday.wapiti.transformations.impl.rma.qn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.core.structures.linalg.matrix.VectorBasedMatrix;
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
import mayday.wapiti.experiments.properties.processing.Normalized;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class QuantileNorm extends AbstractTransformationPlugin {
	
	public QuantileNorm() {}

	public boolean applicableTo(Collection<Experiment> exps) {
		// dimensions must be the same, we will ignore names
		long noe=-1;

		for (Experiment e:exps) {
			boolean isFEE = FeatureExpressionData.class.isAssignableFrom(e.getDataClass());
			boolean multiChannel = ChannelCount.isMultiChannel(e.getDataProperties());

			if (isFEE && !multiChannel) {
				if (noe==-1)
					noe = e.getNumberOfFeatures();
				else {
					if (noe!=e.getNumberOfFeatures())
						return false;
				}
			} else {
				return false;
			}
			
	
		}

		return true;
	}

	
	public String getApplicabilityRequirements() {
		return "Requires single-channel feature expression data. All input experiments must have the same number of features.";
	}

	
	public void compute() {

		List<Experiment> exps = transMatrix.getExperiments(this);
		
		ArrayList<AbstractVector> columns = new ArrayList<AbstractVector>(exps.size());
		
		for (Experiment e : exps) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			AbstractVector v = ((FeatureExpressionData)ex).getExpressionVector(0);
			columns.add(v);
		}
		VectorBasedMatrix pm = new VectorBasedMatrix(columns, false);
		
		AbstractMatrix result = QNorm.qnormC(pm, true); //mask NA values
		
		for (int i=0; i!=exps.size(); ++i) { 
			DoubleVector av = (DoubleVector)result.getColumn(i);
			av.setNamesDirectly(columns.get(i));
			ExperimentData ed =  new AbstractFeatureExpressionData(av);
			transMatrix.setIntermediateData(exps.get(i),ed);
		}
	}
	
	protected ExperimentState makeState(ExperimentState inputState) {
		return new AbstractExperimentState(inputState) {
			public DataProperties getDataProperties() {
				DataProperties in = inputState.getDataProperties();
				DataProperties out = in.clone();
				out.add(new Normalized(), true);
				return out;
			}
		};
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".Qnorm", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Applies Quantile Normalization", 
		"Quantile Normalization");
	}
	
	public String getIdentifier() {
		return "QNorm";
	}

}
