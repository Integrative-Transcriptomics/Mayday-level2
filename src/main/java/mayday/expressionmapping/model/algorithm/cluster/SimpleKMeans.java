package mayday.expressionmapping.model.algorithm.cluster;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.gnu_trove_adapter.TIntObjectIterator;
/**
 * @author Stephan Gade
 *
 */
public class SimpleKMeans extends KMeansBase implements ClusterAlgorithm {	

	/**
	 * @param distance
	 */
	public SimpleKMeans(DistanceMeasurePlugin distance) {
		super(distance);
	}
	
	@Override
	protected void computeNewClusterCenter()  {
		/* Calculate the new cluster centrums.
		 * If a cluster is empty, it will be deleted
		 */
		for(TIntObjectIterator<TIntArrayList> clusterIter = this.clusterToPoint.iterator();
		clusterIter.hasNext();)  {
			clusterIter.advance();

			/* remove empty clusters
			 */
			if (clusterIter.value().isEmpty())
				clusterIter.remove();
			else {
				int i = clusterIter.key();

				TIntArrayList clusterPoints = clusterIter.value();

				int bound = clusterPoints.size();

				/* Weight matrix is not set, so we use the normal kmeans procedure
				 * and compute the average from all point coordinates to minimize
				 * the variance.
				 */
				for (int j = 0; j < this.clusterCoordinates[i].length; ++j)  {
					this.clusterCoordinates[i][j] = 0;  /* initialize the sum wih 0  */

					/* Consider every single point, belonging to the cluster.
					 * We use the local variable bound, so we don't have to access
					 * the size() funtion of our list with the point id's every time.
					 */
					for (int k = 0; k < bound; ++k) 
						this.clusterCoordinates[i][j] += this.pointCoordinates[clusterPoints.get(k)][j];

					/* divide the cluster coordinate by the number of points belonging to it,
					 * this number is the length of clusterPoints, stored in bound
					 */
					this.clusterCoordinates[i][j] /= bound;
				}
			}
		}
	}

	@Override
	protected void initialize() {
		super.initialize();
	}

	@Override
	public String getTitle() {
		return "Simple k-means Clustering";
	}
}