package mayday.tiala.pairwise.data.mastertables;

import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.tiala.pairwise.data.AlignmentStore;

public abstract class MergedMasterTable extends AlignmentDerivedMasterTable {

	public MultiTreeMap<String, Probe> linkedProbes;
	
	public MergedMasterTable(AlignmentStore Store, DataSet dataSet) {
		super(Store, dataSet);
	}
	
	protected abstract Probe[] deriveProbes(String sourceName);	

	protected void addDerivedProbes(String sourceName) {
		if (linkedProbes==null)
			linkedProbes = new MultiTreeMap<String, Probe>();
		Probe[] dp = deriveProbes(sourceName);
		linkedProbes.put(sourceName, dp[0]);
		linkedProbes.put(sourceName, dp[1]);
		probes.put(dp[0].getName(), dp[0]);
		probes.put(dp[1].getName(), dp[1]);
	}
	
	public List<Probe> getProbes(String basename) {
		return linkedProbes.get(basename);
	}
	
	protected Set<String> getProbeNames() {
		return linkedProbes.keySet();
	}

	protected void removeDerivedProbes(String sourceName) {
		for (Probe pb : getProbes(sourceName))
			this.removeProbe(pb, true);
	}
	
	protected void updateNumberOfExperiments() {
		super.updateNumberOfExperiments();
		super.setNumberOfExperiments(alignmentExperimentNames.size(), true);
		setExperimentNames(alignmentExperimentNames);
	}

}


