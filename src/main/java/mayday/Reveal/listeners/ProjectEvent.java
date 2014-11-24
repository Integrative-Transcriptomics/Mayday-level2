package mayday.Reveal.listeners;

import java.util.EventObject;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class ProjectEvent extends EventObject {

	/**
	 * 
	 */
	public static final int PROJECT_CHANGED = 0;
	/**
	 * 
	 */
	public static final int PROJECT_ADDED = 1;
	/**
	 * 
	 */
	public static final int PROJECT_REMOVED = 2;
	
	/**
	 * 
	 */
	public static final int PROJECT_SELECTION_CHANGED = 3;
	
	public static final int SNP_SELECTION_CHANGED = 4;
	
	public static final int SNP_SELECTION_CLEARED = 5;
	
	public static final int METAINFO_SELECTION_CHANGED = 6;
	
	private int change;
	
	/**
	 * @param source
	 * @param change
	 */
	public ProjectEvent(Object source, int change) {
		super(source);
		this.change = change;
	}
	
	/**
	 * @return change
	 */
	public int getChange() {
		return change;
	}
	
	public boolean equals(Object evt) {
		if(evt instanceof ProjectEvent) {
			return ((ProjectEvent) evt).getSource() == source && ((ProjectEvent)evt).getChange() == change;
		}
		return super.equals(evt);
	}
}
