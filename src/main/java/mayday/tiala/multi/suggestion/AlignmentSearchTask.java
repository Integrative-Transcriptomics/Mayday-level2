package mayday.tiala.multi.suggestion;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.structures.maps.MultiTreeMap;
import mayday.core.tasks.AbstractTask;
import mayday.tiala.multi.data.TimepointDataSet;
import mayday.tiala.multi.data.container.TimepointDataSets;

/**
 * @author jaeger
 *
 */
public class AlignmentSearchTask extends AbstractTask {

	protected Integer minimumCommon;
	protected TimepointDataSets tdataSets;
	protected List<MultiTreeMap<Integer, Double>> alignments;
	
	/**
	 * @param tdataSets
	 * @param minimumCommonExperiments
	 */
	public AlignmentSearchTask(TimepointDataSets tdataSets, Integer minimumCommonExperiments) {
		super("Aligning DataSets");
		this.tdataSets = tdataSets;
		this.minimumCommon = minimumCommonExperiments;
	}
	
	/**
	 * @param tdataSets
	 */
	public AlignmentSearchTask(TimepointDataSets tdataSets) {
		this(tdataSets, null);
	}
	
	/**
	 * @param index
	 * @return size of the pairwise alignment with index = index
	 */
	public int size(int index) {
		return alignments.get(index).size_everything();
	}
	
	/**
	 * @param index
	 * @return the pairwise alignment at index = index
	 */
	public MultiTreeMap<Integer, Double> getAlignments(int index) {
		return alignments.get(index);
	}
	
	/**
	 * @param index
	 * @return all possible shifts between the reference data set and the data set with index = index
	 */
	public Set<Double> getAllShifts(int index) {
		TreeSet<Double> ret = new TreeSet<Double>(alignments.get(index).everything());
		return ret;
	}
	
	protected static int estimateMinCommon(TimepointDataSet tds1, TimepointDataSet tds2) {
		int min=Math.min(
				tds1.getDataSet().getMasterTable().getNumberOfExperiments(),
				tds2.getDataSet().getMasterTable().getNumberOfExperiments()
				);
		return (int)(((double)min)*.25);
	}
		
	protected void doWork() throws Exception {
		for(int i = 1; i < this.tdataSets.size(); i++) {
			int minCommon;
			if(this.minimumCommon == null) {
				minCommon = estimateMinCommon(tdataSets.get(0), tdataSets.get(i));
			} else {
				minCommon = this.minimumCommon.intValue();
			}
			// 1 - create a list of all shiftings with at least $n$ common experiments
			Set<Double> shiftings = computeShiftings(tdataSets.get(i), tdataSets.get(i), minCommon);
			// 2 - remove experiments that have no partner with the correct delta
			MultiTreeMap<Integer, Double> alignments = computeAlignments(tdataSets.get(0), tdataSets.get(i), shiftings, minCommon);
			this.alignments.add(alignments);
		}
	}
	
	protected static Set<Double> computeShiftings( TimepointDataSet smaller, TimepointDataSet larger, int minimumCommon ) {
		TreeSet<Double> result = new TreeSet<Double>();
		// shift the smaller exp along the longer one
		int expS = smaller.size();
		int expL = larger.size();
		
		for (int startExperiment = 0; startExperiment<expS-minimumCommon; ++startExperiment) {
			// how far can we go to the right: until smaller[startExperiment] is aligned with larger[endpoint]
			int endPoint = expL - minimumCommon;
			double timeDeltaS = smaller.get(startExperiment)-smaller.get(0); 
			for (int shift = 0; shift<endPoint; ++shift) {
				// try to align smaller[startExperiment] with larger[shift]
				// and compute the real shift in units of time.
				double timeDeltaL = larger.get(shift)-larger.get(0); // the time between larger[0] and larger[shift];
				double realshift = timeDeltaL - timeDeltaS;
				result.add(realshift);
			}
		}
		return result;
	}
	
	protected static MultiTreeMap<Integer, Double> computeAlignments( TimepointDataSet larger, TimepointDataSet smaller, Set<Double> all_shiftings, int minimumCommon) {
		MultiTreeMap<Integer, Double> result = new MultiTreeMap<Integer, Double>();
		for (double timeShift : all_shiftings) {
			int matchingExperiments = getMatchingExperiments(larger, smaller, timeShift);
			//System.out.println(timeShift+" ==> "+matchingExperiments);
			if (matchingExperiments >= minimumCommon) { // some shiftings may lose experiments and become unworthy
				result.put(matchingExperiments, timeShift);
			}
		}
		return result;
	}
	
	protected static int getMatchingExperiments(TimepointDataSet one, TimepointDataSet two, double timeShift) {
		int commonCount = 0;
		// find out which experiments map onto each other via times
		LinkedList<Double> shifted1 = new LinkedList<Double>(one);
		LinkedList<Double> shifted2 = new LinkedList<Double>();
		for (Double d : two) {
			shifted2.add(d+timeShift);
		}

		TreeSet<Double> all = new TreeSet<Double>(shifted1);
		all.addAll(shifted2);
		
		for (double timePoint : all) {
			Integer i1 = one.indexOf(timePoint);
			Integer i2 = two.indexOf(timePoint - timeShift);
			if (i1<0) i1 = null;
			if (i2<0) i2 = null;
			if (i1!=null && i2!=null) {
				commonCount++;
			}
		}	
		return commonCount;
	}

	protected void initialize() {
		this.alignments = new ArrayList<MultiTreeMap<Integer, Double>>();
	}
}
