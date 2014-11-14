package mayday.GWAS.io.project;

import java.util.Collection;

import mayday.GWAS.RevealPlugin;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.io.project.factory.InputFormatSelector;
import mayday.GWAS.io.project.factory.ProjectCreator;
import mayday.GWAS.utilities.RevealMenuConstants;

public class NewProject extends RevealPlugin {

	@Override
	public String getName() {
		return "New Reveal Project";
	}

	@Override
	public String getType() {
		return "project.newproject";
	}

	@Override
	public String getDescription() {
		return "Create a new Reveal Project";
	}

	@Override
	public String getMenuName() {
		return "New Project";
	}

	@Override
	public void run(Collection<SNPList> snpLists) {
		ProjectMediator decorator = new ProjectMediator(getMenuName(), this.projectHandler.getGUI());
		
		InputFormatSelector inputFS = new InputFormatSelector();
		decorator.setInitialProjectDefinition(inputFS);
		
		decorator.setVisible(true);
		
		if(!decorator.closedWithOK()) {
			return;
		}
		
		ProjectCreator pc = decorator.getProjectCreator();
		
		if(pc != null) {
			pc.createNewProject(projectHandler);
		}
	}

	@Override
	public String getMenu() {
		return RevealMenuConstants.FILE_MENU;
	}

	@Override
	public String getCategory() {
		return "Project";
	}
}
