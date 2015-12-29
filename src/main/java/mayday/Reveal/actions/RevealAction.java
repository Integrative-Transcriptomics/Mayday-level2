package mayday.Reveal.actions;

import javax.swing.AbstractAction;

import mayday.Reveal.data.ProjectHandler;

@SuppressWarnings("serial")
public abstract class RevealAction extends AbstractAction {
	
	protected ProjectHandler projectHandler;
	
	public RevealAction(ProjectHandler projectHandler) {
		this.projectHandler = projectHandler;
	}
}
