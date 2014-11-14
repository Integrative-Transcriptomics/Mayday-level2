package mayday.tiala.multi.data.mastertables;

import java.util.HashMap;
import java.util.Map;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.probes.MultiProbe;

/**
 * @author jaeger
 *
 */
public class MultiProbesMasterTable extends MergedMasterTable {

	/**
	 * @param Store
	 * @param dataSet
	 */
	public MultiProbesMasterTable(AlignmentStore Store, DataSet dataSet) {
		super(Store, dataSet);
	}

	@Override
	protected Probe[] deriveProbes(String sourceName, int numOfProbes) {
		Probe[] probes = new Probe[numOfProbes];
		for(int i = 0; i < numOfProbes; i++) {
			probes[i] = new MultiProbe(sourceName, i, this);
		}
		return probes;
	}

	@Override
	protected void refreshProbes(boolean force) {
		for (Probe pb : probes.values()) {
			((MultiProbe)pb).reset();
		}
	}
	
	/**
	 * @param position
	 * @return all probes from dataset with index = position
	 */
	public Map<String, Probe> getProbes(int position) {
		Map<String, Probe> pbs = new HashMap<String, Probe>();
		for(Probe pb : probes.values()) {
			if(((MultiProbe)pb).position == position) {
				String ending = "~"+((MultiProbe)pb).getParentName();
				int end = pb.getName().lastIndexOf(ending);
				String name = pb.getName().substring(0, end);
				pbs.put(name, pb);
			}
		}
		return pbs;
	}
}
