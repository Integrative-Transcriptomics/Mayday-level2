package mayday.expressionmapping.model.algorithm.transform;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.container.PointList;
import mayday.expressionmapping.utils.Array;

/**
 * @author Stephan Gade
 * 
 */
public class SimpleAttractorComputer implements AttractorComputer {

	private static final double[][] attrac2D = { { 1, 0 }, { 0, 1 },
			{ 0.5, 0.5 } };

	private static final double[][] attrac3D = { { 1, 0, 0 }, { 0, 1, 0 },
			{ 0, 0, 1 }, { 0.5, 0.5, 0 }, { 0.5, 0, 0.5 }, { 0, 0.5, 0.5 },
			{ 1. / 3, 1. / 3, 1. / 3 } };

	private static double[][] attrac4D = { { 1, 0, 0, 0 }, { 0, 1, 0, 0 },
			{ 0, 0, 1, 0 }, { 0, 0, 0, 1 }, { 0.5, 0.5, 0, 0 },
			{ 0.5, 0, 0.5, 0 }, { 0.5, 0, 0, 0.5 }, { 0, 0.5, 0.5, 0 },
			{ 0, 0.5, 0, 0.5 }, { 0, 0, 0.5, 0.5 },
			{ 1. / 3, 1. / 3, 1. / 3, 0 }, { 1. / 3, 1. / 3, 0, 1. / 3 },
			{ 1. / 3, 0, 1. / 3, 1. / 3 }, { 0, 1. / 3, 1. / 3, 1. / 3 },
			{ .25, .25, .25, .25 } };

	private DistanceMeasurePlugin distance;

	private PointList<? extends DataPoint> points;

	private int dim;

	private double numberOfPoints;

	private double[][] attractors;

	private double[] countMainAttractors;

	private double[] countAttractors;

	// private boolean flipDecision = true;

	private Random rand;

	/**
	 * @param distance
	 */
	public SimpleAttractorComputer(DistanceMeasurePlugin distance) {
		this.distance = distance;

		this.rand = new Random();
		this.rand.setSeed(0);
	}

	public void setDistance(DistanceMeasurePlugin distance) {
		this.distance = distance;
	}

	public void transform(PointList<? extends DataPoint> points) {
		// System.out.print("Performing the computation of the attractors using "+this.distance.toString());
		this.points = points;

		this.dim = this.points.getDimension();

		this.numberOfPoints = this.points.size();

		switch (this.dim) {

		case 2:
			this.attractors = SimpleAttractorComputer.attrac2D;
			break;

		case 3:
			this.attractors = SimpleAttractorComputer.attrac3D;
			break;

		case 4:
			this.attractors = SimpleAttractorComputer.attrac4D;
			break;

		}

		this.countMainAttractors = new double[this.dim];
		Arrays.fill(this.countMainAttractors, 0);

		this.countAttractors = new double[this.attractors.length];
		Arrays.fill(this.countAttractors, 0);

		computeMainAttractor();

		computeAttractors();
		// System.out.println("...DONE!\n");
	}

	private void computeMainAttractor() {
		double[] tmpCoordinates;

		double[] tmpDistances = new double[this.dim];

		Iterator<? extends DataPoint> pointIter = this.points.iterator();

		DataPoint currentPoint;

		for (; pointIter.hasNext();) {

			currentPoint = pointIter.next();

			tmpCoordinates = currentPoint.getCoordinates();

			// double minDistance =
			// this.distance.computeDistance(tmpCoordinates,
			// this.attractors[0]);
			//			 
			//			
			// int minIndex = 0;
			//
			// for (int i = 1; i < this.dim; ++i) {
			//
			// double currentDistance =
			// this.distance.computeDistance(tmpCoordinates,
			// this.attractors[i]);
			//				
			// if (currentDistance < minDistance) {
			//				
			// minDistance = currentDistance;
			// minIndex = i;
			//
			// }
			// else if (currentDistance == minDistance) {
			//					
			// if (this.flipDecision) {
			//					
			// minDistance = currentDistance;
			// minIndex = i;
			//						
			// }
			//					
			// /* flip decision flag
			// */
			// this.flipDecision ^= true;
			//
			// }
			//
			// }

			for (int i = 0; i < this.dim; ++i) {
				// System.err.println("Coordinates "+Array.arrayToString(tmpCoordinates));
				tmpDistances[i] = this.distance.getDistance(tmpCoordinates,	this.attractors[i]);
				// System.err.println("distance "+tmpDistances[i]);
			}

			int minIndex = minIndex(tmpDistances);

			/*
			 * set the ID of the Attractor with the minimal distance
			 */
			currentPoint.setMainAttractorID(minIndex);

			/*
			 * increment the counter for this point
			 */
			(this.countMainAttractors[minIndex])++;
		}

		/*
		 * normalize frequencies
		 */
		Array.divide(this.countMainAttractors, this.numberOfPoints);
		// for (int i = 0; i < this.countMainAttractors.length; ++i) {
		//			
		// this.countMainAttractors[i] /= (float)this.numberOfPoints;
		//	
		// }

		/*
		 * set the main a. f.
		 */
		this.points.setMainAttractorFreqs(this.countMainAttractors);
	}

	private void computeAttractors() {

		double[] tmpCoordinates;

		double[] tmpDistances = new double[this.attractors.length];

		Iterator<? extends DataPoint> pointIter = this.points.iterator();

		DataPoint currentPoint;

		for (; pointIter.hasNext();) {

			currentPoint = pointIter.next();

			tmpCoordinates = currentPoint.getCoordinates();

			// double minDistance =
			// this.distance.computeDistance(tmpCoordinates,
			// this.attractors[0]);
			//
			// int minIndex = 0;
			//
			// for (int i = 1; i < this.attractors.length; ++i) {
			//
			// double currentDistance =
			// this.distance.computeDistance(tmpCoordinates,
			// this.attractors[i]);
			//
			// if (currentDistance < minDistance) {
			//
			// minDistance = currentDistance;
			// minIndex = i;
			//
			// }
			//
			// }

			for (int i = 0; i < this.attractors.length; ++i) {
				tmpDistances[i] = this.distance.getDistance(tmpCoordinates,	this.attractors[i]);
			}

			int minIndex = minIndex(tmpDistances);

			/*
			 * set the ID of the Attractor with the minimal distance
			 */
			currentPoint.setAllAttractorID(minIndex);

			/*
			 * increment the counter of the appropriate attractor
			 */
			(this.countAttractors[minIndex])++;
		}

		/*
		 * normalize attractor frequencies
		 */
		Array.divide(this.countAttractors, this.numberOfPoints);
		// for (int i = 0; i < this.countAttractors.length; ++i)
		// this.countAttractors[i] /= (float)this.numberOfPoints;

		/*
		 * set the a. f.
		 */
		this.points.setAttractorFreqs(this.countAttractors);
	}

	private int minIndex(double[] input) {
		if (input.length < 1) {
			return -1;
		}
		TIntArrayList tmpIndex = new TIntArrayList();

		/*
		 * fin minimum in array
		 */
		double min = input[0];

		for (int i = 0; i < input.length; ++i) {
			if (input[i] < min) {
				min = input[i];
			}
		}

		for (int i = 0; i < input.length; ++i) {
			if (input[i] == min) {
				tmpIndex.add(i);
			}
		}

		/*
		 * return random value of indices with a minimum value
		 */
		return tmpIndex.get(rand.nextInt(tmpIndex.size()));
	}
}
