package mayday.Reveal.actions.snplist;

import java.util.Collection;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.utilities.SNVLists;
import mayday.Reveal.viewmodel.RevealViewModel;

public class SetToTopPriority extends SNVListPlugin {

	@Override
	public String getName() {
		return "Set Top Priority";
	}

	@Override
	public String getType() {
		return "data.snplist.setTopPriority";
	}

	@Override
	public String getDescription() {
		return "Give the selected SNPList Top Priority";
	}

	@Override
	public String getMenuName() {
		return "Set Top Priority";
	}

	@Override
	public void run(Collection<SNVList> snpLists) {
		if(snpLists != null && snpLists.size() >= 1) {
			DataStorage ds = projectHandler.getSelectedProject();
			RevealViewModel vm = projectHandler.getViewModel(ds);
			
			for(SNVList sl : ds.getSNVLists()) {
				sl.setTopPriority(false);
			}
			
			for(SNVList sl : snpLists) {
				sl.setTopPriority(true);
			}
			
			vm.setTopPrioritySNPList(SNVLists.createUniqueSNVList(snpLists));
		}
	}
}
