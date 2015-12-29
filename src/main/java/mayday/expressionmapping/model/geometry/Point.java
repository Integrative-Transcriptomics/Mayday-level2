/**
 * 
 */
package mayday.expressionmapping.model.geometry;

/**
 * @author Stephan Gade
 *
 */

public interface Point extends Comparable<Point>{
    
    
     //Information methods
    
    /**
     * Returns the ID (Identifier) of this point
     * 
     * @return the ID of this point
     */
    int getID();
    
    /**
     * Returns the dimension of the point and thus the number of values and coordinates.
     * 
     * @return the dimension of this point	
     */
    int getDimension();
    
    
    /**
     * Returns a boolean value, which indicates wether the barycentric coordinates are already
     * set or not. In the first case the field barycentricCoordinates has a length > 1
     * (in particular the dimension of the point), otherwise zero.
     * 
     * @return a boolean value indicating the state of barycentricCoordinates
     */
    boolean areCoordinatesSet();
    
    
    //retrieving the values and coordinates (copy)
    
   
    
    /**
     * Returns a copy of the coordinates array (baryCentricCoordinates). Since it it possible
     * that this values aren't computed at this time, this array can be empty.
     * 
     * @see #areCoordinatesSet()
     * 
     * @return a copy of the baryCentricCoordinates array
     */
    double[] getCoordinates();
    
    
    String coordinatesToString();
    
    
   
    
    
  

}
