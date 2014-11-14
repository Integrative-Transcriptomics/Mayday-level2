package mayday.wapiti.transformations.impl.inter_norm;

import java.util.ArrayList;
import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
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

public class MedianFactorScaling extends AbstractTransformationPlugin {

	protected DoubleSetting scaleQuantile;
	protected DoubleSetting scaleTarget;
	protected BooleanSetting removeZero,removeNA;

	public MedianFactorScaling() {		
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
		
		System.out.println("MGMFScale: Binding input matrix");
		ArrayList<AbstractVector> inputVecs = new ArrayList<AbstractVector>();
		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			AbstractVector input = ((FeatureExpressionData)ex).getExpressionVector(0);
			inputVecs.add(input);
		}
		
		VectorBasedMatrix inputMatrix = new VectorBasedMatrix(inputVecs, false);
		inputMatrix.setRowNames(inputVecs.get(0));

		System.out.println("MGMFScale: Computing per-feature geometric means");		
		AbstractVector featureGeometricMean = new DoubleVector(inputMatrix.nrow());
		for (int j=0; j!=inputMatrix.nrow(); ++j) {
			AbstractVector row = inputMatrix.getRow(j).clone();
			row.log(2);
			double rs = row.sum();
			rs/=inputMatrix.ncol();
			rs = Math.exp(rs);
			featureGeometricMean.set(j,rs);
			// prod(row)^(1/n) == exp(sum(log(row))/n)
		}
//				inputMatrix.applyVec(0, "prod");
//		featureGeometricMean.exp(1.0/inputMatrix.ncol());
		
		System.out.println("MGMFScale: Computing per-sample correction factors, scaling");
		AbstractVector correctionFactors =  new DoubleVector(inputMatrix.ncol());
		for (int i=0; i!=inputMatrix.ncol(); ++i) {
			Experiment e = transMatrix.getExperiments(this).get(i);
			AbstractVector perSamplePerGeneScaling = inputMatrix.getColumn(i).clone();
			perSamplePerGeneScaling.divide(featureGeometricMean);
			double cf = perSamplePerGeneScaling.median(true);
			correctionFactors.set(i, cf);
			System.out.println("MGMFScale: "+e.getName()+": \t"+cf);
			DoubleVector output = inputMatrix.getColumn(i).clone();
			System.out.println("MGMFScale: "+e.getName()+": \t\twas: ["+output.min()+","+output.mean()+","+output.max()+"]");
			output.divide(cf);
			System.out.println("MGMFScale: "+e.getName()+": \t\tis:  ["+output.min()+","+output.mean()+","+output.max()+"]");
			transMatrix.setIntermediateData(e, new AbstractFeatureExpressionData(output));
		}
		
		System.out.println("MGMFScale: Done");
		
	}

	public Setting getSetting() {
		return null;
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
				MC+".MedianFactorScaling", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Experiments are scaled as follows:\n" +
				"For each feature, the geometric mean of expression values is computed across all samples.\n" +
				"For each (sample i,feature j), a scale factor f_ij = expression_ij / geometric_mean(expression_.j) is computed.\n" +
				"For each sample i, the scale factor f_i=median(f_i.) is computed.\n" +
				"The normalized expression values for are then e'_ij = e_ij/f_i\n" +
				"(Anders and Huber, Genome Biology (2010) 11:R106)", 
		"Median geometric mean factor scaling");
	}
	
	public String getIdentifier() {
		return "MGMF scale";
	}

}
