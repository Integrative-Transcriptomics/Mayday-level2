package mayday.wapiti.experiments.importer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.pluma.AbstractPlugin;
import mayday.wapiti.Constants;
import mayday.wapiti.containers.featuresummarization.FeatureSummarizationMap;
import mayday.wapiti.containers.featuresummarization.FeatureSummarizationMapContainer;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.gui.actions.experiments.ExperimentsPropertiesAction;
import mayday.wapiti.transformations.matrix.TransMatrix;

public abstract class ExperimentImportPlugin extends AbstractPlugin {

	public final static String MC = Constants.MCBASE+"ExperimentImport";
	
	public void init() {}

	public abstract void importInto(TransMatrix transMatrix);
	
	protected void addExperiments(List<Experiment> experiments, TransMatrix transMatrix) {
		// first show a common settings dialog for all experiments		
		ExperimentsPropertiesAction.showDialog( experiments, false );
		
		for (Experiment e : experiments) {
			transMatrix.addExperiment(e);
		}
	} 
	
	
	protected static String findUniqueName(String title, Set<String> competitors) {
		int add=0;
		String suggested = title;
		
		boolean satisfied = true;
		do {
			if (!satisfied)
				suggested = title+" "+(++add);
			satisfied = !competitors.contains(suggested);
		} while (!satisfied);
		return suggested;
	}

	
	protected static void makeNamesUnique(String[] featureNames, String name) {		
		// create a summarization map for later use
		FeatureSummarizationMap fsm = new FeatureSummarizationMap("Probe Sets derived from "+name);
		
		HashSet<String> namesInUse = new HashSet<String>();
		for (int i=0; i!=featureNames.length; ++i) {
			String n = featureNames[i];
			if (n==null) // happens in erroneous ScanArray files
				featureNames[i] = n = "(null)";
			if (namesInUse.contains(n)) {
				n = findUniqueName(n, namesInUse);
				fsm.put(featureNames[i], n);
				featureNames[i] = n;
			} else {
				fsm.put(n,n);
			}
			namesInUse.add(n);
		}
		
		FeatureSummarizationMapContainer.INSTANCE.add(fsm);
	}
	
}
