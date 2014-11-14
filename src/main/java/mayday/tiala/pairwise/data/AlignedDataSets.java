package mayday.tiala.pairwise.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.TimeseriesMIO;
import mayday.core.structures.linalg.vector.DoubleVector;

public class AlignedDataSets {

	protected Double timeshift; // null if manual mapping, time shift otherwise
	protected ArrayList<DII> timePointMappingALL = new ArrayList<DII>();
	protected ArrayList<DII> timePointMappingMATCHING = new ArrayList<DII>();
	
	protected TimepointDataSet one, two;
	protected AlignmentStore store;
	
	/**
	 * Constructor
	 * @param td1 the first dataset
	 * @param td2 the second dataset
	 * @param timeShift the relative shift of td2, i.e. -5 means that all times in td2 will be mapped
	 * to times in td1 that are 5 units earlier. 
	 */
	public AlignedDataSets( AlignmentStore Store, TimepointDataSet td1, TimepointDataSet td2, double timeShift ) {
		this.timeshift = timeShift;
		one = td1;
		two = td2;
		store=Store;
		populateWithTimeShift(timeShift);
	}
	
	public DataSet getFirstDataSet() {
		return one.getDataSet();
	}
	
	public DataSet getSecondDataSet() {
		return two.getDataSet();
	}
	
	public TimepointDataSet getFirst() {
		return one;
	}
	
	public TimepointDataSet getSecond() {
		return two;
	}
	
	public void changeShift(double newShift) {
		populateWithTimeShift(newShift);
	}
	
	protected void populateWithTimeShift(double timeShift) {
		timeshift = timeShift;
		timePointMappingALL.clear();
		timePointMappingMATCHING.clear();
		
		// find out which experiments map onto each other via times
		LinkedList<Double> shifted1 = new LinkedList<Double>(one);
		LinkedList<Double> shifted2 = new LinkedList<Double>();
		for (Double d : two)
			shifted2.add(d+timeShift);	

		TreeSet<Double> all = new TreeSet<Double>(shifted1);
		all.addAll(shifted2);
		
		for (double timePoint : all) {
			Integer i1 = one.indexOf(timePoint);
			Integer i2 = two.indexOf(timePoint-timeShift);
			if (i1<0) i1 = null;
			if (i2<0) i2 = null;
			DII mapping = new DII(timePoint, i1, i2);
			timePointMappingALL.add(mapping);
			if (i1!=null && i2!=null) 
				timePointMappingMATCHING.add(mapping);
		}	
	}
	
	public double getTimeShift() {
		return timeshift;
	}
	
	public List<DII> getMatching() {
		return Collections.unmodifiableList(timePointMappingMATCHING);
	}
	
	public List<DII> getAll() {
		return Collections.unmodifiableList(timePointMappingALL);
	}
	
	public int getCommonCount() {
		return timePointMappingMATCHING.size();
	}
	
	public HashMap<String, Double> getScores(Collection<String> probeNames, DistanceMeasurePlugin distance) {
		HashMap<String, Double> ret = new HashMap<String, Double>();
		for (String pbn : probeNames) {
			DoubleVector v1 = getVector(one, pbn);
			DoubleVector v2 = getVector(two, pbn);
			ret.put(pbn, distance.getDistance(v1, v2));
		}
		return ret;
	}
	
	public HashMap<String, Double> getScores(Collection<String> probeNames) {
		DistanceMeasurePlugin sf = store.getScoringFunction();
		return getScores(probeNames, sf);
	}
	
	public HashMap<String, Double> getScores(Boolean allProbes) {
		if (allProbes==null) {
			return getScores();
		} else {
			Collection<String> names;
			if (allProbes) {
				names = store.getAlignedDataSets().getCommonProbeNames();
			} else {
				names = new TreeSet<String>();
				for (Probe pb : store.getVisualizerOne().getViewModel().getProbes())
					names.add(pb.getName());
			}
			return getScores(names);
		}
	}
	
	public HashMap<String, Double> getScores() {
		return getScores(store.isScoringForAll());
	}
	
	public Collection<String> getCommonProbeNames() {
		TreeSet<String> ret = new TreeSet<String>();
		ret.addAll(one.getDataSet().getMasterTable().getProbes().keySet());
		ret.retainAll(two.getDataSet().getMasterTable().getProbes().keySet());
		return ret;
	}
	
	
	
	public DoubleVector getVector(TimepointDataSet tds, String name) {
		Probe pb = tds.getDataSet().getMasterTable().getProbe(name);
		if (pb==null)
			return null;
		DoubleVector ret = new DoubleVector(timePointMappingMATCHING.size());
		double[] field = ret.toArrayUnpermuted();
		int i=0;
		for (DII dii : timePointMappingMATCHING) {
			int index;
			if (tds == one)
				index = dii.getIdx1();
			else 
				index = dii.getIdx2();
			field[i++] = pb.getValue(index);
		}
		return ret;
	}
	

	public Collection<DataSet> deriveDataSets() {
		LinkedList<DataSet> lds = new LinkedList<DataSet>();
		lds.add(deriveDataSet(true));
		lds.add(deriveDataSet(false));
		return lds;
	}
	
	protected DataSet deriveDataSet(boolean first) {
		String modifier = first?" (master)":" (aligned)";
		DataSet sourceDS = (first?getFirstDataSet():getSecondDataSet());
		DataSet targetDS = new DataSet( sourceDS.getName() + modifier);
		MasterTable mata = targetDS.getMasterTable();
		ArrayList<DII> diis = timePointMappingMATCHING;
		mata.setNumberOfExperiments(diis.size());
		LinkedList<Double> timePoints = new LinkedList<Double>();
		for (int i=0; i!=diis.size(); ++i) {
			DII currentItem = diis.get(i);
			mata.setExperimentName(i, sourceDS.getMasterTable().getExperimentName((first?currentItem.getIdx1():currentItem.getIdx2())));
			timePoints.add(currentItem.getTime());
		}
		// Probes
		for (Probe pb : sourceDS.getMasterTable().getProbes().values()) {
			Probe pb2 = new Probe(mata);
			pb2.setName(pb.getName());
			for (DII currentItem: diis)
				pb2.addExperiment(pb.getValue(first?currentItem.getIdx1():currentItem.getIdx2()));
			mata.addProbe(pb2);
		}	
		MIGroup mg = TimeseriesMIO.getGroupInstance(targetDS.getMIManager());
		TimeseriesMIO tsm = (TimeseriesMIO)mg.add(targetDS);
		tsm.setValue(timePoints);
		assert tsm.applicableTo(targetDS);
		return targetDS;
	}
	
	public static class DII {
		
		Double time;
		Integer idx1, idx2;

		public DII(Double a, Integer b, Integer c) {
			time = a;
			idx1 = b;
			idx2 = c;
		}

		public Double getTime() {
			return time;
		}

		public Integer getIdx1() {
			return idx1;
		}

		public Integer getIdx2() {
			return idx2;
		}

	}

	public static Integer[] secondIndices(List<DII> matches) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (DII dii : matches) {
			Integer i = dii.getIdx2();
				ret.add(i);
		}		
		return ret.toArray(new Integer[0]);
	}
	
	public static Integer[] firstIndices(List<DII> matches) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (DII dii : matches) {
			Integer i = dii.getIdx1();
				ret.add(i);
		}		
		return ret.toArray(new Integer[0]);
	}
	
}

