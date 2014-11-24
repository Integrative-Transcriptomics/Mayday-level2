package mayday.Reveal.listeners;

import java.util.EventObject;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class DataStorageEvent extends EventObject {

	/**
	 * 
	 */
	public static final int DATA_CHANGED = 0;

	public static final int SNPLIST_SELECTION_CHANGED = 1;
	
	public static final int STATTEST_SELECTION_CHANGED = 2;

	public static final int CLOSING_CHANGE = 3;

	public static final int META_INFORMATION_CHANGED = 4;
	
	private int change;
	
	/**
	 * @param source
	 * @param change
	 */
	public DataStorageEvent(Object source, int change) {
		super(source);
		this.change = change;
	}
	
	/**
	 * @return change
	 */
	public int getChange() {
		return this.change;
	}
	
	public boolean equals(Object evt) {
		if(evt instanceof DataStorageEvent) {
			return ((DataStorageEvent) evt).getSource() == source && ((DataStorageEvent)evt).getChange() == change;
		}
		return super.equals(evt);
	}
}
