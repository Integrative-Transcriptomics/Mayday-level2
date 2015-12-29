package mayday.tiala.pairwise.suggestion;

import java.util.Set;
import java.util.TreeSet;

import mayday.core.structures.maps.MultiTreeMap;
import mayday.core.tasks.AbstractTask;
import mayday.tiala.pairwise.data.AlignedDataSets;
import mayday.tiala.pairwise.data.TimepointDataSet;

public class AlignmentSearchTask extends AbstractTask {

	protected int minimumCommon;
	protected TimepointDataSet one, two;
	protected MultiTreeMap<Integer, Double> alignments;
	
	public AlignmentSearchTask( TimepointDataSet tds1, TimepointDataSet tds2, int minimumCommonExperiments ) {
		super("Aligning DataSets");
		one = tds1;
		two = tds2;
		this.minimumCommon = minimumCommonExperiments;
	}

	public AlignmentSearchTask( TimepointDataSet tds1, TimepointDataSet tds2 ) {
		this(tds1, tds2, estimateMinCommon(tds1,tds2));
	}
	
	public int size() {
		return alignments.size_everything();
	}
	
	public MultiTreeMap<Integer, Double> getAlignments() {
		return alignments;
	}
	
	public Set<Double> getAllShifts() {
		TreeSet<Double> ret = new TreeSet<Double>(alignments.everything());
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
		
		// 1 - create a list of all shiftings with at least $n$ common experiments 				
		Set<Double> all_shiftings = computeShiftings( one, two, minimumCommon );		
		
		// 2 - remove experiments that have no partner with the correct delta
		alignments = computeAlignments(one, two, all_shiftings, minimumCommon);		
		
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
			AlignedDataSets ads = new AlignedDataSets( null, larger, smaller, timeShift);
			int matchingExperiments = ads.getCommonCount();
//			System.out.println(timeShift+" ==> "+matchingExperiments);
			if (matchingExperiments>=minimumCommon) { // some shiftings may lose experiments and become unworthy
				result.put(matchingExperiments, timeShift);
			}
		}
		return result;
	}


	protected void initialize() {
	}
	
}
