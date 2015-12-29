package mayday.expressionmapping.clustering.weightedkmeans;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import mayday.clustering.ClusterPlugin;
import mayday.clustering.ClusterTask;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.GUIUtilities;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.matrix.PermutableMatrix;

/**
 * @author jaeger
 *
 */
public abstract class WeightedKMeansPluginBase extends ClusterPlugin {

	@Override
	public void init() {}

	/**
	 * @param probeLists
	 * @param masterTable
	 * @param settings
	 * @return List of ProbeLists
	 */
	public List<ProbeList> runWithSettings(List<ProbeList> probeLists, MasterTable masterTable, WeightedKMeansSetting settings) {
		List<ProbeList> Clustering = null;
		
		if(settings != null) {
			Clustering = cluster(probeLists, masterTable, settings);
		}
		
		return Clustering;
	}
	
	/**
	 * @param probeLists
	 * @param masterTable
	 * @param settings
	 * @return list of probe lists
	 */
	public List<ProbeList> cluster(List<ProbeList> probeLists, MasterTable masterTable,
			WeightedKMeansSetting settings) {
		ProbeList uniqueProbeList = ProbeList.createUniqueProbeList(probeLists);
		Object[] uniqueProbes = uniqueProbeList.toCollection().toArray();
		
		int numOfProbes = uniqueProbes.length;
		int numOfExps = masterTable.getNumberOfExperiments();
		
		PermutableMatrix matrix = new DoubleMatrix(numOfProbes,numOfExps);
		fillMatrix(uniqueProbes, numOfProbes, numOfExps, matrix);
		
		WeightedKMeansClustering wkmClustering = new WeightedKMeansClustering(matrix, settings);
		
		ClusterTask cTask = new ClusterTask("Neural Gas Clustering");
		cTask.setClAlg(wkmClustering);
		wkmClustering.setClusterTask(cTask);
		
		cTask.start();
		cTask.waitFor();
		
		int [] ClusterIndices = cTask.getClResult();
		wkmClustering.setClusterTask(null);
		
		if(ClusterIndices == null) {
			return null;
		}
		
		//create result probe lists
		List<ProbeList> Clustering = null;
		
		if(ClusterIndices != null) {
			Clustering = createResultProbeList(masterTable, uniqueProbes,
					ClusterIndices, settings);
		}
		return Clustering;
	}
	
	private List<ProbeList> createResultProbeList(MasterTable masterTable,
			Object[] uniqueProbes, int[] clusterIndices,
			WeightedKMeansSetting settings) {
		Color[] colors = GUIUtilities.rainbow(settings.getNumOfClusters(), 0.75);
		List<ProbeList> Clustering = new ArrayList<ProbeList>();
		
		for(int i = 0; i < settings.getNumOfClusters(); i++) {
			ProbeList tempList = new ProbeList(masterTable.getDataSet(), true);
			tempList.setName("Weighted K-Means" + " " + (settings.getNumOfClusters()-i));
			tempList.setAnnotation(new AnnotationMIO(
					"Weighted K-Means Clustering, maximal number of rounds = "
					+ settings.getMaxNumOfRounds(), ""));
			tempList.setColor(colors[i]);
			Clustering.add(tempList);
		}
				
		for(int i = 0; i < uniqueProbes.length; i++) {
			if(clusterIndices[i] != -1) {
				((ProbeList)Clustering.get(clusterIndices[i])).addProbe((Probe)uniqueProbes[i]);
			} else {
				((ProbeList)Clustering.get(Clustering.size()-1)).addProbe((Probe)uniqueProbes[i]);
			}
		}
		return Clustering;
	}
	
	protected static void fillMatrix(Object[] uniqueProbes, int numOfProbes, int numOfExps, PermutableMatrix matrix) {
		for(int i = 0; i < numOfProbes; i++) {
			for(int j = 0; j < numOfExps; j++) {
				Double probeValue = ((Probe)uniqueProbes[i]).getValue(j);
				
				if(probeValue == null) {
					throw new RuntimeException("Unable to cluster probes " +
							"with missing expression values.");
				} else {
					matrix.setValue(i,j,probeValue.doubleValue());
				}
			}
		}
	}
}
