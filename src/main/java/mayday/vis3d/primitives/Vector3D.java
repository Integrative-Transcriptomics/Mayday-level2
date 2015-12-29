package mayday.vis3d.primitives;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class Vector3D implements Cloneable {

	/**
	 * x-value
	 */
	public double x = 0;
	/**
	 * y-value
	 */
	public double y = 0;
	/**
	 * z-value
	 */
	public double z = 0;
	static private final double DOUBLE_PREC = 2.2204460492503131e-16;

	/**
	 * default constructor
	 */
	public Vector3D() {}

	/**
	 * @param v
	 */
	public Vector3D(Vector3D v) {
		this.set(v);
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vector3D(double x, double y, double z) {
		this.set(x, y, z);
	}

	/**
	 * @param v
	 */
	public void set(Vector3D v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * @param i , should be one of {0, 1, 2}
	 * @return chosen value
	 */
	public double get(int i) {
		switch (i) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		default:
			throw new ArrayIndexOutOfBoundsException(i);
		}
	}

	/**
	 * @param i , should be one of {0, 1, 2}
	 * @param d
	 */
	public void set(int i, double d) {
		switch (i) {
		case 0:
			this.x = d;
			break;
		case 1:
			this.y = d;
			break;
		case 2:
			this.z = d;
			break;
		default:
			throw new ArrayIndexOutOfBoundsException(i);
		}
	}

	/**
	 * @param v1
	 * @param v2
	 */
	public void add(Vector3D v1, Vector3D v2) {
		this.x = v1.x + v2.x;
		this.y = v1.y + v2.y;
		this.z = v1.z + v2.z;
	}

	/**
	 * @param v1
	 */
	public void add(Vector3D v1) {
		this.x += v1.x;
		this.y += v1.y;
		this.z += v1.z;
	}

	/**
	 * @param v1
	 * @param v2
	 */
	public void sub(Vector3D v1, Vector3D v2) {
		this.x = v1.x - v2.x;
		this.y = v1.y - v2.y;
		this.z = v1.z - v2.z;
	}

	/**
	 * @param v1
	 */
	public void sub(Vector3D v1) {
		this.x -= v1.x;
		this.y -= v1.y;
		this.z -= v1.z;
	}

	/**
	 * @param s
	 */
	public void scale(double s) {
		this.x = s * this.x;
		this.y = s * this.y;
		this.z = s * this.z;
	}

	/**
	 * @param s
	 * @param v1
	 */
	public void scale(double s, Vector3D v1) {
		this.x = s * v1.x;
		this.y = s * v1.y;
		this.z = s * v1.z;
	}

	/**
	 * @param s
	 * @param v
	 * @return scaled vector
	 */
	public Vector3D scaleV(double s, Vector3D v) {
		return new Vector3D(s * v.x, s * v.y, s * v.z);
	}

	/**
	 * @return length of the vector
	 */
	public double norm() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * @return squared length of the vector
	 */
	public double normSquared() {
		return x * x + y * y + z * z;
	}

	/**
	 * normalization
	 */
	public void normalize() {
		double lenSqr = x * x + y * y + z * z;
		double err = lenSqr - 1;
		if (err > (2 * DOUBLE_PREC) || err < -(2 * DOUBLE_PREC)) {
			double len = Math.sqrt(lenSqr);
			x /= len;
			y /= len;
			z /= len;
		}
	}

	/**
	 * @param v
	 * @return distance to v
	 */
	public double distance(Vector3D v) {
		double dx = this.x - v.x;
		double dy = this.y - v.y;
		double dz = this.z - v.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	/**
	 * @param v
	 * @return squared distance to v
	 */
	public double distanceSquared(Vector3D v) {
		double dx = this.x - v.x;
		double dy = this.y - v.y;
		double dz = this.z - v.z;
		return (dx * dx + dy * dy + dz * dz);
	}

	/**
	 * @param v1
	 * @return dot product
	 */
	public double dot(Vector3D v1) {
		return this.x * v1.x + this.y * v1.y + this.z * v1.z;
	}

	/**
	 * set all values to zero
	 */
	public void setZero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	/**
	 * @param v1
	 * @param v2
	 */
	public void cross(Vector3D v1, Vector3D v2) {
		double tmpx = v1.y * v2.z - v1.z * v2.y;
		double tmpy = v1.z * v2.x - v1.x * v2.z;
		double tmpz = v1.x * v2.y - v1.y * v2.x;
		this.x = tmpx;
		this.y = tmpy;
		this.z = tmpz;
	}

	public String toString() {
		return x + " " + y + " " + z;
	}

	/**
	 * @return double[] representing this vector
	 */
	public double[] toArray() {
		return new double[] { this.x, this.y, this.z };
	}

	public Vector3D clone() {
		return new Vector3D(this);
	}
}
