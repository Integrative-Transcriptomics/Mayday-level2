package mayday.expressionmapping.model.geometry;

import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;

/**
 * @author Stephan Gade
 *
 */
public interface ClusterPoint extends DataPoint {
    
    //information methods
    int getNumberofMembers();
    
    //get mehtods
    public int[] getMemberList();
    public String memberListToString();
    
    
    //set methods 
    
    /**
     * Set the values array of the point by creating copy of the passed array.
     * 
     * @param values
     * @throws IllegalArgumentException if the length of the given values array doesn't match the dimension of the point.
     */
    void setValues(double[] values);
    
    void setNewMemberList(TIntArrayList memberPoints);
    
    boolean addMembertoCluster(int memberID);
    
    boolean delMemberfromCluster(int memberID);
}
