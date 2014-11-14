/**
 * 
 */
package mayday.expressionmapping.model.geometry;

/**
 * @author Stephan Gade
 *
 */
public class ClusterPoint4D extends ClusterPointBase implements ClusterPoint {


    public ClusterPoint4D(int ID)  {

	this.ID = ID;

	this.dim = 4;

	initializeValues();
	initializeCoordinates();
	initializeAttractorIDs();
	initializeMemberList();

    }

}
