package mayday.expressionmapping.model.geometry;

import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;

/**
 * @author Stephan Gade
 * 
 */
public abstract class ClusterPointBase extends DataPointBase {

	// DATA Section
	protected int dim;
	protected int ID;
	protected double[] values;
	protected double[] barycentricCoordinates;
	protected boolean[] barycentricSigns;
	protected int mainAttractorID;
	protected int allAttractorID;
	TIntArrayList memberPoints;

	// /METHOD Section

	protected void initializeValues() {
		/*
		 * Initialize the values array with an empty array
		 */
		this.values = new double[0];
	}

	protected void initializeCoordinates() {
		this.barycentricCoordinates = new double[0]; 
		/*
		 * initialize barycentricCoordinates
		 * with an empty array
		 */
	}

	protected void initializeAttractorIDs() {

		/* initialie the IDs from both attractors with a negative value */
		this.mainAttractorID = -1;
		this.allAttractorID = -1;

	}

	protected void initializeMemberList() {
		this.memberPoints = new TIntArrayList();
	}

	public int getID() {
		return this.ID;
	}

	public int getDimension() {
		return this.dim;
	}

	public int getMainAttractorID() {
		return this.mainAttractorID;
	}

	public int getAllAttractorID() {
		return this.allAttractorID;
	}

	public int getNumberofMembers() {
		return this.memberPoints.size();
	}

	public boolean areValuesSet() {
		return ((this.values.length > 0) ? true : false);
	}

	public boolean areCoordinatesSet() {
		return ((this.barycentricCoordinates.length > 0) ? true : false);
	}

	public int compareTo(Point b) {
		double[] tmpB = b.getCoordinates();
		int index = Math.min(this.barycentricCoordinates.length, tmpB.length);
		
		for (int i = 0; i < index; ++i) {
			if (this.barycentricCoordinates[i] < tmpB[i])
				return -1;
			if (this.barycentricCoordinates[i] > tmpB[i])
				return 1;
		}
		return 0;
	}

	public double[] getValues() {
		return (double[]) this.values.clone(); 
		/*
		 * We use the overloaded clone() method of the array. This returns
		 * only a flat copy, but since both of our arrays contain
		 * primitives, this fits our needs. We cannot return a direct
		 * reference to our arrays, for the simple reason that in
		 * this case the arrays were no longer immutable from
		 * outside the class.
		 */
	}

	public double[] getCoordinates() {
		return (double[]) this.barycentricCoordinates.clone();
	}

	public int[] getMemberList() {
		return this.memberPoints.toNativeArray();
	}

	public String memberListToString() {
		StringBuffer ret = new StringBuffer();
		ret.append('{');

		int bounds = this.memberPoints.size();
		
		for (int i = 0; i < bounds - 1; ++i) {
			ret.append(this.memberPoints.get(i));
			ret.append(" ,");
		}

		/* append last value * and the closing } */
		ret.append(this.memberPoints.get(bounds - 1));
		ret.append('}');

		return ret.toString();
	}

	public void setValues(double[] values) {
		if (values.length != this.dim)
			throw new IllegalArgumentException(
					"Double array values in method setValues should have length: "
							+ dim + ", and has: " + values.length);
		this.values = (double[]) values.clone();
	}

	public void setCoordinates(double[] barycentricCoordinates) {
		if (barycentricCoordinates.length != dim)
			throw new IllegalArgumentException(
					"Double array in method setCoordinates should have length: "
							+ dim + ", and has: "
							+ barycentricCoordinates.length);
		this.barycentricCoordinates = (double[]) barycentricCoordinates.clone();
	}

	public void setAllAttractorID(int ID) {
		this.allAttractorID = ID;
	}

	public void setMainAttractorID(int ID) {
		this.mainAttractorID = ID;
	}

	public void setNewMemberList(TIntArrayList memberPoints) {
		this.memberPoints = (TIntArrayList) memberPoints.clone();
	}

	public boolean addMembertoCluster(int memberID) {
		int index = this.memberPoints.indexOf(memberID);
		
		 //does the member already exist in the list ?
		if (index < 0) {
			 //if not, add it
			this.memberPoints.add(memberID);
			return true;
		} else
			return false;
	}

	public boolean delMemberfromCluster(int memberID) {
		int index = this.memberPoints.indexOf(memberID);
		if (index >= 0) {
			this.memberPoints.remove(index);
			return true;
		} else
			return false;
	}

	public String coordinatesToString() {
		StringBuffer ret = new StringBuffer();
		ret.append('{');

		for (int i = 0; i < this.dim - 1; ++i) {
			ret.append(this.barycentricCoordinates[i]);
			ret.append(" ,");
		}

		/* append last value * and the closing } */
		ret.append(this.barycentricCoordinates[this.dim - 1]);
		ret.append('}');

		return ret.toString();
	}
}
