package mayday.tiala.pairwise.data;

import java.util.EventListener;

public interface AlignmentStoreListener extends EventListener {
	
	public void alignmentChanged(AlignmentStoreEvent evt);

}
