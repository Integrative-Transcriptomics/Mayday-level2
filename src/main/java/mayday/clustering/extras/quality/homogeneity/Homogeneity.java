package mayday.clustering.extras.quality.homogeneity;

import java.util.List;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.vector.DoubleVector;

/**
 * 
 * @author Jennifer Lange
 *
 */
public class Homogeneity {
	
	public double calculateHomogeneity(List<ProbeList> probelists, HomogeneitySetting hSetting){
		int numProbeLists = probelists.size();
		DoubleVector homogeneityValues = new DoubleVector(numProbeLists);
		DistanceMeasurePlugin distanceMeasure = hSetting.getDistanceMeasure();
		int method = hSetting.getHomogeneityMethod();
		int centroidMethod = hSetting.getCentroidMethod();
		
		for(int i = 0; i < numProbeLists; i++) {
			ProbeList pl = probelists.get(i);
			
			switch(method) {
			case HomogeneitySetting.HOMOGENEITY_MEAN: 
				homogeneityValues.set(i, getHomogeneityMean(pl, distanceMeasure, centroidMethod));
				break;
			case HomogeneitySetting.HOMOGENEITY_MAX: 
				homogeneityValues.set(i, getHomogeneityMax(pl, distanceMeasure, centroidMethod));
				break;
			}
		}
		
		switch(method) {
		case HomogeneitySetting.HOMOGENEITY_MAX:
			return homogeneityValues.max();
		case HomogeneitySetting.HOMOGENEITY_MEAN:
			return homogeneityValues.mean();
		default:
			return homogeneityValues.mean();
		}
	}

	private double getHomogeneityMax(	ProbeList probelist, DistanceMeasurePlugin distanceMeasure, int centroidMethod) {
		double maxHomogeneity = Double.MIN_VALUE;
		Probe centroid = getCentroid(probelist, centroidMethod);
		
		for(Probe pb: probelist.getAllProbes()) {
			double distance = distanceMeasure.getDistance(pb, centroid);
			if(Double.compare(distance, maxHomogeneity) > 0) {
				maxHomogeneity = distance;
			}
		}
		
		return maxHomogeneity;
	}

	private double  getHomogeneityMean(ProbeList probelist, DistanceMeasurePlugin distanceMeasure, int centroidMethod) {
		double sum = 0d;
		Probe centroid = getCentroid(probelist, centroidMethod);
		
		for(Probe pb: probelist.getAllProbes()) {
			double distance = distanceMeasure.getDistance(pb, centroid);
			sum += distance;
		}
		
		double homogeneity = sum / probelist.getNumberOfProbes();
		return homogeneity;
	}
	
	private Probe getCentroid(ProbeList pl, int centroidMethod) {
		Probe centroid;
		
		switch(centroidMethod) {
		case HomogeneitySetting.CENTROID_MEAN:
			centroid = pl.getMean();
			break;
		case HomogeneitySetting.CENTROID_MEDIAN:
			centroid = pl.getMedian();
			break;
		default:
			centroid = pl.getMean();
		}

		return centroid;
	}
}
