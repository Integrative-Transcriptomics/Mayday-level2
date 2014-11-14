package mayday.GWAS.actions;

import javax.swing.AbstractAction;

import mayday.GWAS.data.ProjectHandler;

@SuppressWarnings("serial")
public abstract class RevealAction extends AbstractAction {
	
	protected ProjectHandler projectHandler;
	
	public RevealAction(ProjectHandler projectHandler) {
		this.projectHandler = projectHandler;
	}
}
