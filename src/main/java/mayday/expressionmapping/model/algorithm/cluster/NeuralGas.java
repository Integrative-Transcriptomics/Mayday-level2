package mayday.expressionmapping.model.algorithm.cluster;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.expressionmapping.model.geometry.ClusterPoint;
import mayday.expressionmapping.model.geometry.ClusterPoint2D;
import mayday.expressionmapping.model.geometry.ClusterPoint3D;
import mayday.expressionmapping.model.geometry.ClusterPoint4D;
import mayday.expressionmapping.model.geometry.container.PointList;
import mayday.expressionmapping.utils.Array;

/**
 * @author Stephan Gade
 *
 */

public class NeuralGas extends SelfOrganizeBatchClustering  implements ClusterAlgorithm {
	private int[][] rankings;

	private double lambda;

	private double lambda0;

	/**
	 * @param distance
	 */
	public NeuralGas(DistanceMeasurePlugin distance)  {
		super(distance);
	}

	@Override
	protected void update()  {
		super.update();
		reduceNeighbourRate();
	}

	@Override
	protected void initialize()  {
		super.initialize();
		lambda0 = (double)this.numberOfCluster / 2;
		this.lambda = lambda0;
		rankings = new int[this.numberOfCluster][this.numberOfPoints];
	}

	//////////////mit HashMap
	
//	protected void findCorrespondingCluster()  {
//
//
//		Map<Integer, Double> clusterRanks = new HashMap<Integer, Double>(this.numberOfCluster);
//
//		double roundError = 0;
//		
//		/* iterate over all Points
//		 */
//		for (int i = 0; i < this.numberOfPoints; ++i)  {
//
//
//			/* clear the cluster ranking
//			 */
//			clusterRanks.clear();
//
//			/* iterate over all cluster and compute the distances to our current data point,
//			 * thus, we are able to 
//			 */
//			double currentDistance;
//
//			for (int j = 0; j < this.numberOfCluster; ++j)  {
//
//				currentDistance = this.distance.computeDistance(this.pointCoordinates[i], this.clusterCoordinates[j]);
//
//				clusterRanks.put(j, currentDistance);
//
//			}
//
//			List<Integer> sortedIndices = Array.sortKeysByValues(clusterRanks);
//			
//			//for (int m = 0; m < sortedIndices.size(); ++m)
//			//	System.err.println(m+":  "+clusterRanks.get(sortedIndices.get(m)));
//
//			/* now the List is ready and we can built the mapping cluster -> points
//			 */
//
//			/* list sortedIndices is permutation our cluster points (ID's) and so contains
//			 * numberOfCluster entries
//			 */
//			for (int k = 0; k < this.numberOfCluster; ++k)  {
//
//				this.rankings[sortedIndices.get(k)][i] = k;
//
//
//			}
//
//			/* compute global error sum
//			 */
//			roundError += clusterRanks.get(sortedIndices.get(0)); 
//				
//		}
//		
//		this.globalError.add(roundError);
//		
//		System.out.println("Round: "+this.currentRound+"/"+this.maxRounds+" with error: "+roundError+" and lambda: "+this.lambda);
//
//	}

	
	//////////// with lists
	
//	protected void findCorrespondingCluster()  {
//
//
//		//Map<Integer, Double> clusterRanks = new HashMap<Integer, Double>(this.numberOfCluster);
//
//
//		List<Integer> clusterRanks = new LinkedList<Integer>();
//
//		List<Double> clusterDistances = new LinkedList<Double>();
//
//		double roundError = 0;
//
//		/* iterate over all Points
//		 */
//		for (int i = 0; i < this.numberOfPoints; ++i)  {
//
//
////			/* clear the cluster ranking
////			*/
////			clusterRanks.clear();
//
//			/* clear both linked lists
//			 */
//			clusterRanks.clear();
//			clusterDistances.clear();
//
//			/* iterate over all cluster and compute the distances to our current data point,
//			 * thus, we are able to 
//			 */
//			double currentDistance;
//
//			/* initialize both lists
//			 */
//			//clusterRanks.add(0);
//			//clusterDistances.add(this.distance.computeDistance(this.pointCoordinates[i], this.clusterCoordinates[0]));
//
//			for (int j = 0; j < this.numberOfCluster; ++j)  {
//
//				currentDistance = this.distance.computeDistance(this.pointCoordinates[i], this.clusterCoordinates[j]);
//
//				boolean insert = false;
//
//				ListIterator<Integer> rankIter = clusterRanks.listIterator();
//				ListIterator<Double> distIter = clusterDistances.listIterator();
//
//				/* iterate over the two lists (both lists has the same amount of values stored, so it is enough to check
//				 * one of them) till we find one distance greater than the current
//				 */
//				for (; distIter.hasNext() && !insert;)  {
//
//					
//					rankIter.next();
//					
//					/* 
//					 */
//					if (currentDistance < distIter.next())  {
//
//						/* move one step backward
//						 */
//						distIter.previous();
//						rankIter.previous();
//						
//						/* insert the current distance and the matching cluster index j
//						 * to the two lists
//						 */
//						distIter.add(currentDistance);
//						rankIter.add(j);
//
//						/* set the indicator variable to true, so we can end the loop
//						 */
//						insert = true;
//
//					}
//
//				}
//
//				/* the distance is greater than any other distances and have to be added
//				 * at the end of the list
//				 */
//				if (!insert)  {
//
//					clusterRanks.add(j);
//
//					clusterDistances.add(currentDistance);
//
//				}
//			}
//
//
//			//System.out.println(clusterRanks);
//			//System.out.println(clusterDistances);
//			
//			
//			//List<Integer> sortedIndices = Array.sortKeysByValues(clusterRanks);
//
//			//for (int m = 0; m < sortedIndices.size(); ++m)
//			//	System.err.println(m+":  "+clusterRanks.get(sortedIndices.get(m)));
//
//			/* now the List is ready and we can built the mapping cluster -> points
//			 */
//
//			/* list sortedIndices is permutation our cluster points (ID's) and so contains
//			 * numberOfCluster entries
//			 */
//			//for (int k = 0; k < this.numberOfCluster; ++k)  {
//			
//			ListIterator<Integer> clusterIter = clusterRanks.listIterator();
//			
//			for (int k = 0; clusterIter.hasNext(); ++k)  {
//				
//				this.rankings[clusterIter.next()][i] = k;
//
//			}
//
//
//
//			/* compute global error sum
//			 */
//			roundError += clusterDistances.get(0); 
//
//		}
//
//		this.globalError.add(roundError);
//
//		System.out.println("Round: "+this.currentRound+"/"+this.maxRounds+" with error: "+roundError+" and lambda: "+this.lambda);
//
//	}
	
	
	
//	////////////with TreeSet
//	protected void findCorrespondingCluster()  {
//
//
//		TreeSet<Error> clusterRanks = new TreeSet<Error>();
//		
//		
//		double roundError = 0;
//		
//		/* iterate over all Points
//		 */
//		for (int i = 0; i < this.numberOfPoints; ++i)  {
//
//
//			/* clear the cluster ranking
//			 */
//			clusterRanks.clear();
//
//			/* iterate over all cluster and compute the distances to our current data point,
//			 * thus, we are able to 
//			 */
//			double currentDistance;
//
//			for (int j = 0; j < this.numberOfCluster; ++j)  {
//
//				currentDistance = this.distance.computeDistance(this.pointCoordinates[i], this.clusterCoordinates[j]);
//
//				clusterRanks.add(new Error(j, currentDistance));
//
//			}
//
//			Iterator<Error> clusterIter = clusterRanks.iterator();
//			
//			for (int k = 0; clusterIter.hasNext(); ++k)  {
//
//				this.rankings[clusterIter.next().key][i] = k;
//
//
//			}
//
//			/* compute global error sum
//			 */
//			roundError += clusterRanks.first().value; 
//				
//		}
//		
//		this.globalError.add(roundError);
//		
//		System.out.println("Round: "+this.currentRound+"/"+this.maxRounds+" with error: "+roundError+" and lambda: "+this.lambda);
//
//	}
	
//	////////////with one Array
//	protected void findCorrespondingCluster()  {
//
//
//		double[] clusterDistances = new double[this.numberOfCluster];
//		
//		Integer[] clusterRanks;
//		
//		double roundError = 0;
//		
//		/* iterate over all Points
//		 */
//		for (int i = 0; i < this.numberOfPoints; ++i)  {
//
//			for (int j = 0; j < this.numberOfCluster; ++j)  
//				clusterDistances[j] = this.distance.computeDistance(this.pointCoordinates[i], this.clusterCoordinates[j]);
//
//			clusterRanks = Array.rankArray(clusterDistances);
//			
//			for (int k = 0; k < this.numberOfCluster; ++k)  
//				this.rankings[clusterRanks[k]][i] = k;
//
//			
//			/* compute global error sum
//			 */
//			roundError += clusterDistances[clusterRanks[0]]; 
//				
//		}
//		
//		this.globalError.add(roundError);
//		
//		System.out.println("Round: "+this.currentRound+"/"+this.maxRounds+" with error: "+roundError+" and lambda: "+this.lambda);
//
//	}
	
	
	////////////with own Quicksort
	@Override
	protected void findCorrespondingCluster()  {
		double[] clusterDistances = new double[this.numberOfCluster];
		int[] clusterRanks;
		double roundError = 0;
		
		/* iterate over all Points
		 */
		for (int i = 0; i < this.numberOfPoints; ++i)  {

			for (int j = 0; j < this.numberOfCluster; ++j)				
				clusterDistances[j] = this.distance.getDistance(this.pointCoordinates[i], this.clusterCoordinates[j]);
				
			clusterRanks = Array.sortArray(clusterDistances);
			
			for (int k = 0; k < this.numberOfCluster; ++k)  
				this.rankings[clusterRanks[k]][i] = k;

			
			/* compute global error sum
			 */
			roundError += clusterDistances[clusterRanks[0]]; 
				
		}
		this.globalError.add(roundError);
		
		this.cTask.writeLog("Round: "+this.currentRound+"/"+this.maxRounds+" with error: "+roundError+" and lambda: "+this.lambda+"\n");
		this.cTask.setProgress((int)(((currentRound+1.) / maxRounds)*10000));
	}
	
	//////////with one ERROR Array
//	protected void findCorrespondingCluster()  {
//
//
//		Error[] clusterRanks = new Error[this.numberOfCluster];
//		
//		double roundError = 0;
//		
//		/* iterate over all Points
//		 */
//		for (int i = 0; i < this.numberOfPoints; ++i)  {
//
//			double currentDistance;
//			
//			for (int j = 0; j < this.numberOfCluster; ++j)  {
//				
//				currentDistance = this.distance.computeDistance(this.pointCoordinates[i], this.clusterCoordinates[j]);
//
//				clusterRanks[j] = new Error(j, currentDistance);
//			}
//				
//			java.util.Arrays.sort(clusterRanks);
//			
//			for (int k = 0; k < this.numberOfCluster; ++k)  
//				this.rankings[clusterRanks[k].key][i] = k;
//
//			
//			/* compute global error sum
//			 */
//			roundError += clusterRanks[0].value; 
//				
//		}
//		
//		this.globalError.add(roundError);
//		
//		System.out.println("Round: "+this.currentRound+"/"+this.maxRounds+" with error: "+roundError+" and lambda: "+this.lambda);
//
//	}
	
	@Override
	protected void computeNewClusterCenter()  {
		for (int i = 0; i < this.numberOfCluster; ++i)  {
			double lambda_i;
			double sumWeights = 0;

			Arrays.fill(this.clusterCoordinates[i], 0);
			for (int j = 0; j < this.numberOfPoints; ++j)  {
				double rank = this.rankings[i][j];
				lambda_i = Math.exp(-rank/this.lambda);
				/* determine the new cluster vector as center of mass of all points,
				 * weighted by the rank (a function of it: lambda_i) , the cluster vector has to these points
				 */
				for (int k = 0; k < this.dim; ++k)
					this.clusterCoordinates[i][k] += lambda_i * this.pointCoordinates[j][k];
				sumWeights += lambda_i;
			}	
			/* at least we have to divide by sum of all ranks
			 */
			Array.divide(this.clusterCoordinates[i], sumWeights);
		}
	}

	private void reduceNeighbourRate()  {
		this.lambda = this.lambda0 * Math.pow(0.01 / this.lambda0,(double)this.currentRound / (double)this.maxRounds);	
	}

	@Override
	protected PointList<ClusterPoint> getCluster()  {
		List<ClusterPoint> clusters = new Vector<ClusterPoint>(this.numberOfCluster);
		switch (this.dim)  {
		case 2: {
			int index = 0;
			/* main loop: iterate over all cluster centres, collect their coordinates
			 * and members
			 */
			for (int i = 0; i < this.numberOfCluster; ++i)  {
				ClusterPoint2D currentPoint = new ClusterPoint2D(index);
				/* find every points, this cluster is the winner neuron for
				 */
				for (int j = 0; j < this.numberOfPoints; ++j)  {
					if (this.rankings[i][j] == 0)
						currentPoint.addMembertoCluster(j);
				}

				/* has this cluster members or is it empty
				 */
				if (currentPoint.getNumberofMembers() > 0)  {
					currentPoint.setCoordinates(this.clusterCoordinates[i]);
					clusters.add(currentPoint);
					++index;
					Array.add(this.centerOfMass, this.clusterCoordinates[i]);
				}
			}
			break;
		}
		case 3: {
			int index = 0;
			/* main loop: iterate over all cluster centres, collect their coordinates
			 * and members
			 */
			for (int i = 0; i < this.numberOfCluster; ++i)  {
				ClusterPoint3D currentPoint = new ClusterPoint3D(index);
				/* find every points, this cluster is the winner neuron for
				 */
				for (int j = 0; j < this.numberOfPoints; ++j)  {
					if (this.rankings[i][j] == 0)
						currentPoint.addMembertoCluster(j);
				}

				/* has this cluster members or is it empty
				 */
				if (currentPoint.getNumberofMembers() > 0)  {
					currentPoint.setCoordinates(this.clusterCoordinates[i]);
					clusters.add(currentPoint);
					++index;
					Array.add(this.centerOfMass, this.clusterCoordinates[i]);
				}
			}
			break;
		}
		case 4: {
			int index = 0;
			/* main loop: iterate over all cluster centres, collect their coordinates
			 * and members
			 */
			for (int i = 0; i < this.numberOfCluster; ++i)  {
				ClusterPoint4D currentPoint = new ClusterPoint4D(index);
				/* find every points, this cluster is the winner neuron for
				 */
				for (int j = 0; j < this.numberOfPoints; ++j)  {
					if (this.rankings[i][j] == 0)
						currentPoint.addMembertoCluster(j);
				}

				/* has this cluster members or is it empty
				 */
				if (currentPoint.getNumberofMembers() > 0)  {
					currentPoint.setCoordinates(this.clusterCoordinates[i]);
					clusters.add(currentPoint);
					++index;
					Array.add(this.centerOfMass, this.clusterCoordinates[i]);
				}
			}
			break;
		}
		}

		Array.divide(this.centerOfMass, (double)clusters.size());
		PointList<ClusterPoint> ret = new PointList<ClusterPoint>(clusters, this.points.getGroupLabels());
		setCenterOfMass(ret);
		
		return ret;
	}
	
	@Override
	protected void checkState()  {
		super.checkState();
		if (this.currentRound > 1)
			if (Math.abs(this.globalError.get(this.currentRound -1 ) - this.globalError.get(this.currentRound -2 ) ) < Double.MIN_VALUE) {
				
				this.state = NeuralGas.FINISHED;
				
				this.cTask.writeLog("Minimal error has been reached. END!\n");
				this.cTask.setProgress(10000);
			}
	}

	@Override
	public String getTitle() {
		return "Neural Gas Clustering";
	}
}
