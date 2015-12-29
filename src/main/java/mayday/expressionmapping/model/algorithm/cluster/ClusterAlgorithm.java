
package mayday.expressionmapping.model.algorithm.cluster;

import mayday.core.tasks.AbstractTask;
import mayday.expressionmapping.model.geometry.ClusterPoint;
import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.container.PointList;



/**
 * @author Stephan Gade
 *
 */
public interface ClusterAlgorithm extends Runnable {
    /**
     * @param points
     * @param numberOfCluster
     * @return list of clusters
     */
    public PointList<ClusterPoint> cluster(PointList<DataPoint> points, int numberOfCluster);
    /**
     * @param points
     * @param numberOfCluster
     * @param maxRounds
     * @return list of clusters
     */
    public PointList<ClusterPoint> cluster(PointList<DataPoint> points, int numberOfCluster, int maxRounds);
	
    /**
     * @return title of this algorithm
     */
    public String getTitle();
    
	public void setClusterTask(AbstractTask cTask);
}