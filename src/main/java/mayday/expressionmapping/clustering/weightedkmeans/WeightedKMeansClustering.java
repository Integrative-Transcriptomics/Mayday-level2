package mayday.expressionmapping.clustering.weightedkmeans;

import java.util.ArrayList;
import java.util.List;

import mayday.clustering.ClusterTask;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.expressionmapping.clustering.EMClusterAlgorithm;
import mayday.expressionmapping.clustering.PartitioningClusteringSetting;
import mayday.expressionmapping.gnu_trove_adapter.TDoubleArrayList;
import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.gnu_trove_adapter.TIntIntHashMap;
import mayday.expressionmapping.gnu_trove_adapter.TIntIntIterator;
import mayday.expressionmapping.gnu_trove_adapter.TIntObjectHashMap;
import mayday.expressionmapping.gnu_trove_adapter.TIntObjectIterator;
import mayday.expressionmapping.utils.Array;

/**
 * @author jaeger
 * 
 */
public class WeightedKMeansClustering extends EMClusterAlgorithm {
	private ClusterTask cTask;
	private DistanceMeasurePlugin distanceMeasure;

	protected TIntObjectHashMap<TIntArrayList> clusterToPoint;
	protected TIntIntHashMap pointToCluster;

	/**
	 * @param Data
	 * @param settings
	 */
	public WeightedKMeansClustering(PermutableMatrix Data, PartitioningClusteringSetting settings) {
		super(Data, settings);
	}

	/**
	 * @param cTask
	 */
	public void setClusterTask(ClusterTask cTask) {
		this.cTask = cTask;
	}

	public void initialize() {
		dimension = settings.getDimension();
		distanceMeasure = settings.getDistanceMeasure();
		maxRounds = settings.getMaxNumOfRounds();
		numberOfClusters = settings.getNumOfClusters();
		numberOfPoints = ClusterData.nrow();
		globalError = new TDoubleArrayList(maxRounds);

		super.initialize();
		/*
		 * Initialize the cluster id for every point with -1, so we can assure
		 * that this id will be modified during the first training run
		 */
		this.pointToCluster = new TIntIntHashMap(numberOfPoints);

		for (int i = 0; i < numberOfPoints; ++i) {
			this.pointToCluster.put(i, -1);
		}
		// Initialize the point id's list for every cluster with an empty list
		this.clusterToPoint = new TIntObjectHashMap<TIntArrayList>(
				numberOfClusters);

		for (int i = 0; i < numberOfClusters; ++i) {
			this.clusterToPoint.put(i, new TIntArrayList());
		}

		currentRound = 0;
	}

	@Override
	public int[] cluster() {
		initialize();
		return run();
	}

	/**
	 * start clustering
	 * 
	 * @return mapping of probes indices to cluster indices
	 */
	public int[] run() {
		// starting the algorithm
		this.state = RUNNING;
		// entering the first round
		this.currentRound = 0;

		// MAIN LOOP
		while (this.state == RUNNING) {
			findCorrespondingCluster();
			computeNewClusterCenter();
			update(); /* here: incrementing the round */
			/*
			 * check the state of our algorithmus after each step, if it is time
			 * for termination
			 */
			boolean canceled = checkState();
			if (canceled) {
				return null;
			}
		}

		this.cTask.reportCurrentFractionalProgressStatus(1.0);

		return this.getClusters();
	}

	private int[] getClusters() {
		List<List<Integer>> clusterStructure = new ArrayList<List<Integer>>();
		// main loop: iterate over all cluster centers and collect their members
		for (TIntObjectIterator<TIntArrayList> it = this.clusterToPoint
				.iterator(); it.hasNext();) {
			it.advance();

			TIntArrayList probeIDs = it.value();
			List<Integer> currentCluster = new ArrayList<Integer>();

			for (int i = 0; i < probeIDs.size(); i++) {
				// assign the ID of the point to the cluster
				currentCluster.add(probeIDs.get(i));
			}

			if (currentCluster.size() > 0) {
				clusterStructure.add(currentCluster);
			}
		}
		// create a mapping of every probe to its cluster
		int[] result = new int[numberOfPoints];
		for (int i = 0; i < clusterStructure.size(); i++) {
			ArrayList<Integer> currentCluster = (ArrayList<Integer>) clusterStructure
					.get(i);

			for (int j = 0; j < currentCluster.size(); j++) {
				Integer probe = currentCluster.get(j);
				result[probe] = clusterStructure.size() - i - 1;
			}
		}

		return result;
	}

	private void findCorrespondingCluster() {
		// Initialize the id list (list of point id's) for every cluster with an
		// empty list
		for (TIntObjectIterator<TIntArrayList> clusterIter = this.clusterToPoint
				.iterator(); clusterIter.hasNext();) {
			clusterIter.advance();
			// here we just clear the list instead of replacing the old with a
			// new one
			clusterIter.value().clear();
		}
		double roundError = 0;
		/*
		 * at first determine the corresponding cluster for each point (the
		 * cluster with the minimum distance)
		 */
		for (TIntIntIterator pointIter = this.pointToCluster.iterator(); pointIter
				.hasNext();) {
			pointIter.advance();
			/*
			 * save the current key from the map point -> cluster in the
			 * temporary counter i, so we don't have to access the iterator
			 * twice
			 */
			int i = pointIter.key();
			// the index of the cluster with minimal distance to the current
			// point
			int minIndex = 0;

			double minDistance = distanceMeasure
					.getDistance(this.pointCoordinates[i],
							this.clusterCoordinates[minIndex]);
			double currentDistance;

			/*
			 * Try each cluster. For the access to the cluster we use an
			 * iterator over the map with the cluster -> point mapping. If a
			 * cluster gets empty, we delete his id from this map, this cluster
			 * is not considered any more. Because of that we cannot iterate
			 * simply over the array with cluster coordinates, since the "dead"
			 * cluster are not deleted from here (it would be to expensive)
			 */
			for (TIntObjectIterator<TIntArrayList> clusterIter = this.clusterToPoint
					.iterator(); clusterIter.hasNext();) {
				clusterIter.advance();
				/*
				 * we save the key in the count variable j , so we don't have to
				 * access it twice. With the key we gain the coordinates of the
				 * cluster from the clusterCoordinates array
				 */
				int j = clusterIter.key();

				currentDistance = this.distanceMeasure.getDistance(
						this.pointCoordinates[i], this.clusterCoordinates[j]);

				if (currentDistance < minDistance) {
					minDistance = currentDistance;
					minIndex = j;
				}
			}
			roundError += minDistance;
			/*
			 * write down the new assignments to both maps: point -> cluster and
			 * cluster -> point
			 */
			if (pointIter.value() != minIndex) {
				// update the new cluster id
				pointIter.setValue(minIndex);
			}
			clusterToPoint.get(minIndex).add(i);
		}
		this.globalError.add(roundError);
	}

	private void computeNewClusterCenter() {
		/*
		 * Calculate the new cluster centrums. If a cluster is empty, it will be
		 * deleted
		 */
		for (TIntObjectIterator<TIntArrayList> clusterIter = this.clusterToPoint
				.iterator(); clusterIter.hasNext();) {
			clusterIter.advance();
			// remove empty clusters
			if (clusterIter.value().isEmpty()) {
				clusterIter.remove();
			} else {
				int i = clusterIter.key();
				TIntArrayList clusterPoints = clusterIter.value();
				int bound = clusterPoints.size();
				// We have to recompute every single coordinate of our cluster
				for (int j = 0; j < this.clusterCoordinates[i].length; ++j) {
					this.clusterCoordinates[i][j] = 0;
					/*
					 * the sum of weights, we have to devide with it is
					 * initialized with 0
					 */
					double sumWeights = 0;
					/*
					 * Consider every single point, belonging to the cluster. We
					 * use the local variable bound, so we don't have to access
					 * the size() function of our list with the point id's every
					 * time.
					 */
					for (int k = 0; k < bound; ++k) {
						int currentPoint = clusterPoints.get(k);
						double pointWeight = Array
								.max(this.pointCoordinates[currentPoint]);
						this.clusterCoordinates[i][j] += (this.pointCoordinates[currentPoint][j] * pointWeight);
						sumWeights += pointWeight;
					}
					// divide the cluster coordinate by the sum of weights
					this.clusterCoordinates[i][j] /= sumWeights;
				}
			}
		}
	}

	protected void update() {
		++this.currentRound;
	}

	private boolean checkState() {
		if (this.currentRound >= this.maxRounds) {
			this.state = FINISHED;
			if (this.cTask != null) {
				if (this.cTask.hasBeenCancelled()) {
					return doCancel();
				}
				this.cTask.reportCurrentFractionalProgressStatus(1.0);
				this.cTask.writeLog("The maximal number of rounds has been reached.\n");
				this.cTask.writeLog("Weighted K-Means Clustering finished!");
			}
		} else {
			if (this.cTask != null) {
				if (this.cTask.hasBeenCancelled()) {
					return doCancel();
				}
				this.cTask
						.reportCurrentFractionalProgressStatus((double) currentRound
								/ (double) maxRounds);
			}
		}

		if (this.currentRound > 1) {
			if (Math.abs(this.globalError.get(this.currentRound - 1)
					- this.globalError.get(this.currentRound - 2)) < Double.MIN_VALUE) {
				this.state = FINISHED;

				if (this.cTask != null) {
					if (this.cTask.hasBeenCancelled()) {
						return doCancel();
					}
					this.cTask.reportCurrentFractionalProgressStatus(1.0);
					this.cTask.writeLog("The minimal error has been reached.\n");
					this.cTask.writeLog("Weighted K-Means Clustering finished!");
				}
			}
		}

		return false;
	}

	private boolean doCancel() {
		this.cTask.writeLog("Weighted K-Means Clustering has been canceled!");
		this.cTask.processingCancelRequest();
		return true;
	}
}
