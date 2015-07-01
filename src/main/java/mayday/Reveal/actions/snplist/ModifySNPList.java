package mayday.Reveal.actions.snplist;

import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNVList;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;

public class ModifySNPList extends SNVListPlugin {
	
	public void triggerAction(Collection<SNVList> snpLists) throws Exception {
		DataStorage ds = projectHandler.getSelectedProject();
		if(ds == null)
			throw new Exception("No project has been selected!");
		SNVList global = ds.getGlobalSNVList();
		
		if(snpLists.contains(global)) {
			snpLists.remove(global);
		}
		
		if(snpLists.size() > 0) {
			AbstractPropertiesDialog apd = PropertiesDialogFactory.createDialog(snpLists.toArray(new SNVList[0]));
			apd.setModal(true);
			apd.setVisible(true);
		} else {
			throw new Exception("No modifiable SNPList selected!");
		}
	}
	
	public void triggerAction(SNVList snpList) throws Exception {
		DataStorage ds = projectHandler.getSelectedProject();
		SNVList global = ds.getGlobalSNVList();
		if(snpList == global) {
			throw new Exception("The SNPList \"" + global.getAttribute().getName() + "\" cannot be modified!");
		} else {
			AbstractPropertiesDialog apd = PropertiesDialogFactory.createDialog(snpList);
			apd.setModal(true);
			apd.setVisible(true);
		}
	}

	@Override
	public String getName() {
		return "Modify SNPList";
	}

	@Override
	public String getType() {
		return "data.snplist.modifySNPList";
	}

	@Override
	public String getDescription() {
		return "Modify an existing SNPList";
	}

	@Override
	public String getMenuName() {
		return "Properties";
	}

	@Override
	public void run(Collection<SNVList> snpLists) {
		try {
			triggerAction(snpLists);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}
