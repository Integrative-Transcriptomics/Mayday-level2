/**
 * 
 */
package mayday.expressionmapping.model.geometry;

/**
 * @author Stephan Gade
 *
 */
public class ClusterPoint3D extends ClusterPointBase implements ClusterPoint {


    public ClusterPoint3D(int ID)  {

	this.ID = ID;

	this.dim = 3;

	initializeValues();
	initializeCoordinates();
	initializeAttractorIDs();
	initializeMemberList();

    }


}
