package mayday.expressionmapping.model.algorithm.cluster;

import java.util.Arrays;
import java.util.Random;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.tasks.AbstractTask;
import mayday.expressionmapping.controller.Constants;
import mayday.expressionmapping.gnu_trove_adapter.TDoubleArrayList;
import mayday.expressionmapping.model.algorithm.AlgorithmFactory;
import mayday.expressionmapping.model.algorithm.sort.SortAlgorithm;
import mayday.expressionmapping.model.geometry.ClusterPoint;
import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.DataPoint2D;
import mayday.expressionmapping.model.geometry.DataPoint3D;
import mayday.expressionmapping.model.geometry.DataPoint4D;
import mayday.expressionmapping.model.geometry.container.PointList;

/**
 * @author jaeger
 *
 */
public abstract class SelfOrganizeBatchClustering  {

	/**
	 * 
	 */
	public static final int WAITING = -1; 

	/**
	 * 
	 */
	public static final int RUNNING  = 0;

	/**
	 * 
	 */
	public static final int FINISHED = 1;

	/**
	 * 
	 */
	public static final int DEFAULTMAXROUNDS = 1000;


	/* the list of input, the data points we want to cluster
	 */
	protected PointList<DataPoint> points;

	/* fields with the cooordinates of the data points and the cluster vectors for
	 * faster access during the cluster process
	 */
	protected double[][] clusterCoordinates;

	protected double[][] pointCoordinates;

	protected double[] centerOfMass;



	/* the state of the algorithmus, still running or already finished
	 */
	protected int state;

	/* the maximal number of steps we will proceed
	 */
	protected int maxRounds;

	/* the current cluster round
	 */
	protected int currentRound;

	/* the number of data points we want to cluster, so we don't have to access the size
	 * function of the list with the points
	 */
	protected int numberOfPoints;

	/* the number of cluster we want to construct
	 */
	protected int numberOfCluster;

	/* the dimension of the space we want to cluster in (determined by the dimension of
	 * the input points)
	 */
	protected int dim; 

	/* the sort Algorithm we use to sort the input points
	 */
	protected SortAlgorithm sort;

	protected DistanceMeasurePlugin distance;

	protected Random rand;

	protected TDoubleArrayList globalError;

	protected boolean centerSet;
	
	protected AbstractTask cTask;

	/**
	 * @param distance
	 */
	public SelfOrganizeBatchClustering(DistanceMeasurePlugin distance)  {

		/* variables with public access are initialized
		 */
		this.state = WAITING;

		this.globalError = null;

		this.currentRound = -1;

		this.centerSet = false;

		this.sort = AlgorithmFactory.getSortAlgorithm(Constants.QSORT);

		this.rand = new Random();

		this.distance = distance;
	}

	/**
	 * @param points
	 * @param numberOfCluster
	 * @return point list of cluster points
	 */
	public PointList<ClusterPoint> cluster(PointList<DataPoint> points,
			int numberOfCluster) {
		return cluster(points, numberOfCluster, DEFAULTMAXROUNDS);
	}

	/**
	 * @param points
	 * @param numberOfCluster
	 * @param maxRounds
	 * @return point list of cluster points
	 */
	public PointList<ClusterPoint> cluster(PointList<DataPoint> points, int numberOfCluster, int maxRounds)  {
		this.points = points;

		this.numberOfPoints = this.points.size();

		this.numberOfCluster = numberOfCluster;

		/* assuming all points have the same dimension, determine it from the first of them
		 */
		this.dim = points.get(0).getDimension();

		if (maxRounds > 0)
			this.maxRounds = maxRounds;

		else
			this.maxRounds = DEFAULTMAXROUNDS;

		initialize();

		run();

		return getCluster();
	}


	/**
	 * start clustering
	 */
	public void run()  {

		/* starting the algorithmus
		 */
		this.state = RUNNING;

		/* entering the first round
		 */
		this.currentRound = 0;

		/* MAIN LOOP
		 */
		while (this.state == RUNNING) {
			findCorrespondingCluster();
			computeNewClusterCenter();
			update();	/* here: incrementing the round  */
			/* check the state of our algorithmus after each step, if it is time for
			 * termination
			 */
			checkState();
		}
	}

	/**
	 * @return current round
	 */
	public int getCurrentRound()  {
		return this.currentRound;
	}

	/**
	 * @return state
	 */
	public int getState()  {
		return this.state;
	}

	protected void setCenterOfMass(PointList<ClusterPoint> clusters)  {
		DataPoint center = null;

		switch (this.dim)  {
		case 2: 
			center = new DataPoint2D(0);
            center.setCoordinates(this.centerOfMass);
			break;
		case 3: 
			center = new DataPoint3D(0);
            center.setCoordinates(this.centerOfMass);
			break;
		case 4: 
			center = new DataPoint4D(0);
            center.setCoordinates(this.centerOfMass);
			break;
		}
		clusters.setCenterOfMass(center);
	}

	protected void initialize()  {
		initializeInput();
		initializeCluster();
		/* intialize the list holding the error E (variance over all points to their cluster,
		 * this function we want to minimize) for every round,
		 * thus the capacity must be the maximum number of steps
		 */
		this.globalError = new TDoubleArrayList(this.maxRounds);
		this.centerOfMass = new double[this.dim];
		Arrays.fill(this.centerOfMass, 0);
		this.currentRound = 0;
	}

	protected void initializeInput()  {
		/* initialize the pointcoordinates array
		 */
		this.pointCoordinates = new double[this.numberOfPoints][];

		/* copy the coordinates of the points to the appropriate field
		 */
		for (int i = 0; i < this.numberOfPoints; ++i)  {
			this.pointCoordinates[i] = this.points.get(i).getCoordinates();
		}
	}

	protected void initializeCluster()  {
		this.clusterCoordinates = new double[this.numberOfCluster][this.dim];
		int[] access = new int[this.numberOfPoints];

		/* fill access array
		 */
		for (int i = 0;  i < access.length; ++i)
			access[i] = i;

		/* we use the sorted points to choose a few of them, so we get a better
		 * distribution within our cluster start-values
		 */
		int[] sortIndex = sort.sortAsc(this.points, SortAlgorithm.COORDINATES, 0);

		int index;

		/* Main Loop for choose the cluster start values
		 */
		for (int i = 0; i < this.numberOfCluster;)  {
			/* choose randomly a point with help of our access array
			 */
			index =  rand.nextInt(access.length);
			/* Has the index been chosen before ?
			 * We use a native array to hold the access indices, if we would store them in a
			 * linked list, we could delete the chosen values. However the access to a linked list
			 * is linear in time and the chance to get one value twice is not big enough (many points, a few cluster)
			 * to compensate this.
			 * Another disadvantage of a linked list is, that we only can store Objects but no
			 * primitives
			 */
			if (access[index] != -1)  {
				/* we use the randomly picked index from the access array to access the sortIndex
				 * array, which holds the sorted indeces of our points
				 * with the (now randomly choosed) entry from the sortIndex we access the points List
				 * and get the barycentric coordinates
				 */
				this.clusterCoordinates[i] = points.get(sortIndex[access[index]]).getCoordinates();
				/* at least we have to mark the chosen index as already used
				 */
				access[index] = -1;
				++i;
			}
		}
	}

	protected void update() {
		++this.currentRound;
	}

	protected void checkState() {
		if (this.currentRound >= this.maxRounds)
			this.state = FINISHED;
	}

	protected abstract PointList<ClusterPoint> getCluster();

	protected abstract void findCorrespondingCluster();

	protected abstract void computeNewClusterCenter();
	
	/**
	 * @param cTask
	 */
	public void setClusterTask(AbstractTask cTask) {
		this.cTask = cTask;
	}
}
