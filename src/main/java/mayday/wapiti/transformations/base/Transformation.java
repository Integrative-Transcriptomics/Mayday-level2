package mayday.wapiti.transformations.base;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import mayday.core.tasks.ProgressListener;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.gui.TransformationButton;
import mayday.wapiti.transformations.matrix.TransMatrix;
import mayday.wapiti.transformations.matrix.TransMatrixElement;

public interface Transformation extends Comparable<Transformation>, TransMatrixElement {
	
	public boolean applicableTo(Collection<Experiment> exps);

	public String getApplicabilityRequirements();	
	
	public void compute();			

	public TransformationButton getGUIElement(Experiment e);
	
	public ExperimentState getExperimentState(Experiment e);
	
	public void resetCache();
	
	public void setTransMatrix(TransMatrix tm);
	
	public void setProgressListener(ProgressListener pl);
	
	public void writeToStream( OutputStream os, File targetDir ) throws IOException;
	
	// if settings need to be initialized depending on the selected experiments, it can be done in this method.
	// will be called after getSettings();
	public void updateSettings(Collection<Experiment> experiments); 

}
