package mayday.tiala.pairwise.data;

import javax.swing.event.ChangeEvent;

@SuppressWarnings("serial")
public class AlignmentStoreEvent extends ChangeEvent {

	public static final int SHIFT_CHANGED = 0;
	public static final int SCORING_CHANGED = 1;
	public static final int MATCHINGDISPLAY_CHANGED = 2;
	public static final int STATISTIC_CHANGED = 3;
	public static final int STORE_CLOSED = 4;
	
	protected int change;
	
	public int getChange() {
		return change;
	}
	
	public AlignmentStoreEvent(Object source, int change) {
		super(source);
		this.change = change;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof AlignmentStoreEvent))
			return false;
		AlignmentStoreEvent ase = (AlignmentStoreEvent)o;
		return change==ase.change && source==ase.source;
	}

	public int hashCode() {
		return change+source.hashCode();
	}
	
}
