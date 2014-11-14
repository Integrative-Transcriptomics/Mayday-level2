package mayday.wapiti.transformations.base;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.types.AnnotationMIO;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentState;

public abstract class AbstractDatasetCreatingTransformation extends AbstractTransformationPlugin implements DatasetCreatingTransformation {

	public DataSet createDataset(String dsName) {
		List<Experiment> exps = transMatrix.getExperiments(this);

		// Create DataSet
		DataSet ds = new DataSet(dsName);
		MasterTable mt = ds.getMasterTable();
		mt.setNumberOfExperiments(exps.size());
		for (int i=0; i!=exps.size(); ++i) {
			Experiment ex = exps.get(i);
			mt.setExperimentName(i, ex.getName());			
			mt.getExperiment(i).setAnnotation(
				new AnnotationMIO(
						"Transformations applied: "+transMatrix.getTransformations(ex).toString(), 
						"Original name: "+ex.getName()+"\nSource: "+ex.getSourceDescription())
			);
		}		
		
		StringBuilder annotation = new StringBuilder();
		String date = DateFormat.getDateTimeInstance().format(new Date());
		annotation.append("DataSet created "+date+"\n\n");
		annotation.append("Transformation Matrix:\n");
		annotation.append(transMatrix.toString(exps));
		
		ds.getAnnotation().setInfo(annotation.toString());
		ds.getAnnotation().setQuickInfo("Dataset created using Mayday SeaSight\nCreated "+date);
		
		return ds;
	}
	
	public void finishDataset(DataSet ds) {
		ds.getProbeListManager().addObjectAtTop(ds.getMasterTable().createGlobalProbeList(true));
		DataSetManager.singleInstance.addObjectAtBottom(ds);
	}
	
	protected abstract String getDSName();
	
	
	public void transferAnnotation(DataSet ds) {
		HashMap<MIGroup,MIGroup> alreadyProcessed = new HashMap<MIGroup,MIGroup>();
		
		List<Experiment> exps = transMatrix.getExperiments(this);
		MasterTable mt = ds.getMasterTable();
		
		for (int i=0; i!=exps.size(); ++i) {
			mayday.core.Experiment mtExp = mt.getExperiment(i);
			Experiment seasightExp = exps.get(i);
			for (MIGroup mg : seasightExp.getAnnotations()) {
				MIGroup targetMG = alreadyProcessed.get(mg);
				if (targetMG==null)
					targetMG = ds.getMIManager().newGroup(mg.getMIOType(), mg.getName());
				
				MIType mio = mg.getMIO(seasightExp); //experimental annotation
				if (mio!=null) {
					targetMG.add(mtExp, mio);
				}	
				
				// probe annotations, only if not done before
				if (!alreadyProcessed.containsKey(mg)) {
					for (Probe pb : mt.getProbes().values()) {
						mio = mg.getMIO(pb.getName()); 
						if (mio!=null)
							targetMG.add(pb, mio);
					}
					alreadyProcessed.put(mg, targetMG);
				} 
			}
			
		}

		
		// remove empty groups
		List<MIGroup> empty = new LinkedList<MIGroup>();
		for (MIGroup mg : ds.getMIManager().getGroups())
			if (mg.size()==0)
				empty.add(mg);
		for (MIGroup mg : empty)
			ds.getMIManager().removeGroup(mg);
		
	}

	protected Set<String> iterableToSet(Iterable<String> it) {
		Set<String> newSet = new TreeSet<String>();
		for (String fn : it)
			newSet.add(fn);
		return newSet;
	}
	
	protected ExperimentState makeState(ExperimentState inputState) {
		return inputState; // not changing anything. The important thing is the side-effect ;-)
	}
	
	
	public String getIdentifier() {
		return "-> \""+getDSName()+"\"";
	}

	
}
