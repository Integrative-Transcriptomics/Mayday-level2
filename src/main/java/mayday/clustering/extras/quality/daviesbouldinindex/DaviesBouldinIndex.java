package mayday.clustering.extras.quality.daviesbouldinindex;

import java.util.ArrayList;
import java.util.List;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.DoubleVector;

/**
 * @author Jennifer Lange
 *
 */
public class DaviesBouldinIndex {
	
	public double calculateDBI(List<ProbeList> probelists, DaviesBouldinIndexSetting dbiSetting) {
		final DistanceMeasurePlugin distanceMeasure = dbiSetting.getDistanceMeasure();
		final int centroidMethod = dbiSetting.getCentroidCalculationMethod();
		
		List<Probe> centers = getCenters(probelists, centroidMethod);
		
		//compute intra cluster distances
		DoubleVector deltaCx = getIntraClusterDistances(probelists, distanceMeasure, centers);

		//compute inter cluster distances
		PermutableMatrix deltaCiCj = getInterClusterDistances(distanceMeasure, centers);
		
		//compute dbi
		double dbi = getDBI(probelists, deltaCx, deltaCiCj);
		
		return dbi;
	}
	/*
	 * calculates the davies bouldin index:
	 * dbi(clustering) = 1/K * sum_{i=1}^{K}max_{i!=j}{(Delta(Ci)+Delta(Cj)) / delta(Ci,Cj)}
	 */
	private double getDBI(final List<ProbeList> probelists, DoubleVector deltaCx, PermutableMatrix deltaCiCj) {
		int numProbeLists = probelists.size();
		double[] dbis = new double[numProbeLists];
		//find maximas for all pairs of clusters
		for(int i = 0; i < numProbeLists - 1; i++) {
			double max = Double.MIN_VALUE;
			for(int j = i+1; j < numProbeLists; j++) {
				double cicj = deltaCx.get(i) + deltaCx.get(j);
				double dbi_ij = cicj / deltaCiCj.getValue(i, j);
				
				if(dbi_ij > max) {
					max = dbi_ij;
				}
			}
			dbis[i]= max;
		}
		
		//sum up the different maxima and normalize by the number of clusters
		double sum = 0d;
		for(int i = 0; i < dbis.length; i++) {
			sum += dbis[i];
		}
		
		double dbi = sum / numProbeLists;
		return dbi;
	}

	/*
	 * Compute the centroid linkage inter cluster distances for all pairs of clusters
	 */
	private PermutableMatrix getInterClusterDistances(DistanceMeasurePlugin dist, List<Probe> centers) {
		PermutableMatrix clusterDistances = new DoubleMatrix(centers.size(), centers.size(), true);
		for(int i = 0; i < centers.size() - 1; i++) {
			for(int j = i+1; j < centers.size(); j++) {
				double deltaCiCj = dist.getDistance(centers.get(i), centers.get(j));
				clusterDistances.setValue(i, j, deltaCiCj);
			}
		}
		return clusterDistances;
	}
	
	/*
	 * compute the intra cluster distances
	 */
	private DoubleVector getIntraClusterDistances(	List<ProbeList> probelists,	DistanceMeasurePlugin dist, List<Probe> centers) {
		double[] deltaCi = new double[probelists.size()];
		
		for(int i = 0; i < probelists.size(); i++ ) {
			double sum = 0d;
			ProbeList pl = probelists.get(i);
			for(Probe pb: pl.getAllProbes()) {
				double d = dist.getDistance(pb, centers.get(i));
				sum += d;
			}
			deltaCi[i] = 2. * (sum / pl.getNumberOfProbes());
		}
		
		return new DoubleVector(deltaCi);
	}
	
	/*
	 * compute centroids for each cluster (clusters = probelists)
	 */
	private List<Probe> getCenters(List<ProbeList> probelists, int centroidMethod){
		List<Probe> centers = new ArrayList<Probe>();
		for(int i = 0; i < probelists.size(); i++ ){
			ProbeList pl = probelists.get(i);
			Probe centroid;
			
			switch(centroidMethod){
				case DaviesBouldinIndexSetting.MEAN :
					centroid = pl.getMean();
					break;
				case DaviesBouldinIndexSetting.MEDIAN : 
					centroid = pl.getMedian();
					break;
				default: 
					centroid = pl.getMean();
			}
			
			centers.add(centroid);
		}
		return centers;
	}
}
