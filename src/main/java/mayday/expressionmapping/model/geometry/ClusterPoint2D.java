package mayday.expressionmapping.model.geometry;

/**
 * @author Stephan Gade
 *
 */
public class ClusterPoint2D extends ClusterPointBase implements ClusterPoint {

    /**
     * @param ID
     */
    public ClusterPoint2D(int ID)  {
		this.ID = ID;
		this.dim = 2;
		
		initializeValues();
		initializeCoordinates();
		initializeAttractorIDs();
		initializeMemberList();
    }
}
