package mayday.Reveal.io.project.factory;

import javax.swing.AbstractAction;

import mayday.Reveal.io.project.ProjectDefinition;

public abstract class AbstractProjectDefinition implements ProjectDefinition {

	protected ProjectDefinition previous = null;
	protected ProjectDefinition next = null;
	
	@Override
	public ProjectDefinition getNext() {
		return this.next;
	}

	@Override
	public ProjectDefinition getPrevious() {
		return this.previous;
	}

	@Override
	public void setPrevious(ProjectDefinition previous) {
		this.previous = previous;
	}

	@Override
	public void setNext(ProjectDefinition next) {
		this.next = next;
	}
	
	public boolean isFinal() {
		return false;
	}
	
	public void setFinalizeAction(AbstractAction action) {
		//nothing to do in general
	}
}
