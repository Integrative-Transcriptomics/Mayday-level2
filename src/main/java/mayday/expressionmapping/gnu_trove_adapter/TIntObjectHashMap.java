package mayday.expressionmapping.gnu_trove_adapter;

import java.util.HashMap;

/* This class can be replaced with the Trove class "gnu.trove.TIntObjectHashMap" at any time to
 * reduce memory consumption 
 */ 
 


@SuppressWarnings("serial")
public class TIntObjectHashMap<T> extends HashMap<Integer,T> {

	public TIntObjectHashMap(int numberOfCluster) {
		super(numberOfCluster);
	}

	public TIntObjectHashMap() {
	}

	@SuppressWarnings("unchecked")
	public TIntObjectIterator<T> iterator() {
		return new TIntObjectIterator(entrySet().iterator());
	}

	public boolean contains(int id) {
		return containsKey(id);
	}
	
}
