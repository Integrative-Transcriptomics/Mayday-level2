package mayday.wapiti.transformations.impl.createDS;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.genetics.Locus;
import mayday.genetics.LocusMIO;
import mayday.genetics.advanced.LocusData;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.locusmap.LocusMap;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.transformations.base.AbstractDatasetCreatingTransformation;

public class DataSetFromFeatureExpression extends AbstractDatasetCreatingTransformation {

	protected StringSetting mySetting = new StringSetting("DataSet Name",null,"New DataSet",false);
	
	public DataSetFromFeatureExpression() {}

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
		return "Requires single-channel feature expression data.";
	}
	
	protected String getDSName() {
		return mySetting.getStringValue();
	}


	@SuppressWarnings("null")
	public void compute() {
		Set<String> commonFeatures = null;
		List<Experiment> exps = transMatrix.getExperiments(this);
		Set<Iterable<String>> knownIterables = new HashSet<Iterable<String>>();

		LocusMap locusMap = null;
		
		DataSet ds = createDataset(getDSName());
		MasterTable mt = ds.getMasterTable();
		
		// build set of common features
		for (Experiment e : exps) {
			Iterable<String> features = transMatrix.getInputState(this, e).featureNames();
			if (commonFeatures == null) {
				commonFeatures = iterableToSet(features);
			} else {
				if (!knownIterables.contains(features)) {
					Set<String> newSet = iterableToSet(e.featureNames());					
					commonFeatures.retainAll(newSet);					
				}
			}
			knownIterables.add(features);
			if (locusMap==null && e.hasLocusInformation()) {
				LocusData ld = e.getLocusData();
				if (ld instanceof LocusMap)
					locusMap = (LocusMap)ld;
			}
		}
		
		// add probes
		String[] featureNames = commonFeatures.toArray(new String[0]);
		
		DoubleMatrix expressionMatrix = new DoubleMatrix(featureNames.length, exps.size(), true);
		
		double total = exps.size() * featureNames.length + featureNames.length;
		double current = 0;
		
		for (int e=0; e!=exps.size(); ++e) {				
			FeatureExpressionData ex = (FeatureExpressionData)transMatrix.getIntermediateData(exps.get(e));
			for (int p = 0; p!=featureNames.length; ++p) {
				Double expression = ex.getExpression(0,featureNames[p]);
				if (expression==null)
					expression = Double.NaN;
				expressionMatrix.setValue(p, e, expression);
				if (p % 100 == 0)
					setProgress((int)(10000* current++*100 / total));
			}
		}
		
		MIGroup mg=null;
		if (locusMap!=null)
			mg = ds.getMIManager().newGroup("PAS.MIO.Locus", "Locus");
		
		for (int i=0; i!=featureNames.length; ++i) {
			Probe pb = new Probe(mt); 
			pb.setName(featureNames[i]);
			pb.setValues(expressionMatrix.getRow(i).toArrayUnpermuted());
			mt.addProbe(pb);
			if (locusMap!=null && mg!=null) {
				AbstractGeneticCoordinate agc = locusMap.get(featureNames[i]);
				if (agc!=null) {
					mg.add(pb, new LocusMIO(new Locus(agc)));
				}
			}
			if (i % 100 == 0)
				setProgress((int)(10000* current++*100 / total));
		}
		
		transferAnnotation(ds);
		finishDataset(ds);
		
	}


	
	public Setting getSetting() {
		return mySetting;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".DSfromCommonFeatures", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Create a DataSet from the common features of selected experiments", 
		"Create DataSet from common features");
	}


}
