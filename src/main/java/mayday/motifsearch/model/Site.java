package mayday.motifsearch.model;

/**
 * Class representing a found site/bounding box/occurrence 
 * of a motif on a sequence
 * 
 */
public class Site implements Cloneable
{
    protected int position;
    protected double significanceValue;
    protected Motif motif;



    public Site(Motif motif, int position) {
	super();
	this.motif = motif;
	this.position = position;
	/*if no significance is set, the site inherits the motifs significance value*/
	this.significanceValue = motif.getSignificanceValue();//(significanceValue == Double.MAX_VALUE?motif.getSignificanceValue():significanceValue);
    }


    public final double getSignificanceValue() {
	return this.significanceValue;
    }


    public final void setSignificanceValue(double significanceValue) {
	this.significanceValue = significanceValue;
    }


    public final int getPosition() {
	return position;
    };

    public final Motif getMotif() {
	return motif;
    }


    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((motif == null) ? 0 : motif.hashCode());
	result = prime * result + position;
	long temp;
	temp = Double.doubleToLongBits(significanceValue);
	result = prime * result + (int) (temp ^ (temp >>> 32));
	return result;
    }


    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	final Site other = (Site) obj;
	if (motif == null) {
	    if (other.motif != null)
		return false;
	}
	else if (!motif.equals(other.motif))
	    return false;
	if (position != other.position)
	    return false;
	if (Double.doubleToLongBits(significanceValue) != Double
		.doubleToLongBits(other.significanceValue))
	    return false;
	return true;
    }

    public Object clone() {
	Site clone = new Site(this.motif,this.position);
	clone.setSignificanceValue(this.significanceValue);
	return clone;

    }
}
