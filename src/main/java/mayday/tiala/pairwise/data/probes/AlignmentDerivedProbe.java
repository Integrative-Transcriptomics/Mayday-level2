package mayday.tiala.pairwise.data.probes;

import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.data.mastertables.AlignmentDerivedMasterTable;

public abstract class AlignmentDerivedProbe extends DerivedProbe {

	protected AlignmentStore store;
	
	public AlignmentDerivedProbe( AlignmentDerivedMasterTable masterTable, String sourceName ) {
		super(masterTable, sourceName);
		store = masterTable.getStore();
	}
	
	public AlignmentStore getStore() {
		return store;
	}

}
