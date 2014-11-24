package mayday.Reveal.settings;

import java.util.List;

import mayday.Reveal.data.SNPList;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class SNPListSetting extends HierarchicalSetting {

	private List<SNPList> snpLists;
	private RestrictedStringSetting snpListSelection;
	
	public SNPListSetting(List<SNPList> snpLists) {
		super("SNPList Setting");
		
		this.snpLists = snpLists;
		
		String[] snpListNames = new String[snpLists.size()];
		
		for(int i = 0; i < snpListNames.length; i++) {
			snpListNames[i] = snpLists.get(i).getAttribute().getName();
		}
		
		addSetting(snpListSelection = new RestrictedStringSetting("SNPList", "Select the SNPList of choice.", 0, snpListNames));
	}
	
	public SNPList getSelectedSNPList() {
		return snpLists.get(snpListSelection.getSelectedIndex());
	}
	
	public SNPListSetting clone() {
		SNPListSetting sls = new SNPListSetting(snpLists);
		sls.fromPrefNode(this.toPrefNode());
		return sls;
	}
}
