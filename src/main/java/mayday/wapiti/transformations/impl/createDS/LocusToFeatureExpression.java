package mayday.wapiti.transformations.impl.createDS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.maps.MultiHashMap;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.coordinatemodel.GBNode;
import mayday.genetics.coordinatemodel.GBNode_Unstranded;
import mayday.genetics.locusmap.LocusMap;
import mayday.genetics.locusmap.LocusMapSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class LocusToFeatureExpression extends AbstractTransformationPlugin {

	protected LocusMapSetting locusSet = new LocusMapSetting();
	protected BooleanSetting strandSpecific = new BooleanSetting("Strand-specific mapping",
			"With this option, locus expression objects (e.g., reads) are only mapped to features on the same strand.\n" +
			"Without this option, objects are mapped regardless of strand, e.g. for non-strand-specific RNAseq-data.",Boolean.TRUE);
	protected HierarchicalSetting mySetting = new HierarchicalSetting("Map expression to features")
		.addSetting(locusSet)
		.addSetting(strandSpecific);	
	
	public LocusToFeatureExpression() {}

	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps)
			if (FeatureExpressionData.class.isAssignableFrom(e.getDataClass())) {
				if (!e.hasLocusInformation() || ChannelCount.isMultiChannel(e.getDataProperties())) {
					return false;	
				}
			} else if (LocusReadCountData.class.isAssignableFrom(e.getDataClass())) 
				return false;
		
		return true;
	}
	
	public String getApplicabilityRequirements() {
		return "Requires either locus expression data or single-channel feature expression data with locus information.";
	}


	@SuppressWarnings("unchecked")
	public void compute() {
		setProgress(-1, "Optimizing query regions");

		LocusMap loci = locusSet.getLocusMap();
		List<Experiment> exps = transMatrix.getExperiments(this);

		DoubleMatrix expressionMatrix = new DoubleMatrix(loci.size(), exps.size(), false);
		String[] featureNames = iterableToSet(loci.keySet()).toArray(new String[0]);
		expressionMatrix.setRowNames(featureNames);
		
		// if _ANY_ experiment is of the LocusExpressionData type, we sort the loci for faster access
		boolean anyLED = false;
		for (int e=0; e!=exps.size(); ++e) {	
			ExperimentData ed = transMatrix.getIntermediateData(exps.get(e));
			anyLED |= (ed instanceof LocusExpressionData);
		}
		
		MultiHashMap<AbstractGeneticCoordinate, Integer> reverseMap = new MultiHashMap<AbstractGeneticCoordinate, Integer>();
		ArrayList<AbstractGeneticCoordinate> sortedLoci = new ArrayList<AbstractGeneticCoordinate>(loci.size());
		if (anyLED) {
			int p=0;
			for (String k : featureNames) {
				AbstractGeneticCoordinate agc = loci.get(k);
				if (!strandSpecific.getBooleanValue()) {
					GBNode agcnode = agc.getModel();
					agc = new GeneticCoordinate(agc.getChromosome(), new GBNode_Unstranded(agcnode));					
				}
				sortedLoci.add(agc);
				reverseMap.put(agc,p++);
			}
			setProgress(-1, "Optimizing query regions");
			Collections.sort(sortedLoci);			
		}

		
		double total = exps.size() * featureNames.length;
		double current = 0;
		
		for (int e=0; e!=exps.size(); ++e) {		
			ExperimentData ed = transMatrix.getIntermediateData(exps.get(e));
			if (ed instanceof LocusExpressionData) {
				LocusExpressionData ex = (LocusExpressionData)ed;	
				int cc=0;
				for (AbstractGeneticCoordinate coord: sortedLoci) {
					Double d = ex.getExpression(coord);
					if (d==null)
						d = Double.NaN;
					for (int p : reverseMap.get(coord)) {
						expressionMatrix.setValue(p, e, d);
						if (cc % 100 == 0)
							setProgress((int)(10000* current++*100 / total));
						++cc;
					}
				}
				
			} else {
				FeatureExpressionData ex = (FeatureExpressionData)ed;
				for (int p=0; p!=featureNames.length; ++p) {
					Double d = ex.getExpression(0,featureNames[p]);
					if (d==null)
						d = Double.NaN;
					expressionMatrix.setValue(p, e, d);
					if (p % 100 == 0)
						setProgress((int)(10000* current++*100 / total));
				}
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
		return mySetting;
	}

	protected ExperimentState makeState(ExperimentState inputState) {		
		LocusMap loci = locusSet.getLocusMap();
		return new LocusToFeatureExpressionState(inputState, loci);
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".MapToFeatures", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Converts locus-based expression to feature-based expression data", 
		"Map expression levels to a set of features");
	}
	
	public String getIdentifier() {
		return "Map to Features";
	}
	

}
