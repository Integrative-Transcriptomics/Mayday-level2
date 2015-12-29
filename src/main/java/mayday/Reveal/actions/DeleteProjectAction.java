package mayday.Reveal.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.ProjectHandler;
import mayday.core.DataSet;
import mayday.core.datasetmanager.DataSetManager;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class DeleteProjectAction extends RevealAction {
	
	public DeleteProjectAction(ProjectHandler projectHandler) {
		super(projectHandler);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DataStorage dataStorage = projectHandler.getSelectedProject();
		if(dataStorage != null) {
			String projectName = dataStorage.getAttribute().getName();
			int value = JOptionPane.showConfirmDialog(null, "Are you sure, that you want to delete the project:\n\n" + projectName + "\n", 
					"Deleting project...", JOptionPane.YES_NO_OPTION);
			if(value == JOptionPane.YES_OPTION) {
				DataSet ds = dataStorage.getDataSet();
				projectHandler.remove(dataStorage);
				DataSetManager.singleInstance.removeObject(ds);
				
				System.gc();
			}
		}
	}
}