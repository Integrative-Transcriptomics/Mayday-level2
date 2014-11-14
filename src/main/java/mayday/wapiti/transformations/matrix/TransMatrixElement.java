package mayday.wapiti.transformations.matrix;

import mayday.core.settings.Setting;
import mayday.wapiti.experiments.base.Experiment;

public interface TransMatrixElement {

	public String getIdentifier(Experiment e);
	public String getName();
	public Setting getSetting(); 
	
	public TransMatrix getTransMatrix();
}
