package mayday.expressionmapping.clustering;

import java.util.Random;

import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.expressionmapping.gnu_trove_adapter.TDoubleArrayList;

/**
 * @author jaeger
 *
 */
public abstract class EMClusterAlgorithm extends PartitioningClustering {

	/**
	 * @param Data
	 * @param settings
	 */
	public EMClusterAlgorithm(PermutableMatrix Data, PartitioningClusteringSetting settings) {
		super(Data, settings);
	}

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
	
	protected TDoubleArrayList globalError;
	
	protected double[][] clusterCoordinates;
	protected double[][] pointCoordinates;
	
	protected int currentRound;
	protected double[] centerOfMass;
	//protected PointList<DataPoint> points;
	
	protected int numberOfClusters;
	protected int numberOfPoints;
	protected int maxRounds;
	
	protected Random rand = new Random();
	
	protected int state;
	protected int dimension;
	
	/**
	 * point and cluster initialization
	 */
	public void initialize() {
		this.initializeInput();
		this.initializeCluster();
	}
	
	private void initializeInput()  {
		this.pointCoordinates = ((DoubleMatrix)this.ClusterData).getInternalData();
	}

	private void initializeCluster()  {
		this.clusterCoordinates = new double[numberOfClusters][dimension];
		int[] access = new int[numberOfPoints];

		//fill access array
		for (int i = 0;  i < access.length; ++i) {
			access[i] = i;
		}

		/* 
		 * we use the sorted points to choose a few of them, 
		 * so we get a better distribution within our cluster start-values
		 */
		DMQuickSort sort = new DMQuickSort();
		int[] sortIndex = sort.sortAsc((DoubleMatrix)this.ClusterData, 0);
		int index;

		//Main Loop to choose the cluster start values
		for (int i = 0; i < numberOfClusters;)  {
			//choose a point randomly with help of our access array
			index =  rand.nextInt(access.length);
			/* 
			 * Has the index been chosen before ?
			 * We use a native array to hold the access indices, if we would store them in a
			 * linked list, we could delete the chosen values. However the access to a linked list
			 * is linear in time and the chance to get one value twice is not big enough (many points, only a few clusters)
			 * to compensate this.
			 * Another disadvantage of a linked list is, that no primitives can be stored
			 */
			if (access[index] != -1)  {
				/* 
				 * we use the randomly picked index from the access array to access the sortIndex
				 * array, which holds the sorted indices of our points
				 * with the (now randomly chosen) entry from the sortIndex we access the points List
				 * and get the barycentric coordinates
				 */
				this.clusterCoordinates[i] = this.ClusterData.getRow(sortIndex[access[index]]).toArray();
				//at last we have to mark the chosen index as already used
				access[index] = -1;
				++i;
			}
		}
	}
}
