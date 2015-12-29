package mayday.tiala.multi.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.TimeseriesMIO;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.tiala.multi.data.container.TimeShifts;
import mayday.tiala.multi.data.container.TimepointDataSets;

/**
 * @author jaeger
 */
public class AlignedDataSets {

	protected ArrayList<List<DII>> tPMapping = new ArrayList<List<DII>>();
	protected ArrayList<List<DII>> tPMatching = new ArrayList<List<DII>>();
	
	protected TimepointDataSets dataSets;
	protected AlignmentStore store;
	protected TimeShifts timeshifts;
	
	protected ArrayList<Double> alignment;
	protected ArrayList<Double> allExperiments;
	
	/**
	 * @param Store
	 * @param dataSets
	 * @param timeShifts
	 */
	public AlignedDataSets(AlignmentStore Store, TimepointDataSets dataSets, TimeShifts timeShifts) {
		this.timeshifts = timeShifts;
		this.dataSets = dataSets;
		this.store = Store;
		this.populateWithTimeShifts();
	}

	/**
	 * @param datasetID
	 * @return data set at position 'datasetID'
	 */
	public DataSet getDataSet(int datasetID) {
		if(datasetID >= 0 && datasetID < dataSets.size()) {
			return dataSets.get(datasetID).getDataSet();
		}
		return null;
	}
	
	/**
	 * @param datasetID
	 * @return time-point data-set at position 'which'
	 */
	public TimepointDataSet get(int datasetID) {
		if(datasetID >= 0 && datasetID < dataSets.size()) {
			return dataSets.get(datasetID);
		}
		return null;
	}
	
	/**
	 * @param newShifts
	 */
	public void changeShifts(double[] newShifts) {
		timeshifts = new TimeShifts(newShifts);
		populateWithTimeShifts();
	}
	
	/**
	 * @param index
	 * @param value
	 */
	public void changeShift(int index, double value) {
		timeshifts.set(index, value);
		this.populateWithTimeShifts();
	}
	
	/**
	 * @param timeShifts , an array of time shifts between the dataSets 
	 * (e.g. timeShifts[0] is the shift between dataSet[0] and dataSet[1])
	 */
	protected void populateWithTimeShifts() {
		//for multiple alignment
		tPMapping.clear();
		tPMatching.clear();
		
		if(dataSets.size() == 0) {
			return;
		}
		
		TimepointDataSet first = dataSets.get(0);
		
		Set<Double> alignmentSet = new TreeSet<Double>(first);
		List<Set<Double>> allShifted = new ArrayList<Set<Double>>();
		allShifted.add(alignmentSet);
		
		Set<Double> allExps = new TreeSet<Double>(first);
		
		/*
		 * calculate the current alignment given the shifts of all data sets to the first data set
		 */
		for(int i = 1; i < dataSets.size(); i++) {
			TimepointDataSet s = dataSets.get(i);
			Set<Double> shifted = new TreeSet<Double>();
			for(Double d : s) {
				shifted.add(d + timeshifts.get(i-1));
			}
			alignmentSet = intersection(alignmentSet, shifted);
			allShifted.add(shifted);
			allExps.addAll(s);
		}
		
		alignment = new ArrayList<Double>(alignmentSet);
		allExperiments = new ArrayList<Double>(allExps);
		
		/*
		 * calculate the matching time points of all data sets to the alignment
		 * store indices of the matching in DII objects
		 */
		for(int i = 0; i < allShifted.size(); i++) {
			ArrayList<Double> shifted = new ArrayList<Double>(allShifted.get(i));
			
			Set<Double> all = new TreeSet<Double>(alignment);
			all.addAll(shifted);
			
			ArrayList<DII> mappingDII = new ArrayList<DII>();
			ArrayList<DII> matchingDII = new ArrayList<DII>();
			
			/*
			 * determine which timepoints of the dataset match to the current alignment
			 */
			for (double timePoint : all) {
				Integer i1 = alignment.indexOf(timePoint);
				Double stmp = 0.0;
				if(i != 0) {
					stmp = timeshifts.get(i-1);
				}
				Integer i2 = dataSets.get(i).indexOf(timePoint - stmp);
				
				if (i1 < 0) i1 = null;
				if (i2 < 0) i2 = null;
				
				DII mapping = new DII(timePoint, i1, i2);
				mappingDII.add(mapping);
				//found a match?
				if (i1!=null && i2!=null){
					matchingDII.add(mapping);
				}
			}
			
			tPMapping.add(mappingDII);
			tPMatching.add(matchingDII);
		}
		
		store.fireAlignmentChanged();
	}
	
	/**
	 * @return all time-shifts as Double values
	 */
	public TimeShifts getTimeShifts() {
		return this.timeshifts;
	}
	
	/**
	 * @return all matched experiments for the multiple alignment
	 */
	public List<List<DII>> getMatchingAll() {
		return Collections.unmodifiableList(tPMatching);
	}
	
	/**
	 * @return all mapped experiments for the multiple alignment
	 */
	public List<List<DII>> getMappingAll() {
		return Collections.unmodifiableList(tPMapping);
	}
	
	/**
	 * @param number
	 * @return the number of common experiments for the pairwise alignment at position 'number'
	 */
	public int getCommonCountAll(int number) {
		return tPMatching.get(number).size();
	}
	
	/**
	 * @param probeNames
	 * @param distance
	 * @return all scores for the provided set of probes
	 */
	public HashMap<String, Double> getScores(Collection<String> probeNames, DistanceMeasurePlugin distance) {
		HashMap<String, Double> ret = new HashMap<String, Double>();
		for (String pbn : probeNames) {
			DoubleVector v1 = getVector(0, pbn);
			DoubleVector v2 = getVector(1, pbn);
			ret.put(pbn, distance.getDistance(v1, v2));
		}
		return ret;
	}
	
	/**
	 * @param number
	 * @param probeNames
	 * @return all scores for the provided set of probes under the scoring function at position 'number'
	 */
	public HashMap<String, Double> getScores(int number, Collection<String> probeNames) {
		DistanceMeasurePlugin sf = store.getScoringFunction(number);
		return getScores(probeNames, sf);
	}
	
	/**
	 * @param which
	 * @param allProbes
	 * @return scores for all probes?
	 */
	public HashMap<String, Double> getScores(int which, Boolean allProbes) {
		if (allProbes==null) {
			return getScores(which);
		} else {
			Collection<String> names;
			if (allProbes) {
				names = store.getAlignedDataSets().getCommonProbeNames();
			} else {
				names = new TreeSet<String>();
				for (Probe pb : store.getVisualizer(0).getViewModel().getProbes())
					names.add(pb.getName());
			}
			return getScores(which, names);
		}
	}
	
	/**
	 * @param which
	 * @return scores for all probes?
	 */
	public HashMap<String, Double> getScores(int which) {
		return getScores(which, store.getSettings().isScoringForAll());
	}
	
	/**
	 * @return number of input data sets
	 */
	public int getNumberOfDataSets() {
		if(dataSets != null) {
			return dataSets.size();
		}
		return 0;
	}
	
	/**
	 * @return list of all experiment time-points
	 */
	public List<Double> getAllExperiments() {
		return this.allExperiments;
	}
	
	/**
	 * @return collection of all probe names
	 */
	public Collection<String> getCommonProbeNames() {
		TreeSet<String> ret = new TreeSet<String>();
		
		if(dataSets != null) {
			ret.addAll(dataSets.get(0).getDataSet().getMasterTable().getProbes().keySet());
			for(int i = 1; i < dataSets.size(); i++) {
				ret.retainAll(dataSets.get(i).getDataSet().getMasterTable().getProbes().keySet());
			}
		}
		return ret;
	}
	
	/**
	 * @param dataSetID
	 * @param name
	 * @return vector of mapped time-points
	 */
	public DoubleVector getVector(int dataSetID, String name) {
		TimepointDataSet tpds = dataSets.get(dataSetID);
		Probe pb = tpds.getDataSet().getMasterTable().getProbe(name);
		if(pb == null) {
			return null;
		}
		DoubleVector ret = new DoubleVector(tPMatching.get(dataSetID).size());
		double[] field = ret.toArrayUnpermuted();
		int i = 0;
		for(DII dii : tPMatching.get(dataSetID)) {
			int index = dii.getIdx2();
			field[i++] = pb.getValue(index);
		}
		return ret;
	}
	
	/**
	 * @return all derived data-sets
	 */
	public Collection<DataSet> deriveDataSetsAll() {
		LinkedList<DataSet> lds = new LinkedList<DataSet>();
		for(int i = 0; i < dataSets.size(); i++) {
			lds.add(deriveDataSetAll(i));
		}
		return lds;
	}
	
	protected DataSet deriveDataSetAll(int datasetID) {
		String modifier = (datasetID == 0) ? " (master)" : " (aligned " + datasetID + ")";
		DataSet sourceDS = dataSets.get(datasetID).getDataSet();
		DataSet targetDS = new DataSet(sourceDS.getName() + modifier);
		MasterTable mata = targetDS.getMasterTable();
		List<DII> diis = tPMatching.get(datasetID);
		mata.setNumberOfExperiments(diis.size());
		LinkedList<Double> timePoints = new LinkedList<Double>();
		for(int i = 0; i != diis.size(); i++) {
			DII currentItem = diis.get(i);
			mata.setExperimentName(i, sourceDS.getMasterTable().getExperimentName(currentItem.getIdx2()));
			timePoints.add(currentItem.getTime());
		}
		//Probes
		for (Probe pb : sourceDS.getMasterTable().getProbes().values()) {
			Probe pb2 = new Probe(mata);
			pb2.setName(pb.getName());
			for (DII currentItem: diis)
				pb2.addExperiment(pb.getValue(currentItem.getIdx2()));
			mata.addProbe(pb2);
		}
		MIGroup mg = TimeseriesMIO.getGroupInstance(targetDS.getMIManager());
		TimeseriesMIO tsm = (TimeseriesMIO)mg.add(targetDS);
		tsm.setValue(timePoints);
		assert tsm.applicableTo(targetDS);
		return targetDS;
	}
	
	/**
	 * @param <T>
	 * @param setA
	 * @param setB
	 * @return the intersection of the two provided sets
	 */
	public static <T> Set<T> intersection(Set<T> setA, Set<T> setB) {
		Set<T> tmp = new TreeSet<T>();
		for (T x : setA)
			if (setB.contains(x))
				tmp.add(x);
		return tmp;
	}
	
	/**
	 * @author jaeger
	 */
	public static class DII {
		
		Double time;
		Integer idx1, idx2;

		/**
		 * @param a
		 * @param b
		 * @param c
		 */
		public DII(Double a, Integer b, Integer c) {
			time = a;
			idx1 = b;
			idx2 = c;
		}

		/**
		 * @return the time-point
		 */
		public Double getTime() {
			return time;
		}

		/**
		 * @return the time-point index in the first data set
		 */
		public Integer getIdx1() {
			return idx1;
		}

		/**
		 * @return the time-point index in the second data set
		 */
		public Integer getIdx2() {
			return idx2;
		}
	}

	/**
	 * @param matches
	 * @return all second indices
	 */
	public static Integer[] secondIndices(List<DII> matches) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (DII dii : matches) {
			Integer i = dii.getIdx2();
				ret.add(i);
		}		
		return ret.toArray(new Integer[0]);
	}
	
	/**
	 * @param matches
	 * @return all first indices
	 */
	public static Integer[] firstIndices(List<DII> matches) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (DII dii : matches) {
			Integer i = dii.getIdx1();
				ret.add(i);
		}		
		return ret.toArray(new Integer[0]);
	}
	
	/**
	 * @param matches
	 * @param position
	 * @return all second of first indices, see getIdx()
	 */
	public static Integer[] indices(List<DII> matches) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for(DII dii : matches) {
			Integer i = dii.getIdx2();
			ret.add(i);
		}
		return ret.toArray(new Integer[0]);
	}
}
