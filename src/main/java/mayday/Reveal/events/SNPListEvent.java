package mayday.Reveal.events;

import java.util.EventObject;

import mayday.Reveal.data.SNPList;

@SuppressWarnings("serial")
public class SNPListEvent extends EventObject {

	private int change;
	
	public SNPListEvent(SNPList snpList, int change) {
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
		if (evt instanceof SNPListEvent)
			return ((SNPListEvent)evt).getSource()==source && ((SNPListEvent)evt).getChange()==change;
		return super.equals(evt);
	}

	public static final int CONTENT_CHANGE = 0;
	public final static int ANNOTATION_CHANGE = 1;
	public final static int SNPLIST_CLOSED = 2;
}
