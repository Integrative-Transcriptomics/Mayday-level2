package mayday.tiala.pairwise.data.probes;

import java.util.Arrays;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.tiala.pairwise.data.AlignedDataSets;
import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.data.AlignedDataSets.DII;
import mayday.tiala.pairwise.data.mastertables.AlignmentDerivedMasterTable;
import mayday.tiala.pairwise.data.mastertables.StatisticsMasterTable;

public class StatisticsProbe extends DerivedProbe {

	protected AlignmentStore store;
	protected int statisticHash;
	
	public StatisticsProbe(String sourceName, StatisticsMasterTable admt) {
		super(admt, sourceName );
		setName(sourceName);
		store = admt.getStore();
		values = null;
	}
	

	protected int getNoE() {
		return (getDerivedMasterTable() instanceof AlignmentDerivedMasterTable) ? 
				((AlignmentDerivedMasterTable)getMasterTable()).getAlignmentNumberOfExperiments() :
				getMasterTable().getNumberOfExperiments();
	}
	
	public double[] getMappedSourceValues(int parent,Integer[] indices, int targetNoE) {
		MasterTable parentMT = getParentMasterTable(parent);
		Probe pb = parentMT.getProbe(name);
		double[] source = pb.getValues(); 
		double[] mappedValues = new double[targetNoE];
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
	
	public double[] getMappedSourceValues(int parent,Integer[] indices) {
		return getMappedSourceValues(parent, indices, indices.length);
	}

	public double[][] getMappedSourceValues() {
		List<DII> alignment = store.getShowOnlyMatching() ? store.getAlignedDataSets().getMatching() : store.getAlignedDataSets().getAll();								
		double[] mappedSource1 = getMappedSourceValues(0, AlignedDataSets.firstIndices(alignment), getNoE());
		double[] mappedSource2 = getMappedSourceValues(1, AlignedDataSets.secondIndices(alignment), getNoE());
		return new double[][]{mappedSource1, mappedSource2};
	}
	
	
	protected double[] getUnmappedSourceValues(int parent, String name) {
		MasterTable parentMT = getParentMasterTable(parent);
		Probe pb = parentMT.getProbe(name);
		return pb.getValues(); 
	}
	
	public double[][] getUnmappedSourceValues() {							
		double[] Source1 = getUnmappedSourceValues(0, name);
		double[] Source2 = getUnmappedSourceValues(1, name);
		return new double[][]{Source1, Source2};
	}
	
	

	protected double[] values() {
		return values;
	}

	public int getStatisticHash() {
		return statisticHash;
	}
	
	public void setStatisticHash(int hash) {
		statisticHash = hash;
	}
	
	public void setValuesFromStatistic(double[] values) {
		this.values = values;
	}
}
