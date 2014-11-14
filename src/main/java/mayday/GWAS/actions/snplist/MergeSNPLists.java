package mayday.GWAS.actions.snplist;

import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.utilities.SNPLists;

public class MergeSNPLists extends SNPListPlugin {

	@Override
	public String getName() {
		return "Merge SNPLists";
	}

	@Override
	public String getType() {
		return "data.snplist.mergeSNPLists";
	}

	@Override
	public String getDescription() {
		return "Merge existing SNPLists into a single SNPList";
	}

	@Override
	public String getMenuName() {
		return "Merge";
	}

	@Override
	public void run(Collection<SNPList> snpLists) {
		SNPList merged = SNPLists.createUniqueSNPList(snpLists);
		if(merged != null) {
			DataStorage ds = merged.getDataStorage();
			if(ds == null) {
				JOptionPane.showMessageDialog(null, "No project has been selected!");
				return;
			}
			ds.addSNPList(merged.getAttribute().getName(), merged);
		}
	}
}
