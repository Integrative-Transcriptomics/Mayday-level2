package mayday.wapiti.transformations.impl.rma.mp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import mayday.core.meta.MIGroup;
import mayday.core.meta.types.IntegerMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.matrix.VectorBasedMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.wapiti.containers.featuresummarization.FeatureSummarizationSetting;
import mayday.wapiti.containers.featuresummarization.IFeatureSummarizationMap;
import mayday.wapiti.experiments.base.AbstractExperiment;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;
import mayday.wapiti.transformations.impl.summarizefeatures.FeatureSummarizedExperimentState;

public class MedianPolish extends AbstractTransformationPlugin {
	
	public FeatureSummarizationSetting summarization = new FeatureSummarizationSetting();
	public BooleanSetting attachPSSize = new BooleanSetting("Attach ProbeSet size",
			"Attach the size (number of probes) of each the probeset as meta-information?", false);
	public BooleanSetting useTRMA = new BooleanSetting("Use the tRMA procedure",
			"tRMA can reduce inter-array correlation artifacts for small sample sizes\n" +
			"while preserving the benefits of RMA. Reference: Giorgi et al (BMC Bioinformatics 2010, 11:553)", true);
	public HierarchicalSetting mySetting = new HierarchicalSetting("Median Polish")
	.addSetting(summarization)
	.addSetting(useTRMA)
	.addSetting(attachPSSize);
	
	public MedianPolish() {}
	

	public MedianPolish(IFeatureSummarizationMap theMap) {
		summarization.setFeatureSummarizationMap(theMap);
	}

	public boolean applicableTo(Collection<Experiment> exps) {
		// dimensions must be the same, we will ignore names
		long noe=-1;
		
		for (Experiment e:exps) {
			boolean isFEE = FeatureExpressionData.class.isAssignableFrom(e.getDataClass());
			if (isFEE) {
				boolean isNumberOK = true;
				if (noe==-1)
					noe = e.getNumberOfFeatures();
				else
					isNumberOK = noe==e.getNumberOfFeatures();
				return isNumberOK;
			}
			return false;
		}
		return true;
	}

	public Setting getSetting() {		
		return mySetting;
	}
	
	
	public String getApplicabilityRequirements() {
		return "Requires feature expression data. " +
				"All input experiments must have the same number of features. " +
				"Only works on the first channel of multi-channel experiments.";
	}
	
	public void compute() {

		List<Experiment> exps = transMatrix.getExperiments(this);
		
		ArrayList<AbstractVector> columns = new ArrayList<AbstractVector>(exps.size());
		
		AbstractVector v0 = null;
		
		for (Experiment e : exps) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			AbstractVector v = ((FeatureExpressionData)ex).getExpressionVector(0);
			// make sure all vectors have a name cache. we can assume they have the same content, because otherwise rma would not make sense
			if (v0==null)
				v0 = v;
			else 
				v.setNameCacheDirectly(v0);
			columns.add(v);
		}
		VectorBasedMatrix pm = new VectorBasedMatrix(columns, false);

		AbstractMatrix result = summarize(pm, summarization.getFeatureSummarizationMap(), useTRMA.getBooleanValue());

		MIGroup mg = transMatrix.getCommonMIGroup("ProbeSet size", "PAS.MIO.Integer");
		
		if (attachPSSize.getBooleanValue()) {
			IFeatureSummarizationMap ifsm = summarization.getFeatureSummarizationMap();
			for (String probesetname :  ifsm.featureNames()) {
				((IntegerMIO)mg.add(probesetname)).setValue(ifsm.getSubFeatures(probesetname).size());
			}
		}
		
		for (int i=0; i!=exps.size(); ++i) { 
			AbstractVector av = result.getColumn(i);
			ExperimentData ed =  new AbstractFeatureExpressionData(av);
			transMatrix.setIntermediateData(exps.get(i),ed);
			((AbstractExperiment)exps.get(i)).addAnnotation(mg);
		}
		
	}
	
	public AbstractMatrix summarize(AbstractMatrix logPM, IFeatureSummarizationMap probeNames, boolean useTRMA) {
		
		int cols = logPM.ncol();
		int featureCount = probeNames.featureNames().size();
						
		//initialize result Matrix
		String[] featureNames = probeNames.featureNames().toArray(new String[0]);
		HashMap<String, Integer> speedUp = new HashMap<String, Integer>();
		DoubleVector[] PMcor = new DoubleVector[cols];
		for(int i = 0; i < cols; i++){
			 DoubleVector dv = new DoubleVector(featureCount);
			 dv.setNamesDirectly(featureNames, speedUp);
			 PMcor[i] = dv;
		}
		PermutableMatrix result = new VectorBasedMatrix(PMcor, false); 
		
		DoubleVector resultRow = new DoubleVector(cols);
		
		for (int i=0; i!=featureNames.length; ++i) {
			List<String> curRows = probeNames.getSubFeatures(featureNames[i]);
			MPolish.medianPolish(logPM, curRows, resultRow, useTRMA);
			result.setRow(i, resultRow);
			result.setRowName(i, featureNames[i]);
		}
		
		return result;
	}
	
	protected ExperimentState makeState(ExperimentState inputState) {
		return new FeatureSummarizedExperimentState(inputState, summarization.getFeatureSummarizationMap());
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".MPolish", 
				new String[0], 
				MC, null, 
				"Günter Jäger", 
				"jaeger@informatik.uni-tuebingen.de", 
				"Applies Median Polish Summarization", 
		"Median Polish");
	}
	
	public String getIdentifier() {
		return "RMA:Polish";
	}

}
