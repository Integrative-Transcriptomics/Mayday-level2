package mayday.vis3d.primitives;

/**
 * Represents a Point in 3-dimensional space
 * @author G\u00FCnter J\u00E4ger
 */
public class Point3D extends Vector3D {

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public Point3D(double x, double y, double z) {
		super(x, y, z);
	}

	/**
	 * default constructor
	 */
	public Point3D() {
		super();
	}
	
	/**
	 * @param p
	 */
	public Point3D(Point3D p) {
		super(p);
	}
}
