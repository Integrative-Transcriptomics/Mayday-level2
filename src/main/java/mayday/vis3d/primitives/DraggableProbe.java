package mayday.vis3d.primitives;

import mayday.core.Probe;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class DraggableProbe implements Comparable<String>, CharSequence {
	private double x, y;
	public boolean freePlaced;
	
	private Probe pb;
	
	/**
	 * @param pb
	 */
	public DraggableProbe(Probe pb) {
		this(pb, 0, 0);
	}
	
	/**
	 * @param pb
	 * @param x
	 * @param y
	 */
	public DraggableProbe(Probe pb, double x, double y) {
		this(pb, x, y, false);
	}
	
	/**
	 * @param pb
	 * @param x
	 * @param y
	 * @param freePlaced
	 */
	public DraggableProbe(Probe pb, double x, double y, boolean freePlaced) {
		this.pb = pb;
		this.x = x;
		this.y = y;
		this.freePlaced = freePlaced;
	}
	
	/**
	 * @param x
	 */
	public void setX(double x) {
		this.x = x;
	}
	
	/**
	 * @param y
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * @return x
	 */
	public double getX() {
		return this.x;
	}
	
	/**
	 * @return y
	 */
	public double getY() {
		return this.y;
	}
	
	/**
	 * @return true is string is free placed
	 */
	public boolean freePlaced() {
		return this.freePlaced;
	}
	
	/**
	 * toggle free placed
	 */
	public void setFreePlaced() {
		this.freePlaced = this.freePlaced ? false : true;
	}
	
	/**
	 * remove free placed
	 */
	public void removeFreePlaced() {
		this.freePlaced = false;
	}
	
	/**
	 * @return probe
	 */
	public Probe getProbe() {
		return this.pb;
	}
	
	/**
	 * @param pb
	 */
	public void setProbe(Probe pb) {
		this.pb = pb;
	}
	
	/**
	 * @return display name of this probe
	 */
	public String getProbeName() {
		return this.pb.getDisplayName();
	}
	
	@Override
	public String toString() {
		return this.pb.getDisplayName().toString();
	}

	@Override
	public int compareTo(String o) {
		return this.pb.getDisplayName().compareTo(o);
	}

	@Override
	public char charAt(int index) {
		return this.pb.getDisplayName().charAt(index);
	}

	@Override
	public int length() {
		return this.pb.getDisplayName().length();
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return this.pb.getDisplayName().subSequence(start, end);
	}
	
	/**
	 * @param start
	 * @param end
	 * @return substring from index start to index end
	 */
	public String substring(int start, int end) {
		return this.pb.getDisplayName().substring(start, end);
	}
}
