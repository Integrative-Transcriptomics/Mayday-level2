package mayday.tiala.multi.data.probes;

import java.util.Arrays;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.tiala.multi.data.AlignedDataSets;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignedDataSets.DII;
import mayday.tiala.multi.data.mastertables.AlignmentDerivedMasterTable;
import mayday.tiala.multi.data.mastertables.StatisticsMasterTable;

/**
 * @author jaeger
 *
 */
public class StatisticsProbe extends DerivedProbe {

	protected AlignmentStore store;
	protected int statisticHash;
	protected int dsID;
	
	/**
	 * @param dsID
	 * @param sourceName
	 * @param admt
	 */
	public StatisticsProbe(int dsID, String sourceName, StatisticsMasterTable admt) {
		super(admt, sourceName );
		setName(sourceName);
		store = admt.getStore();
		this.dsID = dsID;
		values = null;
	}
	
	protected int getNoE() {
		return (getDerivedMasterTable() instanceof AlignmentDerivedMasterTable) ? 
//				((AlignmentDerivedMasterTable)getMasterTable()).getStatAlignedNOE(dsID):
				((AlignmentDerivedMasterTable)getMasterTable()).getAlignmentNumberOfExperiments():
				getMasterTable().getNumberOfExperiments();
	}
	
	/**
	 * @param parent
	 * @param indices
	 * @param targetNoE
	 * @return mapped source values
	 */
	public double[] getMappedSourceValues(int parent, Integer[] indices, int targetNoE) {
		MasterTable parentMT = getParentMasterTable(parent);
		Probe pb = parentMT.getProbe(name);
		double[] source = pb.getValues();
		double[] mappedValues = new double[targetNoE];
		//System.out.println((parent+1) + " = " + Arrays.toString(indices));
		Arrays.fill(mappedValues, Double.NaN);
		for (int i=0; i!=indices.length; ++i) {
			Integer idx = indices[i];
			if (idx!=null) {
				Double nextValue = source[idx];
				mappedValues[i] = nextValue;
			}
		}
		return mappedValues;		
	}
	
	/**
	 * @param parent
	 * @param indices
	 * @return mapped source values
	 */
	public double[] getMappedSourceValues(int parent,Integer[] indices) {
		return getMappedSourceValues(parent, indices, indices.length);
	}

	/**
	 * @return mapped source values
	 */
	public double[][] getMappedSourceValues() {
//		List<DII> alignment = store.getSettings().showOnlyMatching() ? store.getAlignedDataSets().getStatMatching().get(dsID) : store.getAlignedDataSets().getStatMapping().get(dsID);
		List<DII> alignment = store.getSettings().showOnlyMatching() ? store.getAlignedDataSets().getMatchingAll().get(dsID+1) : store.getAlignedDataSets().getMappingAll().get(dsID+1);
		double[] mappedSource1 = getMappedSourceValues(0, AlignedDataSets.firstIndices(alignment), getNoE());
		double[] mappedSource2 = getMappedSourceValues(dsID+1, AlignedDataSets.secondIndices(alignment), getNoE());
		return new double[][]{mappedSource1, mappedSource2};
	}
	
	protected double[] getUnmappedSourceValues(int parent, String name) {
		MasterTable parentMT = getParentMasterTable(parent);
		Probe pb = parentMT.getProbe(name);
		return pb.getValues();
	}
	
	/**
	 * @return unmapped source values
	 */
	public double[][] getUnmappedSourceValues() {
		double[] Source1 = getUnmappedSourceValues(0, name);
		double[] Source2 = getUnmappedSourceValues(dsID+1, name);
		return new double[][]{Source1, Source2};
	}
	
	protected double[] values() {
		return values;
	}

	/**
	 * @return statistic hash
	 */
	public int getStatisticHash() {
		return statisticHash;
	}
	
	/**
	 * @param hash
	 */
	public void setStatisticHash(int hash) {
		statisticHash = hash;
	}
	
	/**
	 * @param values
	 */
	public void setValuesFromStatistic(double[] values) {
		this.values = values;
	}
}
