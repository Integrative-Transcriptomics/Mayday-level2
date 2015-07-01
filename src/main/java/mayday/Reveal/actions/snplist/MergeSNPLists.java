package mayday.Reveal.actions.snplist;

import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.utilities.SNVLists;

public class MergeSNPLists extends SNVListPlugin {

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
	public void run(Collection<SNVList> snpLists) {
		SNVList merged = SNVLists.createUniqueSNVList(snpLists);
		if(merged != null) {
			DataStorage ds = merged.getDataStorage();
			if(ds == null) {
				JOptionPane.showMessageDialog(null, "No project has been selected!");
				return;
			}
			ds.addSNVList(merged.getAttribute().getName(), merged);
		}
	}
}
