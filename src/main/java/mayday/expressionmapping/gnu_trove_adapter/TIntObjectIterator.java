package mayday.expressionmapping.gnu_trove_adapter;

import java.util.Map.Entry;

import java.util.Iterator;

public class TIntObjectIterator<T> {

	public Iterator<Entry<Integer,T>> it;
	public Entry<Integer,T> e;
	
	public TIntObjectIterator(Iterator<Entry<Integer,T>> inner) {
		it = inner;
	}
	
	public void advance() {
		e = it.next();
	}
	
	public int key() {
		return e.getKey();
	}
	
	public T value() {
		return e.getValue();
	}
	
	public boolean hasNext() {
		return it.hasNext();
	}

	public void remove() {
		it.remove();
	}

	public void setValue(T value) {
		e.setValue(value);
	}
	
}
