
package mayday.expressionmapping.model.algorithm.transform;

import java.util.Arrays;
import java.util.Iterator;

import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.DataPoint2D;
import mayday.expressionmapping.model.geometry.DataPoint3D;
import mayday.expressionmapping.model.geometry.DataPoint4D;
import mayday.expressionmapping.model.geometry.container.PointList;



/**
 * @author Stephan Gade
 *
 */
public class FoldchangeBaryComputer implements TransformAlgorithm {


	private PointList<? extends DataPoint> points;

	private double[] centerOfMass;

	private int numberOfPoints;

	private int dim;


	public FoldchangeBaryComputer()  {

		this.centerOfMass = null;

	}


	public void transform(PointList<? extends DataPoint> points) {

		this.points = points;

		this.dim = points.getDimension();

		this.numberOfPoints = points.size();

		this.centerOfMass = new double[this.dim];
		Arrays.fill(this.centerOfMass, 0);

		System.out.print
		("Performing computation (foldchange) of baryentric Coordinates based on expression values from "+this.numberOfPoints+" probes with dimension "+this.dim);

		computeCoordinates();

		setCenter();

		System.out.println("  DONE!\n");


	}

	private void computeCoordinates()  {

		Iterator<? extends DataPoint> iter = points.iterator();

		DataPoint currentPoint;

		while (iter.hasNext())  {

			currentPoint = iter.next();

			double sum = 0;

			double[] tmpValues = currentPoint.getValues();

			/* At first we compute 2^(foldchange) to compute positive values, we need to
			 * compute the barycentric coordinates.
			 * At a second step we compute the sum, we divide later by.
			 */
			for (int i = 0; i < tmpValues.length; ++i)  {

				tmpValues[i] = Math.pow(2, tmpValues[i]);

				sum+= tmpValues[i];


			}

			/* normalize with respect to the sum
			 */
			for (int i = 0; i < tmpValues.length; ++i)
				tmpValues[i] /= sum;

			/* add this point to the barycenter
			 */
			for (int i = 0; i < this.centerOfMass.length; ++i)
				this.centerOfMass[i] += tmpValues[i];

			/* finally we set the barycentric coordinates of the current point
			 */
			currentPoint.setCoordinates(tmpValues);

		}


	}

	private void setCenter()  {

		/* calculate the barycenter by dividing the coordinates by the number of data
		 * points
		 */
		for (int i = 0; i < this.centerOfMass.length; ++i)
			centerOfMass[i] /= this.numberOfPoints;

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

