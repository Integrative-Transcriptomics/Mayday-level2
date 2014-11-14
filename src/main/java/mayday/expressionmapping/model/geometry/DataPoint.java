/**
 * 
 */
package mayday.expressionmapping.model.geometry;

/**
 * @author Stephan Gade
 *
 */
public interface DataPoint extends Point {
    
    
    /**
     * Returns a copy of the array holding our values. Due to the fact that this array
     * is initialized from the beginning with an appropriate number of values, the returned
     * arrays is never empty.
     * 
     * @return a copy of the values array.
     */
    double[] getValues();
    
    
    /**
     * Returns the ID of the main attractor (one of the attractors corresponding to the corners of the simplex and therefor representing the groups themselves)
     * this point lies closest to.
     * 
     * @return the ID of the nearest main attractor
     */
    int getMainAttractorID();
    
    /**
     * Returns the ID of the attractor (this time all attractors are considered) this point lies closest to.
     * 
     * @return the ID of the nearest attractor
     */
    int getAllAttractorID();
    
    void setID(int ID);
    
    /**
     * Set the barycentricCoordinates array of the point by creating a copy of the passed array.
     * 
     * @param barycentricCoordinates
     * 
     * @throws IllegalArgumentException if the length of the given coordinates array doesn't match the dimension of the point.
     */
    void setCoordinates(double[] barycentricCoordinates);
    
    
    void setMainAttractorID(int id);
    
    void setAllAttractorID(int id);
    
    
    
    
    

}
