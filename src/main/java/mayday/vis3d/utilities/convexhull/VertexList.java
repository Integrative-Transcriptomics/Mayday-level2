package mayday.vis3d.utilities.convexhull;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class VertexList {

	private Vertex head;
	private Vertex tail;

	/**
	 * clear the vertex list
	 */
	public void clear() {
		head = tail = null;
	}

	/**
	 * @param v
	 */
	public void add(Vertex v) {
		if (head == null) {
			head = v;
		} else {
			tail.next = v;
		}
		v.prev = tail;
		v.next = null;
		tail = v;
	}

	/**
	 * @param v
	 */
	public void addAll(Vertex v) {
		if (head == null) {
			head = v;
		} else {
			tail.next = v;
		}
		v.prev = tail;
		while (v.next != null) {
			v = v.next;
		}
		tail = v;
	}

	/**
	 * @param v
	 */
	public void delete(Vertex v) {
		if (v.prev == null) {
			head = v.next;
		} else {
			v.prev.next = v.next;
		}
		if (v.next == null) {
			tail = v.prev;
		} else {
			v.next.prev = v.prev;
		}
	}

	/**
	 * @param v1
	 * @param v2
	 */
	public void delete(Vertex v1, Vertex v2) {
		if (v1.prev == null) {
			head = v2.next;
		} else {
			v1.prev.next = v2.next;
		}
		if (v2.next == null) {
			tail = v1.prev;
		} else {
			v2.next.prev = v1.prev;
		}
	}

	/**
	 * @param v
	 * @param next
	 */
	public void insertBefore(Vertex v, Vertex next) {
		v.prev = next.prev;
		if (next.prev == null) {
			head = v;
		} else {
			next.prev.next = v;
		}
		v.next = next;
		next.prev = v;
	}

	/**
	 * @return head
	 */
	public Vertex first() {
		return head;
	}

	/**
	 * @return true , if the list is empty, else false
	 */
	public boolean isEmpty() {
		return head == null;
	}
}
