package mayday.Reveal.events;

import java.util.EventObject;

import mayday.Reveal.data.SNVList;

@SuppressWarnings("serial")
public class SNVListEvent extends EventObject {

	private int change;
	
	public SNVListEvent(SNVList snpList, int change) {
		super(snpList);
		this.change = change;
	}
	
	public int getChange() {
		return this.change; 
	}

	public void setChange(int change) {
		this.change = change;
	}
	
	public boolean equals(Object evt) {
		if (evt instanceof SNVListEvent)
			return ((SNVListEvent)evt).getSource()==source && ((SNVListEvent)evt).getChange()==change;
		return super.equals(evt);
	}

	public static final int CONTENT_CHANGE = 0;
	public final static int ANNOTATION_CHANGE = 1;
	public final static int SNPLIST_CLOSED = 2;
}
