package mayday.expressionmapping.clustering.neuralgas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mayday.clustering.ClusterTask;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.expressionmapping.clustering.EMClusterAlgorithm;
import mayday.expressionmapping.clustering.PartitioningClusteringSetting;
import mayday.expressionmapping.gnu_trove_adapter.TDoubleArrayList;
import mayday.expressionmapping.utils.Array;

/**
 * @author jaeger
 *
 */
public class NeuralGasClustering extends EMClusterAlgorithm {
	
	/**
	 * @param Data
	 * @param settings 
	 */
	public NeuralGasClustering(PermutableMatrix Data, PartitioningClusteringSetting settings) {
		super(Data, settings);
	}

	private int[][] rankings;
	private double lambda;
	private double lambda0;
	
	private ClusterTask cTask;
	
	private DistanceMeasurePlugin distanceMeasure;
	
	/**
	 * @param cTask
	 */
	public void setClusterTask(ClusterTask cTask) {
		this.cTask = cTask;
	}
	
	@Override
	public int[] cluster() {
		initialize();
		return run();
	}
	
	
	public void initialize() {
		//get the chosen settings
		distanceMeasure = settings.getDistanceMeasure();
		
		maxRounds = settings.getMaxNumOfRounds();
		numberOfClusters = settings.getNumOfClusters();
		numberOfPoints = ClusterData.nrow();
		dimension = settings.getDimension();
		
		lambda0 = (double)numberOfClusters / 2.0;
		lambda = lambda0;
		rankings = new int[numberOfClusters][numberOfPoints];
		
		super.initialize();
		
		globalError = new TDoubleArrayList(maxRounds);
		centerOfMass = new double[dimension];
		Arrays.fill(this.centerOfMass, 0);
		currentRound = 0;
	}

	private int[] getClusters()  {
		List<List<Integer>> clusterStructure = new ArrayList<List<Integer>>();
		
		//main loop: iterate over all cluster centers and collect their members
		for (int i = 0; i < this.numberOfClusters; ++i)  {
			List<Integer> currentCluster = new ArrayList<Integer>();
			
			for (int j = 0; j < this.numberOfPoints; ++j)  {
				if (this.rankings[i][j] == 0) {
					currentCluster.add(j);
				}
			}
			
			if(currentCluster.size() > 0) {
				clusterStructure.add(currentCluster);
			}
		}
		
		//create a mapping of every probe to its cluster
		int[] result = new int[numberOfPoints];
		for(int i = 0; i < clusterStructure.size(); i++) {
			ArrayList<Integer> currentCluster = (ArrayList<Integer>)clusterStructure.get(i);
			
			for(int j = 0; j < currentCluster.size(); j++) {
				Integer probe = currentCluster.get(j);
				result[probe] = clusterStructure.size() - i - 1;
			}
		}
		
		return result;
	}
	
	protected int[] run()  {
		//start the algorithm
		this.state = RUNNING;

		//enter the first round
		this.currentRound = 0;

		//main loop
		while (this.state == RUNNING) {
			findCorrespondingCluster();
			computeNewClusterCenter();
			update();
			//here: incrementing the round
			/* 
			 * check the state of our algorithm after each step, 
			 * to see if it is time for termination
			 */
			boolean canceled = checkState();
			if(canceled) {
				return null;
			}
		}
		
		this.cTask.reportCurrentFractionalProgressStatus(1.0);
		
		return this.getClusters();
	}

	protected void findCorrespondingCluster()  {
		double[] clusterDistances = new double[this.numberOfClusters];
		int[] clusterRanks;
		double roundError = 0;
		
		//iterate over all Points
		for (int i = 0; i < numberOfPoints; ++i)  {
			for (int j = 0; j < numberOfClusters; ++j) {
				clusterDistances[j] = distanceMeasure.getDistance(this.pointCoordinates[i], this.clusterCoordinates[j]);
			}
			
			clusterRanks = Array.sortArray(clusterDistances);
			
			for (int k = 0; k < this.numberOfClusters; ++k) {
				this.rankings[clusterRanks[k]][i] = k;
			}

			//compute global error sum
			roundError += clusterDistances[clusterRanks[0]];	
		}
		this.globalError.add(roundError);
	}
	
	protected void computeNewClusterCenter()  {
		for (int i = 0; i < this.numberOfClusters; ++i)  {
			double lambda_i;
			double sumWeights = 0;

			Arrays.fill(this.clusterCoordinates[i], 0);
			for (int j = 0; j < this.numberOfPoints; ++j)  {
				double rank = this.rankings[i][j];
				lambda_i = Math.exp(-rank/this.lambda);
				/* 
				 * determine the new cluster vector as center of mass of all points,
				 * weighted by the rank (a function of it: lambda_i) , the cluster vector has to these points
				 */
				for (int k = 0; k < this.dimension; ++k)
					this.clusterCoordinates[i][k] += lambda_i * this.pointCoordinates[j][k];
				sumWeights += lambda_i;
			}	
			//at least we have to divide by sum of all ranks
			Array.divide(this.clusterCoordinates[i], sumWeights);
		}
	}
	
	protected void update()  {
		++currentRound;
		reduceNeighbourRate();
	}
	
	private void reduceNeighbourRate()  {
		this.lambda = this.lambda0 * Math.pow(0.01 / this.lambda0,(double)this.currentRound / (double)this.maxRounds);	
	}
	
	protected boolean checkState()  {
		if (this.currentRound >= this.maxRounds) {
			this.state = FINISHED;
			
			if(this.cTask != null) {
				if(this.cTask.hasBeenCancelled()) {
					return doCancel();
				}
				this.cTask.reportCurrentFractionalProgressStatus(1.0);
				this.cTask.writeLog("Maximal number of rounds has been reached.\n");
				this.cTask.writeLog("Neural Gas Clustering finished!\n");
			}
		}
			
		
		if (this.currentRound > 1) {
			if (Math.abs(this.globalError.get(this.currentRound -1 ) - this.globalError.get(this.currentRound -2 ) ) < Double.MIN_VALUE) {
				this.state = FINISHED;
				if(this.cTask != null) {
					if(this.cTask.hasBeenCancelled()) {
						return doCancel();
					}
					this.cTask.reportCurrentFractionalProgressStatus(1.0);
					this.cTask.writeLog("Minimal error has been reached.\n");
					this.cTask.writeLog("Neural Gas Clustering finished!\n");
				}
			}
		}
		
		if(this.cTask != null) {
			if(this.cTask.hasBeenCancelled()) {
				return doCancel();
			}
			this.cTask.reportCurrentFractionalProgressStatus((double)currentRound / (double)maxRounds);
		}
		
		return false;
	}
	
	private boolean doCancel() {
		this.cTask.writeLog("Neural Gas Clustering has been canceled!");
		this.cTask.processingCancelRequest();
		return true;
	}
}
