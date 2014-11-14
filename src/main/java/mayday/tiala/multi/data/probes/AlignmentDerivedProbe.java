package mayday.tiala.multi.data.probes;

import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.mastertables.AlignmentDerivedMasterTable;

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
