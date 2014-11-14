package mayday.GWAS.actions;

import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.GWAS.RevealPlugin;
import mayday.GWAS.actions.io.SaveProject;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.gui.RevealGUI;
import mayday.GWAS.utilities.RevealMenuConstants;

/**
 * @author jaeger
 *
 */
public class ExitReveal extends RevealPlugin {
	
	public void exit(final RevealGUI gui) {
		int numProjects = projectHandler.numberOfProjects();
		
		if(numProjects > 0) {
			int option = JOptionPane.showConfirmDialog(null, "There are open projects, that might not have been saved.\n" +
					"Do you want to save them first?");
			switch(option) {
			case JOptionPane.CANCEL_OPTION:
				break;
			case JOptionPane.NO_OPTION:
				projectHandler.clear();
				gui.dispose();
				break;
			case JOptionPane.YES_OPTION:
				SaveProject a = new SaveProject();
				a.setProjectHandler(projectHandler);
				a.saveAndExit(gui, true);
				break;
			}
		} else {
			projectHandler.clear();
			gui.dispose();
		}
	}

	@Override
	public String getName() {
		return "Exit Reveal";
	}


	@Override
	public String getType() {
		return "exitReveal";
	}


	@Override
	public String getDescription() {
		return "Close the current Reveal session";
	}


	@Override
	public String getMenuName() {
		return "Exit";
	}


	@Override
	public void run(Collection<SNPList> snpLists) {
		exit(projectHandler.getGUI());
	}


	@Override
	public String getMenu() {
		return RevealMenuConstants.FILE_MENU;
	}


	@Override
	public String getCategory() {
		return null;
	}
}
