package mayday.expressionmapping.model.algorithm.cluster;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.gnu_trove_adapter.TIntObjectIterator;
import mayday.expressionmapping.utils.Array;

/**
 * @author Stephan Gade
 *
 */
public class WeightedKMeans extends KMeansBase implements ClusterAlgorithm {

	/**
	 * @param distance
	 */
	public WeightedKMeans(DistanceMeasurePlugin distance) {
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
				/* Weigth matrix is set, so we use a weighted kmeans
				 */

				/* We have to recompute every single coordinate of our cluster
				 */
				for (int j = 0; j < this.clusterCoordinates[i].length; ++j)  {
					this.clusterCoordinates[i][j] = 0;  /* initialize the sum wih 0  */
					double sumWeights = 0;  /* the sum of the weights, we have to divide with */

					/* Consider every single point, belonging to the cluster.
					 * We use the local variable bound, so we don't have to access
					 * the size() funtion of our list with the point id's every time.
					 */
					for (int k = 0; k < bound; ++k)  {
						int currentPoint = clusterPoints.get(k);
						double pointWeight = Array.max(this.pointCoordinates[currentPoint]);
						this.clusterCoordinates[i][j] += (this.pointCoordinates[currentPoint][j] * pointWeight);
						sumWeights += pointWeight;
					}
					/* divide the cluster coordinate by the sum of weights
					 */
					this.clusterCoordinates[i][j] /= sumWeights;
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
		return "Weighted k-means Clustering";
	}
}