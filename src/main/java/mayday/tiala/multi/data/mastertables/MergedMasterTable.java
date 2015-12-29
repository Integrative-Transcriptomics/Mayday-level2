package mayday.tiala.multi.data.mastertables;

import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.tiala.multi.data.AlignmentStore;

public abstract class MergedMasterTable extends AlignmentDerivedMasterTable {

	public MultiTreeMap<String, Probe> linkedProbes;
	
	public MergedMasterTable(AlignmentStore Store, DataSet dataSet) {
		super(-1, Store, dataSet);
	}
	
	protected abstract Probe[] deriveProbes(String sourceName, int numOfProbes);	

	protected void addDerivedProbes(String sourceName, int numOfProbes) {
		if (linkedProbes==null)
			linkedProbes = new MultiTreeMap<String, Probe>();
		Probe[] dp = deriveProbes(sourceName, numOfProbes);
		
		for(int i = 0; i < numOfProbes; i++) {
			linkedProbes.put(sourceName, dp[i]);
			probes.put(dp[i].getName(), dp[i]);
		}
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
