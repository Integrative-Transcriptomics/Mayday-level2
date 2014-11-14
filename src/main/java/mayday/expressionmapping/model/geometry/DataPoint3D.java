/**
 * 
 */
package mayday.expressionmapping.model.geometry;


/**
 * @author Stephan Gade
 *
 */
public class DataPoint3D extends DataPointBase implements DataPoint{

    
    /* for the CoG
     */
    public DataPoint3D(int ID)  {

        this.ID = ID;

	this.dim = 3;

	initializeValues();
	initializeCoordinates();
        initializeAttractorIDs();
        
    }
    
    
    // the exception thrown is unchecked, the situation is a programmers bug and isnt cover 
    public DataPoint3D(int ID, double[] values)  {
	
	this.ID = ID;
	
	this.dim = 3;
	
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
    
    
    
    public DataPoint3D (int ID, double x, double y, double z)  {
	
	this.ID = ID;
	
	this.dim = 3;
	
	this.values = new double[this.dim];
	
	this.values[0] = x;
	this.values[1] = y;
	this.values[2] = z;
	
	initializeCoordinates();
	initializeSigns();
	initializeAttractorIDs();
	
	
    }
    
    
 
  
}
