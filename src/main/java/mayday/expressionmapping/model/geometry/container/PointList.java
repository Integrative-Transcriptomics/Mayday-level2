
package mayday.expressionmapping.model.geometry.container;


import java.util.Iterator;
import java.util.List;

import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.Point;

public class PointList<T extends DataPoint>  {

	private List<T> _points;

	private String[] groupLabels;

	private double[] mainAttractorFreqs = null;
	private double[] attractorFreqs = null;

	Point centerOfMass = null;

	private final int size;

	private final int dim;

	/**
	 * Constructor to build a PointList. The points are given in a list.
	 * All points in the list are supposed to have the same dimension.
	 * The validity of this assumption is not explecitly checked in the constructor.
	 * Use the method checkDimensions for this purpose.
	 * @param points a list containing the data points
	 * @param groupLabels list of labels to identify the groups
	 */
	public PointList(List<T> points, List<String> groupLabels)  {

		this._points = points;

		this.size = this._points.size();

		this.dim = points.get(0).getDimension();


		/* setting the group labels
		 */
		this.groupLabels = new String[this.dim];

		if (groupLabels != null && groupLabels.size() == dim)  {

			for (int i = 0; i < this.groupLabels.length; ++i)
				this.groupLabels[i] = groupLabels.get(i);

		}
		else  {

			for (int i = 0; i < this.groupLabels.length; ++i)
				this.groupLabels[i] = new String("Group "+(i+1));

		}

	}

	/**
	 * Constructor to build a PointList. Here the group lables are given as an array
	 * of String.
	 * @param points a list containing the data points.
	 * @param groupLabels array of labels to identify the groups
	 */
	public PointList(List<T> points, String[] groupLabels)  {

		this._points = points;

		this.size = this._points.size();

		this.dim = points.get(0).getDimension();

		this.groupLabels = new String[this.dim];

		if (groupLabels != null && groupLabels.length == dim)  {

			for (int i = 0; i < this.groupLabels.length; ++i)
				this.groupLabels[i] = new String(groupLabels[i]);

		}
		else  {

			for (int i = 0; i < this.groupLabels.length; ++i)
				this.groupLabels[i] = new String("Group "+(i+1));


		}

	}

	/**
	 * Checks the dimension integrety of the contained DataPoint. We require all data points
	 * to have the same dimension.
	 * @return a boolean value, true if alle points have the same dimension false 
	 */
	public boolean checkDimensions() {
		
		/* checking the dimension and the ID's of the points
		 */
		for (int i = 0; i < this.size; ++i)  {
			
			/* check for dimensions of all points
			 */
			T currentPoint = this._points.get(i);
			
			/* check for dimensions of all points
			 */
			if (currentPoint.getDimension() != this.dim) {
				
				return false;
				
			}
			
		}
		
		return true;
	}

	/**
	 * ensures that the ID from every data points correpsonds to its position in the list
	 */
	public void checkIDs() {
		
		for (int i = 0; i < this.size; ++i) {
			
			T currentPoint = this._points.get(i);
			
			/* ensure, that the ID of a point corresponds with its position in the list
			 */
			if (currentPoint.getID() != i)
				currentPoint.setID(i);
			
		}
	}

	/**
	 * Sets the main attractor frequencies. These frequencies describe how many points lie in 
	 * each of the dim main attractors.
	 * @param freq array with the point frequencies of the main attractors. The length of the array has to be equal to the number of main attractors and thus to the dimension of the points.
	 */
	public void setMainAttractorFreqs(double[] freqs)  {

		/* we have dim main attractors
		 */
		if (freqs.length != this.dim)
			throw new IllegalArgumentException("Frequency field has the wrong length: " + freqs.length + ". Should have " + this.dim);

		this.mainAttractorFreqs = freqs.clone();
	
	}

	/**
	 * Set the attractor frequencies. Thes frequencies describe the spreading of the points over the 
	 * attractors of the simplex.
	 * @param freqs array with the point frequencies of the attrator areas. The length of the array has to be equal to 2^dim-1 where dim is the dimension of the points.
	 */
	public void setAttractorFreqs(double[] freqs)  {

		/* we have (2^dim) - 1 attractors
		 */
		if (freqs.length != (int)(Math.pow(2, this.dim) - 1))
			throw new IllegalArgumentException("Frequency field has the wrong length: " + freqs.length + ". Should have " + (int)(Math.pow(2, this.dim) - 1));

		this.attractorFreqs = freqs.clone();

	}

	/**
	 * Sets the center of mass of all points
	 * @param centerOfMass a point describing the center of mass of the points
	 */
	public void setCenterOfMass(Point centerOfMass) {

		if (centerOfMass.getDimension() != this.dim)
			throw new IllegalArgumentException
			("The dimension of center of mass: " + centerOfMass.getDimension() + "does not match the dimension of the points: " + this.dim);

		this.centerOfMass = centerOfMass;

	}
        
	/**
	 * Get the center of mass of the points in this list
	 * @return a point describing the center of gravity of the points
	 */
        public Point getCenterofMass()  {
            
            return this.centerOfMass;
            
        }


	/**
	 * Returns a specific point of the list determined by an index
	 * @param index the index of the desíred point
	 * @return the point on index i
	 */
	public T get(int index) {

		return this._points.get(index);


	}

	/**
	 * Returns the dimension of the point and therewith the number of groups.
	 * @return the dimension of the points
	 */
	public int getDimension() {

		return this.dim;

	}

	/**
	 * Checks if the point frequencies of the main attractors are set
	 * @return a boolean value indicatin if the main attractor frequencies are set
	 */
	public boolean areMainAttractorFreqsSet() {

		return (this.mainAttractorFreqs != null);

	}

	public boolean areAttractorFreqsSet()  {

		return (this.attractorFreqs != null);

	}

	public double[] getMainAttractorFreqs()  {

		/* we can use the clone function, since we are dealing with fields of primitives,
		 * and since we have a function to determine the state of this field, we can pass null, 
		 * if it is not set
		 */
		if (this.mainAttractorFreqs != null)
			return this.mainAttractorFreqs.clone();

		else
			return null;


	}

	public double[] getAttractorFreqs()  {

		/* (see above)
		 */
		if (this.mainAttractorFreqs != null)
			return this.attractorFreqs.clone();

		else
			return null;


	}

	public String[] getGroupLabels()  {

		/* constructing a deep copy of our label array
		 */
		String[] ret = new String[this.groupLabels.length];

		for (int i = 0; i < ret.length; ++i)  
			ret[i] = new String(this.groupLabels[i]);

		/* this would make a flat copy
		 *
		 * ret = this.groupLabels.clone();
		 */
		return ret;

	}

	public Iterator<T> iterator() {

		return this._points.iterator();

	}

	public int size() {

		return this.size;

	}
	
	/**
	 * Returns an access list for a given main attratctor
	 * 
	 * @param id the ID of the main attractor the points belong
	 * @return an index list for accessing points lying in the specified attractor
	 */
	public TIntArrayList getMainAttractorPoints (int id)  {
		
		if (id < 0 || id > (this.dim - 1))
			throw new IllegalArgumentException("Parameter id doesn't match a main attractor: "+id);
		
		TIntArrayList ret = new TIntArrayList();
		
		for (int i = 0; i < this.size; ++i)  {
			
			if (this._points.get(i).getMainAttractorID() == id)
				ret.add(i);
		}
		
		return ret;
		
	}
	
	public TIntArrayList getAttractorPoints (int id)  {
		
		if (id < 0 || id > (int)(Math.pow(2,this.dim) - 2))
			throw new IllegalArgumentException("Parameter id doesn't match an attractor: "+id);
		
		TIntArrayList ret = new TIntArrayList();
		
		for (int i = 0; i < this.size; ++i)  {
			
			if (this._points.get(i).getAllAttractorID() == id)
				ret.add(i);
		}
		
		return ret;
		
	}



}