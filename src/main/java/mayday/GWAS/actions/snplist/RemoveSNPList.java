package mayday.GWAS.actions.snplist;

import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.SNPList;

public class RemoveSNPList extends SNPListPlugin {

	@Override
	public String getName() {
		return "Delete SNPList";
	}

	@Override
	public String getType() {
		return "data.snplist.removeSNPList";
	}

	@Override
	public String getDescription() {
		return "Delete an existing SNPList";
	}

	@Override
	public String getMenuName() {
		return "Remove";
	}

	@Override
	public void run(Collection<SNPList> snpLists) {
		DataStorage ds = projectHandler.getSelectedProject();
		
		if(ds == null) {
			JOptionPane.showMessageDialog(null, "No project has been selected!");
			return;
		}
		
		ds.removeSNPLists(snpLists);
	}
}
