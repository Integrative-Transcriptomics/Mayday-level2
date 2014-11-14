package mayday.expressionmapping.model.algorithm.transform;

import java.util.Arrays;

import mayday.expressionmapping.controller.Constants;
import mayday.expressionmapping.model.algorithm.AlgorithmFactory;
import mayday.expressionmapping.model.algorithm.sort.SortAlgorithm;
import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.DataPoint2D;
import mayday.expressionmapping.model.geometry.DataPoint3D;
import mayday.expressionmapping.model.geometry.DataPoint4D;
import mayday.expressionmapping.model.geometry.container.PointList;
import mayday.expressionmapping.utils.Array;

/**
 *
 * @author Stephan Gade
 */
public class RankBaryComputer implements TransformAlgorithm{
	
	
	private PointList<? extends DataPoint> points;

	private double[] centerOfMass;

	private int numberOfPoints;

	private int dim;


	public RankBaryComputer()  {

		this.centerOfMass = null;

	}


	public void transform(PointList<? extends DataPoint> points) {

		this.points = points;

		this.dim = points.getDimension();

		this.numberOfPoints = points.size();

		this.centerOfMass = new double[this.dim];
		Arrays.fill(this.centerOfMass, 0);

		System.out.print
		("Performing computation (ranked) of baryentric Coordinates based on expression values from "+this.numberOfPoints+" probes with dimension "+this.dim);

		computeCoordinates();

		setCenter();

		System.out.println("...DONE!\n");


	}

	private void computeCoordinates()  {

		SortAlgorithm sort = AlgorithmFactory.getSortAlgorithm(Constants.QSORT);
		
		int[] index;
		
		double[][] tmpCoords = new double[this.numberOfPoints][this.dim];
		
		/* each value (colums) has to be ranked seperatet
		 */
		for (int i = 0; i < this.dim; ++i)  {
			
			/* rank the i-th column
			 */
			index = sort.sortAsc(this.points, SortAlgorithm.VALUES, i);
			
			/* fill the i-th column of the temporary coordinates
			 */
			for (int j = 0; j < index.length; ++j) {
				
				tmpCoords[index[j]][i] = j+1;
				
			}
			
		}

		DataPoint currentPoint;

		/* compute barycentric coordinates from the ranks
		 * and set the coordinates of each point
		 */
		for (int i = 0; i < this.numberOfPoints; ++i)  {

			currentPoint = this.points.get(i);

			
			/* compute the sum of the ranks of the i-th probe
			 */
			double sum = Array.sum(tmpCoords[i]);
			
			/* normalize with respect to the sum
			 */
			Array.divide(tmpCoords[i], sum);

			/* add this point to the barycenter
			 */
			Array.add(this.centerOfMass,tmpCoords[i]);
			
			
			/* finally we set the barycentric coordinates of the current point
			 */
			currentPoint.setCoordinates(tmpCoords[i]);
			
			//System.err.println("Values: "+Array.arrayToString(currentPoint.getValues()));
			//System.err.println("Coordinates: "+currentPoint.coordinatesToString());

		}
		


	}

	private void setCenter()  {

		/* calculate the barycenter by dividing the coordinates by the number of data
		 * points
		 */
//                 double tmpSum = 0;
            
//		for (int i = 0; i < this.centerOfMass.length; ++i)  {
//			this.centerOfMass[i] /= this.numberOfPoints;
//                        tmpSum += this.centerOfMass[i];
//                }
                 
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
		
               
                this.points.setCenterOfMass(center);

	}

	

}
