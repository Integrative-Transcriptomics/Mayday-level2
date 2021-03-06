package mayday.Reveal.actions;

import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.Reveal.RevealPlugin;
import mayday.Reveal.actions.io.SaveProject;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.gui.RevealGUI;
import mayday.Reveal.utilities.RevealMenuConstants;

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
	public void run(Collection<SNVList> snpLists) {
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
