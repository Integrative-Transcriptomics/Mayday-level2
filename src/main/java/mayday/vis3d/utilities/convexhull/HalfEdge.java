package mayday.vis3d.utilities.convexhull;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class HalfEdge {

	/**
	 * 
	 */
	public Vertex vertex;
	/**
	 * 
	 */
	public Face face;
	/**
	 * 
	 */
	public HalfEdge next;
	/**
	 * 
	 */
	public HalfEdge prev;
	/**
	 * 
	 */
	public HalfEdge opposite;

	/**
	 * default constructor
	 */
	public HalfEdge() {}

	/**
	 * @param v
	 * @param f
	 */
	public HalfEdge(Vertex v, Face f) {
		this.vertex = v;
		this.face = f;
	}

	/**
	 * @param edge
	 */
	public void setNext(HalfEdge edge) {
		this.next = edge;
	}

	/**
	 * @return next half edge
	 */
	public HalfEdge getNext() {
		return next;
	}

	/**
	 * @param edge
	 */
	public void setPrev(HalfEdge edge) {
		prev = edge;
	}

	/**
	 * @return previous half edge
	 */
	public HalfEdge getPrev() {
		return prev;
	}

	/**
	 * @return face
	 */
	public Face getFace() {
		return face;
	}

	/**
	 * @return opposite half edge
	 */
	public HalfEdge getOpposite() {
		return opposite;
	}

	/**
	 * @param edge
	 */
	public void setOpposite(HalfEdge edge) {
		opposite = edge;
		edge.opposite = this;
	}

	/**
	 * @return head
	 */
	public Vertex head() {
		return vertex;
	}

	/**
	 * @return tail
	 */
	public Vertex tail() {
		return prev != null ? prev.vertex : null;
	}

	/**
	 * @return opposite face
	 */
	public Face oppositeFace() {
		return opposite != null ? opposite.face : null;
	}

	/**
	 * @return string representation
	 */
	public String getVertexString() {
		if (tail() != null) {
			return "" + tail().index + "-" + head().index;
		} else {
			return "?-" + head().index;
		}
	}

	/**
	 * @return distance from head to tail
	 */
	public double length() {
		if (tail() != null) {
			return head().p.distance(tail().p);
		} else {
			return -1;
		}
	}

	/**
	 * @return squared distance from head to tail
	 */
	public double lengthSquared() {
		if (tail() != null) {
			return head().p.distanceSquared(tail().p);
		} else {
			return -1;
		}
	}
}
