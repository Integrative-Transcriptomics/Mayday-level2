package mayday.clustering.extras.quality.potentialfunction;

import java.util.List;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasurePlugin;

/**
 * 
 * @author Jennifer Lange
 *
 */
public class PotentialFunction {

	public double calculatePotentialFunction(MasterTable masta, List<ProbeList> probelists, PotentialFunctionSetting pfSetting) {
		DistanceMeasurePlugin distanceMeasure = pfSetting.getDistanceMeasure();
		int centroidMethod = pfSetting.getCentroidCalculationMethod();
		
		List<Probe> allProbes = ProbeList.mergeProbeLists(probelists, masta);
		double totalDist = 0d;
		
		for(ProbeList pl: probelists) {
			Probe centroid = null;
			
			switch(centroidMethod) {
				case PotentialFunctionSetting.MEAN: 
					centroid = pl.getMean();
					break;
				case PotentialFunctionSetting.MEDIAN: 
					centroid = pl.getMedian();
					break;
				default:
					centroid = pl.getMean();
			}
			
			// calculate distance of a probe to the probe lists centroid
			for(Probe pb: pl.getAllProbes()) {
				double distance = distanceMeasure.getDistance(pb, centroid);
				totalDist += distance;
			}
		}
		// total potential
		double potential = totalDist / allProbes.size();
		
		return potential;
	}
}
