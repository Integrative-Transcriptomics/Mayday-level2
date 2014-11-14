package mayday.vis3d.primitives;
/**
 * This class represents a rotation of an object in 3-dimensional space
 * @author G\u00FCnter J\u00E4ger
 * @param <T>
 */
public class Rotation3D<T extends Number> {
	private T xRot;
	private T yRot;
	private T zRot;
	/**
	 * Define a rotation of a scene in 3d space
	 * @param xRot
	 * @param yRot
	 * @param zRot
	 */
	public Rotation3D(T xRot, T yRot, T zRot) {
		this.xRot = xRot;
		this.yRot = yRot;
		this.zRot = zRot;
	}
	/**
	 * set the rotation angle around the x axis
	 * @param xRot
	 */
	public void setXRotation(T xRot) {
		this.xRot = xRot;
	}
	/**
	 * set the rotation angle around the y axis
	 * @param yRot
	 */
	public void setYRotation(T yRot) {
		this.yRot = yRot;
	}
	/**
	 * set the rotation angle around the z axis
	 * @param zRot
	 */
	public void setZRotation(T zRot) {
		this.zRot = zRot;
	}
	/**
	 * @return rotation angle around the x axis
	 */
	public T getXRotation() {
		return xRot;
	}
	/**
	 * @return rotation angle around the y axis
	 */
	public T getYRotation() {
		return yRot;
	}
	/**
	 * @return rotation angle around the z axis
	 */
	public T getZRotation() {
		return zRot;
	}
}
