package mayday.tiala.pairwise.data.probes;

import java.util.Arrays;
import java.util.List;

import mayday.tiala.pairwise.data.AlignedDataSets;
import mayday.tiala.pairwise.data.AlignedDataSets.DII;
import mayday.tiala.pairwise.data.mastertables.PairedProbesMasterTable;

public class PairedProbe extends AlignmentDerivedProbe {

	public final boolean firstProbe;

	public PairedProbe(String sourceName, boolean isFirstProbe, PairedProbesMasterTable mata) {
		super(mata, sourceName);
		firstProbe = isFirstProbe;
		String mappedName = sourceName+"~"+getParentMasterTable(firstProbe).getDataSet().getName();
		setName(mappedName);
		values = null;
	}
	
	public void reset() {
		values=null;
	}

	protected double[] values() {
		if (values==null) {
			values = new double[getMasterTable().getNumberOfExperiments()];
			Arrays.fill(values, Double.NaN);

			int pidx = firstProbe?0:1;
			
			double[] sourceValues = ((PairedProbesMasterTable)getDerivedMasterTable()).getParent(pidx).getProbe(sourceName).getValues();
			
			List<DII> alignment = store.getShowOnlyMatching() ? store.getAlignedDataSets().getMatching() : store.getAlignedDataSets().getAll();
			
			Integer[] indices_to_fill = firstProbe ? AlignedDataSets.firstIndices(alignment) : AlignedDataSets.secondIndices(alignment);

			for (int i=0; i!=indices_to_fill.length; ++i) {
				Integer i2f = indices_to_fill[i];					
				if (i2f!=null) {
					Double nextValue = sourceValues[i2f];
					try {
						values[i] = nextValue;
					} catch (Throwable t) {
						System.out.println(t.getMessage());
						AlignedDataSets.firstIndices(alignment);
					}
				}
			}

		}
		return values;
	}

}