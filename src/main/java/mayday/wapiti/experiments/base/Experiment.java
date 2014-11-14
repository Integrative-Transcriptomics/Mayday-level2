package mayday.wapiti.experiments.base;

import java.util.List;

import mayday.core.meta.MIGroup;
import mayday.wapiti.gui.ExperimentPanel;
import mayday.wapiti.transformations.matrix.TransMatrixElement;

public interface Experiment extends Comparable<Experiment>, TransMatrixElement, ExperimentState {
	
	public String getSourceDescription();

	public ExperimentPanel getGUIElement();
	
	// overrides TransMatrixElement:getSettings()
	public ExperimentSetting getSetting();
	
	public ExperimentState getCurrentState();
	
	public ExperimentState getInitialState();
	
	public ExperimentData getInitialData();

	public void setName(String newName);
	
	public String getIdentifier();
	
	public List<MIGroup> getAnnotations();
	
	@SuppressWarnings("unchecked")
	public AbstractExperimentSerializer getSerializer();
	
}