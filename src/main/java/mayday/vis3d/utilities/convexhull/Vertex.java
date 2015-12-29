package mayday.vis3d.utilities.convexhull;

import mayday.vis3d.primitives.Point3D;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class Vertex {

	/**
	 * 
	 */
	public Point3D p;
	/**
	 * 
	 */
	public int index;
	/**
	 * 
	 */
	public Vertex prev;
	/**
	 * 
	 */
	public Vertex next;
	/**
	 * 
	 */
	public Face face;
	
	/**
	 * 
	 */
	public Vertex() {
		this.p = new Point3D();
	}
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param index
	 */
	public Vertex(double x, double y, double z, int index) {
		this.p = new Point3D(x, y, z);
		this.index = index;
	}
}
