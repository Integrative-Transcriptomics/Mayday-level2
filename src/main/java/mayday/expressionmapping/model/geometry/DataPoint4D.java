/**
 * 
 */
package mayday.expressionmapping.model.geometry;

/**
 * @author Stephan Gade
 *
 */
public class DataPoint4D extends DataPointBase implements DataPoint {
    
    
    public DataPoint4D(int ID)  {

        this.ID = ID;

	this.dim = 4;

	initializeValues();
	initializeCoordinates();
        initializeAttractorIDs();
        
    }
    
    
    public DataPoint4D(int ID, double[] values)  {
	
	this.ID = ID;
	
	this.dim = 4;
	
	if (values.length != this.dim)
	    throw new IllegalArgumentException
	    		("Double Array values in constructor Point3D should have length: "+dim+", and has: "+values.length);
	
	//the array will be copied, since we cannot garantee values is immutable
	//clone implements only a flat copy, since we use double array, this fits our needs
	this.values = (double[])values.clone();
	
	initializeCoordinates();
	initializeAttractorIDs();
	
	
    }
    
    public DataPoint4D(int ID, double w, double x, double y, double z)  {
	
	this.ID = ID;
	
	this.dim = 4;
	
	this.values = new double[this.dim];
	
	this.values[0] = w;
	this.values[1] = x;
	this.values[2] = y;
	this.values[3] = z;
	
    }

}
