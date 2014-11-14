/**
 * 
 */
package mayday.expressionmapping.model.geometry;

/**
 * @author Stephan Gade
 *
 */
public class DataPoint2D extends DataPointBase implements DataPoint {
    
    
    public DataPoint2D(int ID)  {

        this.ID = ID;
       
	this.dim = 2;

	initializeValues();
	initializeCoordinates();
        initializeAttractorIDs();
        
    }
    
    
    public DataPoint2D(int ID, double[] values)  {
	
	this.ID = ID;
	
	this.dim = 2;
	
	if (values.length != this.dim)
	    throw new IllegalArgumentException
	    		("Double Array values in constructor Point3D should have length: "+dim+", and has: "+values.length);
	
	//the array will be copied, since we cannot garantee values is immutable
	//clone implements only a flat copy, since we use double array, this fits our needs
	this.values = (double[])values.clone();
	
	initializeCoordinates();
	initializeSigns();
	initializeAttractorIDs();
	
	
    }
    
    public DataPoint2D (int ID, double x, double y)  {
	
	this.ID = ID;
	
	this.dim = 2;
	
	this.values = new double[this.dim];
	
	this.values[0] = x;
	this.values[1] = y;
	
	initializeCoordinates();
	initializeSigns();
	initializeAttractorIDs();
	
	
    }

}
