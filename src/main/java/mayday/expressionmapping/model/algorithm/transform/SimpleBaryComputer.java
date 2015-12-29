/**
 * 
 */
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
public class SimpleBaryComputer implements TransformAlgorithm {


	private PointList<? extends DataPoint> points;

	private double[] centerOfMass;

	private int numberOfPoints;

	private int dim;


	public SimpleBaryComputer()  {

		this.centerOfMass = null;

	}

	
	public void transform(PointList<? extends DataPoint> points) {

		this.points = points;

		this.dim = points.getDimension();

		this.numberOfPoints = points.size();

		this.centerOfMass = new double[this.dim];
		Arrays.fill(this.centerOfMass, 0);

		System.out.print
		("Performing computation of baryentric Coordinates based on expression values from "+this.numberOfPoints+" probes with dimension "+this.dim);

		computeCoordinates();

		setCenter();
		
		System.out.println("...DONE!\n");


	}

	private void computeCoordinates()  {

		Iterator<? extends DataPoint> iter = this.points.iterator();

		DataPoint currentPoint;

		while (iter.hasNext())  {

			currentPoint = iter.next();

			double sum = 0;

			double[] tmpValues = currentPoint.getValues();

			/* calculate first the sum over all values
			 */
			for (int i = 0; i < tmpValues.length; ++i)  {


				/* If negative expression values occur, we take  0,
				 * assume, that these values are, according to amount, small (this seems to be a safe
				 * assumption, since negative values occur during subraction  of the background signal
				 * from the foreground signal).
				 */
				if (tmpValues[i] < 0) {

//					System.err.println
//					("Value "+i+" from probe "+currentPoint.getID()+" is negative. Taking the absolute value!");

					tmpValues[i] = 0;
				}

				sum+= tmpValues[i];


			}

			if (sum > 0)  {

				/* normalize with respect to the sum
				 */
				for (int i = 0; i < tmpValues.length; ++i)
					tmpValues[i] /= sum;


			}

			else  {  

				/* The sum is equal zero (since we add only values >= 0), so we cannot
				 * divide by it. On the other hand we know, that all values have to be zero (therewith equal), 
				 * so all barycentric coordinates have to be 1/dim.
				 */
				Arrays.fill(tmpValues, 1.0/this.dim);

			}

                       /* add this point to the barycenter
			 */
			for (int i = 0; i < this.centerOfMass.length; ++i)
				this.centerOfMass[i] += tmpValues[i];

			/* finnaly we set the barycentric coordinates of the current point
			 */
			currentPoint.setCoordinates(tmpValues);

		}

	}

	private void setCenter()  {

		/* calculate the barycenter by dividing the coordinates by the number of data
		 * points
		 */
//                 double tmpSum = 0;
//            
//		for (int i = 0; i < this.centerOfMass.length; ++i)  {
//			this.centerOfMass[i] /= this.numberOfPoints;
//                        tmpSum += this.centerOfMass[i];
//                }
//                 
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
