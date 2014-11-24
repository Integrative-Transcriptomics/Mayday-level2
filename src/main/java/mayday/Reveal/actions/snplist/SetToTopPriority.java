package mayday.Reveal.actions.snplist;

import java.util.Collection;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.utilities.SNPLists;
import mayday.Reveal.viewmodel.RevealViewModel;

public class SetToTopPriority extends SNPListPlugin {

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
	public void run(Collection<SNPList> snpLists) {
		if(snpLists != null && snpLists.size() >= 1) {
			DataStorage ds = projectHandler.getSelectedProject();
			RevealViewModel vm = projectHandler.getViewModel(ds);
			
			for(SNPList sl : ds.getSNPLists()) {
				sl.setTopPriority(false);
			}
			
			for(SNPList sl : snpLists) {
				sl.setTopPriority(true);
			}
			
			vm.setTopPrioritySNPList(SNPLists.createUniqueSNPList(snpLists));
		}
	}
}
