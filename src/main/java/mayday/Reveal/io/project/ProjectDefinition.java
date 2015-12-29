package mayday.Reveal.io.project;

import javax.swing.AbstractAction;

import mayday.Reveal.io.project.factory.ProjectCreator;
import mayday.core.settings.generic.HierarchicalSetting;

public interface ProjectDefinition {
	
	//setting class that contains the setting for this project definition
	public HierarchicalSetting getSetting();
	
	//provide the next project definition
	public ProjectDefinition getNext();

	//return the last project definition
	public ProjectDefinition getPrevious();
	
	//set the previous project definition
	public void setPrevious(ProjectDefinition previous);
	
	//set the next project definition
	public void setNext(ProjectDefinition next);

	public ProjectCreator getCreator();

	public boolean isFinal();
	
	public void setFinalizeAction(AbstractAction action);
}
