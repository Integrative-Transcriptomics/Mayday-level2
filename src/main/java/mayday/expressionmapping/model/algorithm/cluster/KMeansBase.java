package mayday.expressionmapping.model.algorithm.cluster;


import java.util.List;
import java.util.Vector;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.gnu_trove_adapter.TIntIntHashMap;
import mayday.expressionmapping.gnu_trove_adapter.TIntIntIterator;
import mayday.expressionmapping.gnu_trove_adapter.TIntObjectHashMap;
import mayday.expressionmapping.gnu_trove_adapter.TIntObjectIterator;
import mayday.expressionmapping.model.geometry.ClusterPoint;
import mayday.expressionmapping.model.geometry.ClusterPoint2D;
import mayday.expressionmapping.model.geometry.ClusterPoint3D;
import mayday.expressionmapping.model.geometry.ClusterPoint4D;
import mayday.expressionmapping.model.geometry.container.PointList;
import mayday.expressionmapping.utils.Array;


/**
 * @author jaeger
 *
 */
public abstract class KMeansBase extends SelfOrganizeBatchClustering {
	/* the mapping cluster -> data points, for every cluster we define a list of indices
	 * of data points belonging to this cluster
	 */
	protected TIntObjectHashMap<TIntArrayList> clusterToPoint;

	protected TIntIntHashMap pointToCluster;

	/**
	 * @param distance
	 */
	public KMeansBase(DistanceMeasurePlugin distance)  {
		super(distance);
	}
	
	@Override
	protected void initialize()  {
		super.initialize();

		/* Initialize the cluster id for every point with -1, so we can assure
		 * that this id will be modified during the first training run
		 */
		this.pointToCluster = new TIntIntHashMap(this.numberOfPoints);

		for (int i = 0; i < this.numberOfPoints; ++i)
			this.pointToCluster.put(i, -1);

		/* Initialize the point id's list for every cluster with an empty list
		 */
		this.clusterToPoint = new TIntObjectHashMap<TIntArrayList>(this.numberOfCluster);

		for (int i = 0; i < this.numberOfCluster; ++i)
			this.clusterToPoint.put(i, new TIntArrayList());

//		this.changedClustering = true;
	}


	@Override
	protected PointList<ClusterPoint> getCluster()  {

		int remainingCluster = this.clusterToPoint.size();

		List<ClusterPoint> clusters = new Vector<ClusterPoint>(remainingCluster);

		this.centerOfMass = new double[this.dim];
		
		/* the switch statement stands before the main loop, so we asure to put only
		 * one type (2D, 3D or 4D) of ClusterPoint in the return list.
		 * Additional the dimension has not been checked in every run of the main loop.
		 */
		switch (this.dim)  {

		case 2:   {

			for (TIntObjectIterator<TIntArrayList> clusterIter = this.clusterToPoint.iterator(); clusterIter.hasNext();)  {

				clusterIter.advance();

				int clusterID = clusterIter.key();

				TIntArrayList memberPoints = clusterIter.value();
				int bounds = memberPoints.size();

				/* create new ClusterPoint2D with given ID
				 */
				ClusterPoint2D currentCluster = new ClusterPoint2D(clusterID);

				/* set the Coordinates of the cluster, and ID's of the points
				 * associated with this cluster
				 */
				currentCluster.setCoordinates(this.clusterCoordinates[clusterID]);

				Array.add(this.centerOfMass, this.clusterCoordinates[clusterID]);
				
				for (int i = 0; i < bounds; ++i)  {

					/* assign the index of the point to the cluster
					 */
					//currentCluster.addMembertoCluster(memberPoints.get(i));
					currentCluster.addMembertoCluster(this.points.get(memberPoints.get(i)).getID());
					//System.out.println(clusterID+"  "+i+"   "+this.points.get(i).getID());

				}
				clusters.add(currentCluster);

			}
			
			Array.divide(this.centerOfMass, clusters.size());

			break;

		}

		case 3:   {

			for (TIntObjectIterator<TIntArrayList> clusterIter = this.clusterToPoint.iterator(); clusterIter.hasNext();)  {

				clusterIter.advance();

				int clusterID = clusterIter.key();

				TIntArrayList memberPoints = clusterIter.value();
				int bounds = memberPoints.size();

				/* create new ClusterPoint3D with given ID
				 */
				ClusterPoint3D currentCluster = new ClusterPoint3D(clusterID);

				/* set the Coordinates of the cluster, and ID's of the points
				 * associated with this cluster
				 */
				currentCluster.setCoordinates(this.clusterCoordinates[clusterID]);

				for (int i = 0; i < bounds; ++i)  {


					/* assign the index of the point to the cluster
					 */
					currentCluster.addMembertoCluster(memberPoints.get(i));
					//currentCluster.addMembertoCluster(this.points.get(memberPoints.get(i)).getID());
					//System.out.println(clusterID+"  "+memberPoints.get(i)+"   "+this.points.get(memberPoints.get(i)).getID());

				}

				clusters.add(currentCluster);

			}

			break;

		}

		case 4:   {

			for (TIntObjectIterator<TIntArrayList> clusterIter = this.clusterToPoint.iterator(); clusterIter.hasNext();)  {

				clusterIter.advance();

				int clusterID = clusterIter.key();

				TIntArrayList memberPoints = clusterIter.value();
				int bounds = memberPoints.size();

				/* create new ClusterPoint4D with given ID
				 */
				ClusterPoint4D currentCluster = new ClusterPoint4D(clusterID);

				/* set the Coordinates of the cluster, and ID's of the points
				 * associated with this cluster
				 */
				currentCluster.setCoordinates(this.clusterCoordinates[clusterID]);

				for (int i = 0; i < bounds; ++i)  {

					/* assign the index of the point to the cluster
					 */
					currentCluster.addMembertoCluster(memberPoints.get(i));


				}

				clusters.add(currentCluster);

			}

			break;


		}

		}

		/*
		 * creating the PointList with the cluster points
		 */
		PointList<ClusterPoint> ret = new PointList<ClusterPoint>(clusters, this.points.getGroupLabels()); 
		
		/*
		 * setting the center of mass
		 */
		setCenterOfMass(ret);
		
		System.out.println("Returning "+ret.size()+" Clusters."+"\n");
		
		return ret;

	}


	@Override
	protected void findCorrespondingCluster()  {

		/* Initialize the id list (list of point id's) for every cluster 
		 * with an empty list
		 */
		for (TIntObjectIterator<TIntArrayList> clusterIter = this.clusterToPoint.iterator(); clusterIter.hasNext(); )  {

			clusterIter.advance();
			clusterIter.value().clear();  /* here we just clear the list instead of replacing the old with a new one  */
		}


		/*
		 * are there still changes in the mapping ?
		 */
//		this.changedClustering = false;

		/*  
		 */
		double roundError = 0;

		/* at first determine for each point the corresponding cluster
		 * (the cluster with the minimum disctance)
		 */
		for (TIntIntIterator pointIter = this.pointToCluster.iterator(); 
		pointIter.hasNext();)  {

			pointIter.advance();

			int i = pointIter.key();  /* save the current key from the map point -> cluster
			 * in the temporary counter i, so we don't have to access
			 * the iterator twice
			 */

			int minIndex = 0;   /* the index of the cluster with minimal distance to the current point */

			double minDistance = distance.getDistance(this.pointCoordinates[i], this.clusterCoordinates[minIndex]);

			double currentDistance;

			/* Try each cluster. 
			 * For the access to the cluster we use an iterator over the map with the cluster -> point
			 * mapping. If a cluster gets empty, we delete his id from this map, this cluster is not
			 * considered any more.
			 * Because of that we cannot iterate simply over the array with cluster coordinates, since
			 * the "dead" cluster are not deleted from here (it would be to expensive)
			 */

			for (TIntObjectIterator<TIntArrayList> clusterIter = this.clusterToPoint.iterator(); clusterIter.hasNext(); )  {

				clusterIter.advance();

				int j  = clusterIter.key(); /* we save the key in the count variable j , so we 
				 * don't have to access it twice.
				 * With the key we gain the coordinates of the cluster
				 * from the clusterCoordinates array
				 */

				currentDistance = this.distance.getDistance(this.pointCoordinates[i], this.clusterCoordinates[j]);

				if (currentDistance < minDistance)  {

					minDistance = currentDistance;
					minIndex = j;

				}

			}

			roundError += minDistance;

			/* write down the new assignments to both maps: point -> cluster and cluster -> point
			 */ 
			if (pointIter.value() != minIndex)  {

//				this.changedClustering = true;   /* since at least one point is now assigned
//				 * to another cluster, the clustering is still changing
//				 */

				pointIter.setValue(minIndex);  /* update the new cluster id  */

			}

			clusterToPoint.get(minIndex).add(i);

		}

		this.globalError.add(roundError);
		
		this.cTask.writeLog("Round: "+this.currentRound+"/"+this.maxRounds+" with error: "+roundError+"\n");
		this.cTask.setProgress((int)(((currentRound+1.) / maxRounds)*10000));
	}

	@Override
	protected void checkState() {
		super.checkState();
		/*
		 * After the first round the error can increase, so we would finish after the first round, obviously stuck in
		 * a local optimum
		 */
		if (this.currentRound > 1) {
			if (Math.abs(this.globalError.get(this.currentRound -1 ) - this.globalError.get(this.currentRound -2 ) ) < Double.MIN_VALUE) {
				this.state = KMeansBase.FINISHED;
				this.cTask.writeLog("Minimal error has been reached. END!\n");
				this.cTask.setProgress(10000);
			}
		}
	}
}
