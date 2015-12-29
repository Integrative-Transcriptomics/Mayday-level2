package mayday.expressionmapping.model.algorithm;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.math.distance.measures.EuclideanDistance;
import mayday.expressionmapping.controller.Constants;
import mayday.expressionmapping.model.algorithm.cluster.ClusterAlgorithm;
import mayday.expressionmapping.model.algorithm.cluster.NeuralGas;
import mayday.expressionmapping.model.algorithm.cluster.SimpleKMeans;
import mayday.expressionmapping.model.algorithm.cluster.WeightedKMeans;
import mayday.expressionmapping.model.algorithm.sort.Quicksort;
import mayday.expressionmapping.model.algorithm.sort.SortAlgorithm;
import mayday.expressionmapping.model.algorithm.transform.AttractorComputer;
import mayday.expressionmapping.model.algorithm.transform.FoldchangeBaryComputer;
import mayday.expressionmapping.model.algorithm.transform.RankBaryComputer;
import mayday.expressionmapping.model.algorithm.transform.SimpleAttractorComputer;
import mayday.expressionmapping.model.algorithm.transform.SimpleBaryComputer;
import mayday.expressionmapping.model.algorithm.transform.TransformAlgorithm;

/**
 * This is a class following the factory pattern. It provides various algorithm classes
 * which operates on the data or cluster points.
 * 
 * @author Stephan Gade <stephan.gade@googlemail.com>
 */
public class AlgorithmFactory {

	
	/**
	 * Method to provide a TransformAlgorithm.
	 * @see TransformAlgorithm
	 * @param type Type of the TransformAlgorithm operating  on the data or cluster points.
	 * @return transform algorithm
	 */
	public static TransformAlgorithm getTransformAlgorithm(int type) {
		switch (type) {
			case Constants.SIMPLE:
				return new SimpleBaryComputer();
			case Constants.FOLDCHANGE:
				return new FoldchangeBaryComputer();
			case Constants.RANK:
				return new RankBaryComputer();
			case Constants.ATTRAC:
				return new SimpleAttractorComputer(new EuclideanDistance());
			default: {
				return new SimpleBaryComputer();
			}
		}
	}
	
	/**
	 * @param type
	 * @param distanceMeasure
	 * @return attractor computer
	 */
	public static AttractorComputer getAttractorComputer(int type, DistanceMeasurePlugin distanceMeasure)  {
		switch (type) {
			case Constants.ATTRAC: {
				if (distanceMeasure != null)
					return new SimpleAttractorComputer(distanceMeasure);
				else
					return new SimpleAttractorComputer(new EuclideanDistance());
			}
			default: {
				return new SimpleAttractorComputer(new EuclideanDistance());
			}
		}
	}
	
	/**
	 * @param type
	 * @param distanceMeasure
	 * @return cluster algorithm
	 */
	public static ClusterAlgorithm getClusterAlgorithm(int type, DistanceMeasurePlugin distanceMeasure) {
		switch (type) {
			case Constants.KMEANS:
				return new SimpleKMeans(distanceMeasure);

			case Constants.WKMEANS:
				return new WeightedKMeans(distanceMeasure);

			case Constants.NG:
				return new NeuralGas(distanceMeasure);

			default: {
				return new SimpleKMeans(distanceMeasure);
			}
		}
	}

	/**
	 * @param type
	 * @return sort algorithm
	 */
	public static SortAlgorithm getSortAlgorithm(int type) {
		switch (type) {
			case Constants.QSORT:
				return new Quicksort();
			default: {
				return new Quicksort();
			}
		}
	}
}
