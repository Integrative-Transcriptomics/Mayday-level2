package mayday.clustering.extras.quality.separation;

import java.util.List;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasurePlugin;

/**
 * 
 * @author jaeger
 *
 */
public class Separation {
	
	public double calculateSeparation(List<ProbeList> probelists, SeparationSetting sSetting){
		DistanceMeasurePlugin distanceMeasure = sSetting.getDistanceMeasure();
		int linkageMethod = sSetting.getLinkageMethod();
		int centroidMethod = sSetting.getCentroidMethod();
		
		double distXiXj = 0d;
		double sum = 0d;
		for(int i = 0; i < probelists.size() - 1; i++) {
			for(int j = i+1; j < probelists.size(); j++) {
				
				switch(linkageMethod) {
				case SeparationSetting.SINGLE: 
					distXiXj = singleLinkage(distanceMeasure, probelists.get(i), probelists.get(j)); 
					break;
				case SeparationSetting.COMPLETE: 
					distXiXj = completeLinkage(distanceMeasure, probelists.get(i), probelists.get(j)); 
					break;
				case SeparationSetting.AVERAGE: 
					distXiXj = averageLinkage(distanceMeasure, probelists.get(i), probelists.get(j), centroidMethod); 
					break;
				}
				
				sum += distXiXj;
			}
		}
		
		double numProbelistPairs = (probelists.size() * (probelists.size() - 1)); 
		double separation = sum / numProbelistPairs;
		
		return separation;
	}
	
	/*
	 * centroid linkage
	 */
	protected double averageLinkage(DistanceMeasurePlugin distanceMeasure, ProbeList probeList1, ProbeList probeList2, int centroidMethod) {
		Probe centroid1 = getCentroid(probeList1, centroidMethod);
		Probe centroid2 = getCentroid(probeList2, centroidMethod);
		double distance = distanceMeasure.getDistance(centroid1, centroid2);
		return distance;
	}

	/*
	 * complete linkage
	 */
	protected double completeLinkage(DistanceMeasurePlugin distanceMeasure, ProbeList probeList, ProbeList probeList2) {
		double maxDist = Double.NEGATIVE_INFINITY;
		for(Probe pb1: probeList.getAllProbes()) {
			for(Probe pb2: probeList2.getAllProbes()) {
				double distance = distanceMeasure.getDistance(pb1, pb2);
				if(Double.compare(distance, maxDist) > 0) {
					maxDist = distance;
				}
			}
		}
		return maxDist;
	}
	/*
	 * single linkage
	 */
	protected double singleLinkage(DistanceMeasurePlugin distanceMeasure,	ProbeList probeList, ProbeList probeList2) {
		double minDist = Double.POSITIVE_INFINITY;
		for(Probe pb1: probeList.getAllProbes()) {
			for(Probe pb2: probeList2.getAllProbes()) {
				double distance = distanceMeasure.getDistance(pb1, pb2);
				if(Double.compare(distance, minDist) < 0) {
					minDist = distance;
				}
			}
		}
		return minDist;
	}
	
	/*
	 * return the probe list centroid according to the chosen method
	 */
	private Probe getCentroid(ProbeList pl, int centroidMethod) {
		Probe centroid = null;
		
		switch(centroidMethod) {
		case SeparationSetting.CENTROID_MEAN:
			centroid = pl.getMean();
			break;
		case SeparationSetting.CENTROID_MEDIAN:
			centroid = pl.getMedian();
			break;
		default:
			centroid = pl.getMean();
		}

		return centroid;
	}
}
