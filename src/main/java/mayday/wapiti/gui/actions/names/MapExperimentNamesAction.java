
package mayday.wapiti.gui.actions.names;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import mayday.core.settings.SettingDialog;
import mayday.wapiti.containers.identifiermapping.IdentifierMap;
import mayday.wapiti.containers.identifiermapping.IdentifierMapSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.gui.ExperimentPanel;
import mayday.wapiti.gui.layeredpane.ReorderableHorizontalPanel;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class MapExperimentNamesAction extends AbstractAction {

	protected IdentifierMapSetting setting = new IdentifierMapSetting();
	
	private final TransMatrix transMatrix;
	private final SelectionModel selection;

	public MapExperimentNamesAction(TransMatrix transMatrix, SelectionModel sm) {
		super("Map Experiment Names");
		this.transMatrix = transMatrix;
		this.selection = sm;
	}

	public void actionPerformed(ActionEvent e) {
		if (selection.size()>0) {
			
			SettingDialog sd = new SettingDialog(transMatrix.getFrame(), "Map Experiment Names", setting);
			sd.showAsInputDialog();
			if (sd.canceled())
				return;
			
			List<Experiment> le = new LinkedList<Experiment>();
			for (ReorderableHorizontalPanel rhp : selection.getSelection())
				le.add(((ExperimentPanel)rhp).getExperiment());
			
			IdentifierMap idm = setting.getIdentifierMap();
			if (idm==null)
				return;
			
			for (Experiment ex : le) {
				String curName = ex.getName();
				String newName = idm.map(curName);
				if (!newName.equals(curName)) {
					ex.setName(newName);
				}
			}
			
			transMatrix.getPane().getTMLayout().updateLayout();
		} else {
			JOptionPane.showMessageDialog(null, 
					"Please select which experiments the mapping should work on.", 
					"Unable to map experiment names", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
}