package mayday.wapiti.containers.loci.merging;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class MergeSortIterator<ContentType> implements Iterator<ContentType> {
	
	protected HashMap<Iterator<ContentType>, ContentType> input;
	protected Comparator<ContentType> comparator;
	protected ContentType next;
	
	public MergeSortIterator( Collection<Iterator<ContentType>> input, Comparator<ContentType> comparator) {
		this.comparator = comparator;
		this.input = new HashMap<Iterator<ContentType>, ContentType>();
		for (Iterator<ContentType> ict : input)
			if (ict.hasNext())
				this.input.put(ict, ict.next());
		pickNext();
	}
	
	public MergeSortIterator( Collection<Iterator<ContentType>> input ) {
		this(input, null);
	}
	
	public boolean hasNext() {
		return next != null;
	}

	public ContentType next() {
		ContentType theNext = next;
		pickNext();		
		return theNext;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	protected void pickNext() {
		next = null;
		Iterator<ContentType> source = null;
		for (Entry<Iterator<ContentType>, ContentType> e : input.entrySet()) {
			ContentType cand = e.getValue();
			if (next==null || compare(next, cand)>0) {
				next = cand;
				source = e.getKey();
			}				
		}
		if (source!=null) {
			ContentType nextInSource = source.hasNext()?source.next():null;
			if (nextInSource!=null)
				input.put(source, nextInSource);
			else
				input.remove(source);
		}		
	}
	
	@SuppressWarnings("unchecked")
	protected int compare( ContentType o1, ContentType o2) {
		if (comparator!=null)
			return comparator.compare(o1, o2);
		Comparable<? super ContentType> k = (Comparable<? super ContentType>) o1;
		return k.compareTo(o2);
	}
	
	

}
