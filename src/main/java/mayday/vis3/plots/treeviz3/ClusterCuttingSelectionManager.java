package mayday.vis3.plots.treeviz3;

import java.util.List;

import mayday.core.meta.types.AnnotationMIO;
import mayday.core.structures.trees.tree.Edge;
import mayday.vis3.model.ViewModel;

/**
 * @author Eugen Netz
 */
public interface ClusterCuttingSelectionManager<T> {

	/**
	 * Selects the Leaves of each cut edge and creates either DataSets or ProbeLists
	 * @param edgeList
	 * @param prefix The Prefix for each cluster set in the Settings
	 * @param hierarchicalClusteringName The Name of the hierarchical clustering, that is cut
	 * @param annotation The Annotation of the ProbeList from the hierarchical Clustering
	 * @return An ArrayList with the clusters (DataSets or ProbeLists) as Objects
	 */
	public void selectNodes(List<Edge> edgeList, String prefix, String hierarchicalClusteringName, AnnotationMIO annotation);
	
	/**
	 * @return The ViewModel used by the hierarchical clustering
	 */
	public ViewModel getViewModel();
	
	/**
	 * @return A List of the clusters
	 */
	public List<T> getClusters();
	
	/**
	 * Accepts a temporary clustering and makes it permanent
	 * @param clusters
	 * @param name The name of the whole Clustering (relevant to ProbeLists, not to DataSets)
	 */
	public void acceptClustering(String name);
	
}
