package mayday.Reveal.actions.snplist;

import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.gui.menu.SNPListPopupMenu;

public class RemoveSNPList extends SNVListPlugin {

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
	public void run(Collection<SNVList> snpLists) {
		DataStorage ds = projectHandler.getSelectedProject();
		
		if(ds == null) {
			JOptionPane.showMessageDialog(null, "No project has been selected!");
			return;
		}
		
		ds.removeSNVLists(snpLists);
	}

	@Override
	public String getPopupMenuCategroy() {
		return SNPListPopupMenu.MANIPULATION_CATEGORY;
	}
}
