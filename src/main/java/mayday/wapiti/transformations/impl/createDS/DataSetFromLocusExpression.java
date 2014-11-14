package mayday.wapiti.transformations.impl.createDS;

import java.util.Collection;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.genetics.Locus;
import mayday.genetics.LocusMIO;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.locusmap.LocusMap;
import mayday.genetics.locusmap.LocusMapSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.locusexpression.LocusExpressionData;
import mayday.wapiti.transformations.base.AbstractDatasetCreatingTransformation;

public class DataSetFromLocusExpression extends AbstractDatasetCreatingTransformation {

	protected LocusMapSetting locusSet = new LocusMapSetting();
	protected StringSetting dsName = new StringSetting("DataSet Name",null,"New DataSet",false);
	protected HierarchicalSetting mySetting = new HierarchicalSetting("Create DataSet from Locus Expression Data")
		.addSetting(dsName).addSetting(locusSet);
	
	
	public DataSetFromLocusExpression() {}

	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps) 
			if (!LocusExpressionData.class.isAssignableFrom(e.getDataClass()))
				return false;		
		return true;
	}
	
	public String getApplicabilityRequirements() {
		return "Requires locus expression data.";
	}

	protected String getDSName() {
		return dsName.getStringValue();
	}

	public void compute() {
		LocusMap loci = locusSet.getLocusMap();

		List<Experiment> exps = transMatrix.getExperiments(this);

		DataSet ds = createDataset(getDSName());
		MasterTable mt = ds.getMasterTable();
		
		// add probes
		DoubleMatrix expressionMatrix = new DoubleMatrix(loci.size(), exps.size(), true);
		expressionMatrix.setColumnNames(mt.getExperimentNames());
		String[] featureNames = iterableToSet(loci.keySet()).toArray(new String[0]);
		expressionMatrix.setRowNames(featureNames);

		double total = exps.size() * featureNames.length + featureNames.length;
		double current = 0;
		
		for (int e=0; e!=exps.size(); ++e) {				
			LocusExpressionData ex = (LocusExpressionData)transMatrix.getIntermediateData(exps.get(e));
			for (int p=0; p!=featureNames.length; ++p) {
				AbstractGeneticCoordinate coord = loci.get(featureNames[p]);
				Double d = ex.getExpression(coord);
				if (d==null)
					d = Double.NaN;
				expressionMatrix.setValue(p, e, d);
				if (p % 100 == 0)
					setProgress((int)(10000* current++*100 / total));
			}
			ex.compact(); // remove cached data if necessary
		}
		
		MIGroup mg = ds.getMIManager().newGroup("PAS.MIO.Locus", "Locus");
		
		for (int i=0; i!=featureNames.length; ++i) {
			Probe pb = new Probe(mt);
			pb.setName(featureNames[i]);
			pb.setValues(expressionMatrix.getRow(i).toArrayUnpermuted());
			mt.addProbe(pb);
			mg.add(pb, new LocusMIO(new Locus(loci.get(featureNames[i]))));
			if (i % 100 == 0)
				setProgress((int)(10000* current++*100 / total));
		}
		
		transferAnnotation(ds); 
		finishDataset(ds);
		
	}
	

	protected ExperimentState makeState(ExperimentState inputState) {		
		LocusMap loci = locusSet.getLocusMap();
		return new LocusToFeatureExpressionState(inputState, loci);
	}
	
	public Setting getSetting() {
		return mySetting;
	}



	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".DSfromCommonLoci", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Create a DataSet from a selected experiments and a set of loci", 
		"Create DataSet from common loci");
	}

	

}
