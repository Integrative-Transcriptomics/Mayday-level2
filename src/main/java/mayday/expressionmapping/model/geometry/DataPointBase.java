package mayday.expressionmapping.model.geometry;


/**
 * A abstract base class for our Point classes holding the (expression)-values
 * and their corresponding barycentric coordinates.
 * 
 * @author Stephan Gade
 *
 */
public abstract class DataPointBase {


    /// DATA Section
    protected int dim;
    protected int ID;
    protected double[] values;
    protected double[] barycentricCoordinates;
    protected boolean[] barycentricSigns;
    protected int mainAttractorID;
    protected int allAttractorID;


    ///METHOD Section
    protected void initializeCoordinates() {

        this.barycentricCoordinates = new double[0]; /* initialize barycentricCoordinates with an empty array */

    }

    /* for the case of the center of gravity, a data point that have no values
     */
    protected void initializeValues() {

        /* Initialize the values array with an empty array
         */
        this.values = new double[0];

    }

    protected void initializeSigns() {

        this.barycentricSigns = new boolean[0];

    }

    protected void initializeAttractorIDs() {

        /* initialie the IDs from both attractors with a negative value */
        this.mainAttractorID = -1;
        this.allAttractorID = -1;

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

    public boolean areCoordinatesSet() {

        return ((this.barycentricCoordinates.length > 0) ? true : false);

    }

    public int compareTo(Point b) {

        double[] tmpB = b.getCoordinates();

        if (this.barycentricCoordinates.length < tmpB.length) {
            return -1;
        }
        if (this.barycentricCoordinates.length > tmpB.length) {
            return 1;
        }
        for (int i = 0; i < this.barycentricCoordinates.length; ++i) {

            if (this.barycentricCoordinates[i] < tmpB[i]) {
                return -1;
            }
            if (this.barycentricCoordinates[i] > tmpB[i]) {
                return 1;
            }
        }

        return 0;

    }

    public double[] getValues() {

        double[] ret = (double[]) this.values.clone();  /* We use the overloaded clone() method
        of the array. This returns only a flat copy, but
        since both of our arrays contain primitives, this fits our needs.
        We cannot return a direct reference to our arrays, for the simple reason that in this case the arrays were no longer immutable from outside the class.*/

        return ret;

    }

    public double[] getCoordinates() {

        double ret[] = (double[]) this.barycentricCoordinates.clone();

        return ret;

    }

    public String coordinatesToString() {

        StringBuffer ret = new StringBuffer();

        ret.append('(');

        for (int i = 0; i < this.dim - 1; ++i) {

            ret.append(this.barycentricCoordinates[i]);

            ret.append(" ,");

        }

        /* append last value * and the closing }   */
        ret.append(this.barycentricCoordinates[this.dim - 1]);
        ret.append(')');

        return ret.toString();

    }

    public void setID(int ID) {

        this.ID = ID;

    }

    public void setCoordinates(double[] barycentricCoordinates) {

        if (barycentricCoordinates.length != dim) {
            throw new IllegalArgumentException("Double array in method setCoordinates should have length: " + dim + ", and has: " + barycentricCoordinates.length);
        }
        this.barycentricCoordinates = (double[]) barycentricCoordinates.clone();

    }

    public void setAllAttractorID(int ID) {

        this.allAttractorID = ID;

    }

    public void setMainAttractorID(int ID) {

        this.mainAttractorID = ID;

    }
}

