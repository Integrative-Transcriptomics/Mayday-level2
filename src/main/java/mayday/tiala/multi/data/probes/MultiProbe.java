package mayday.tiala.multi.data.probes;

import java.util.Arrays;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.tiala.multi.data.AlignedDataSets;
import mayday.tiala.multi.data.AlignedDataSets.DII;
import mayday.tiala.multi.data.mastertables.MultiProbesMasterTable;

/**
 * @author jaeger
 *
 */
public class MultiProbe extends AlignmentDerivedProbe {

	/**
	 * index of the respective data set
	 */
	public final int position;
	
	protected String displayName;
	
	/**
	 * @param sourceName
	 * @param position
	 * @param multiProbesMasterTable
	 */
	public MultiProbe(String sourceName, int position, MultiProbesMasterTable multiProbesMasterTable) {
		super(multiProbesMasterTable, sourceName);
		this.position = position;
		String mappedName = sourceName+"~"+getParentName();
		setName(mappedName);
		displayName = sourceName;
		values = null;
	}
	
	/**
	 * @return name of the parent data set
	 */
	public String getParentName() {
		return getParentMasterTable(position).getDataSet().getName();
	}
	
	public String getDisplayName()	{
		if (masterTable!=null) {
			MIGroup mg = masterTable.getDataSet().getProbeDisplayNames();
			if (mg!=null) {
				MIType mt = mg.getMIO(this);
				if (mt==null || mt.toString().trim().length()==0)
					displayName+="*";
				else
					displayName=mt.toString();
			}
		}
		return displayName;
	}
	
	/**
	 * clear the expression values
	 */
	public void reset() {
		values=null;
	}

	@Override
	protected double[] values() {
		if (values==null) {
			values = new double[getMasterTable().getNumberOfExperiments()];
			Arrays.fill(values, Double.NaN);
			
			MultiProbesMasterTable table = ((MultiProbesMasterTable)getDerivedMasterTable());
			MasterTable parent = table.getMasterTable(position);
			Probe probe = parent.getProbe(sourceName);
			
			double[] sourceValues = probe.getValues();
			
			List<DII> alignment = store.getSettings().showOnlyMatching() ? 
					store.getAlignedDataSets().getMatchingAll().get(position) : 
						store.getAlignedDataSets().getMappingAll().get(position);
			Integer[] indices_to_fill = AlignedDataSets.indices(alignment);

			for (int i = 0; i != indices_to_fill.length; ++i) {
				Integer i2f = indices_to_fill[i];
				if (i2f != null) {
					Double nextValue = sourceValues[i2f];
					try {
						values[i] = nextValue;
					} catch (Throwable t) {
						System.out.println(t.getMessage());
					}
				}
			}
		}
		return values;
	}
}
