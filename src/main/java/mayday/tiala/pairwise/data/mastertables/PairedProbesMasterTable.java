package mayday.tiala.pairwise.data.mastertables;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.data.probes.PairedProbe;

public class PairedProbesMasterTable extends MergedMasterTable  {
	
	public PairedProbesMasterTable(AlignmentStore Store, DataSet dataSet) {
		super(Store, dataSet);
	}

	protected void refreshProbes(boolean force) {
		for (Probe pb : probes.values()) {
			((PairedProbe)pb).reset();
		}
	}

	protected Probe[] deriveProbes(String sourceName) {
		Probe p1 = new PairedProbe(sourceName, true, this);
		Probe p2 = new PairedProbe(sourceName, false, this);
		return new Probe[]{p1,p2};
	}

}
