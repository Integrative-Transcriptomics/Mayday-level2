package mayday.vis3d.utilities.convexhull;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class FaceList {

	private Face head;
	private Face tail;

	/**
	 * clear the list
	 */
	public void clear() {
		head = tail = null;
	}

	/**
	 * @param f
	 */
	public void add(Face f) {
		if (head == null) {
			head = f;
		} else {
			tail.next = f;
		}
		f.next = null;
		tail = f;
	}

	/**
	 * @return head
	 */
	public Face first() {
		return head;
	}

	/**
	 * @return true, if list is empty, else false
	 */
	public boolean isEmpty() {
		return head == null;
	}
}
