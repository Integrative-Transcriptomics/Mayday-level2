package mayday.clustering.extras.quality.dunnindex;

import java.util.List;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasurePlugin;

public class DunnIndex {
	
	public double calculateDunnIndex(List<ProbeList> probelists, DunnIndexSetting diSetting){
		DistanceMeasurePlugin distanceMeasure = diSetting.getDistanceMeasure();
		int clusterDiameterMethod = diSetting.getClusterDiameterMethod();
		int linkageMethod = diSetting.getLinkageMethod();
		
		// get maximum diameter of all probe lists
		double maxClusterDiameter = getMaximumClusterDiameter(probelists, distanceMeasure, clusterDiameterMethod);
		
		// minimal normalized inter cluster distance and diameter
		double minNormInterClusterDistance = getMinNormInterClusterDistance(probelists, distanceMeasure, linkageMethod, maxClusterDiameter);
		
		return minNormInterClusterDistance;
	}
	
	private double getMinNormInterClusterDistance(List<ProbeList> probelists, 	DistanceMeasurePlugin dist, int link, double maxDiameter) {
		double min = Double.MAX_VALUE;
		for(int i = 0; i < probelists.size() - 1; i++) {
			ProbeList pl1 = probelists.get(i);
			for(int j = i+1; j < probelists.size(); j++) {
				ProbeList pl2 = probelists.get(j);
				double interClusterDistance = getInterClusterDistance(pl1, pl2, link, dist);
				double ratio = interClusterDistance / maxDiameter;
				if(Double.compare(ratio, min) < 0) {
					min = ratio;
				}
			}
		}
		return min;
	}

	/*
	 * calculates the diameter of each probe list and returns the maximum of all diameters
	 */
	private double getMaximumClusterDiameter(List<ProbeList> probelists, DistanceMeasurePlugin dist, int dm) {
		double maxDiameter = 0d;
		for(ProbeList pl: probelists) {
			double diam = 0d;
			
			switch(dm) {
			case DunnIndexSetting.DIAMETER_MAXIMUM: 
				diam = getDiameterMaximum(pl, dist);
				break;
			case DunnIndexSetting.DIAMETER_MEAN: 
				diam = getDiameterMean(pl, dist);
				break;
			case DunnIndexSetting.DIAMETER_CENTROID: 
				diam = getDiameterCentroid(pl, dist);
				break;
			}
			
			if(Double.compare(diam, maxDiameter) > 0) {
				maxDiameter = diam;
			}
		}

		return maxDiameter;
	}

	private double getInterClusterDistance(ProbeList probeList,
			ProbeList probeList2, int linkage, DistanceMeasurePlugin distance) {
		double dist = 0d;
		switch(linkage){
			case DunnIndexSetting.LINKAGE_SINGLE: 
				dist = singleLinkageDistance(probeList, probeList2, distance);
				break;
			case DunnIndexSetting.LINKAGE_COMPLETE: 
				dist = completeLinkageDistance(probeList, probeList2, distance);
				break;
			case DunnIndexSetting.LINKAGE_AVERAGE: 
				dist = averageLinkageDistance(probeList, probeList2, distance);
				break;
			default:
				dist = averageLinkageDistance(probeList, probeList2, distance);
			}
		return dist;
	}
	
	private double averageLinkageDistance(ProbeList pl1, ProbeList pl2, DistanceMeasurePlugin distance) {
		return distance.getDistance(pl1.getMean(), pl2.getMean());
	}
			
	private double completeLinkageDistance(ProbeList pl1, ProbeList pl2, DistanceMeasurePlugin distance) {
		double maxDist = Double.NEGATIVE_INFINITY;
		for(Probe pb1: pl1.getAllProbes()) {
			for(Probe pb2: pl2.getAllProbes()) {
				double dist = distance.getDistance(pb1, pb2);
				if(Double.compare(dist, maxDist) > 0) {
					maxDist = dist;
				}
			}
		}
		return maxDist;
	}
	
	private double singleLinkageDistance(ProbeList pl1, ProbeList pl2, DistanceMeasurePlugin distance) {
		double minDist = Double.POSITIVE_INFINITY;
		for(Probe pb: pl1.getAllProbes()) {
			for(Probe pb2: pl2.getAllProbes()) {
				double dist = distance.getDistance(pb, pb2);
				if(Double.compare(dist, minDist) < 0) {
					minDist = dist;
				}
			}
		}
		return minDist;
	}
	
	/*
	 * mean distance of all probes to the cluster centroid
	 */
	private double getDiameterCentroid(ProbeList pl, DistanceMeasurePlugin distance) {
		double sum = 0d;
		Probe mean = pl.getMean();
		for(Probe pb: pl.getAllProbes()) {
			double dist = distance.getDistance(pb, mean);
			sum += dist;
		}
		double centroid = sum / pl.getNumberOfProbes();
		return centroid;
	}
	
	/*
	 * mean pairwise distance between all probes in the probelist
	 */
	private double getDiameterMean(ProbeList pl, DistanceMeasurePlugin distance) {
		double sum = 0d;
		int numProbes = pl.getNumberOfProbes();
		for(int i = 0; i < numProbes - 1; i++) {
			Probe p1 = pl.getProbe(i);
			for(int j = i+1; j < numProbes; j++) {
				Probe p2 = pl.getProbe(j);
				double dist = distance.getDistance(p1, p2);
				sum += dist;
			}
		}
		double a = numProbes * (numProbes - 1);
		double mean = sum / a;
		return mean;
	}
	
	/*
	 * maximum distance of two probes in the probelist
	 */
	private double getDiameterMaximum(ProbeList pl, DistanceMeasurePlugin distance) {
		double max = 0d;
		int numProbes = pl.getNumberOfProbes();
		for(int i = 0; i < numProbes - 1; i++) {
			Probe p1 = pl.getProbe(i);
			for(int j = i+1; j < numProbes; j++) {
				Probe p2 = pl.getProbe(j);
				double dist = distance.getDistance(p1, p2);
				if(Double.compare(dist, max) > 0){
					max = dist;
				}
			}
		}
		return max;
	}
}
