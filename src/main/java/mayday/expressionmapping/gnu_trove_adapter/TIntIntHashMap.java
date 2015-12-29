package mayday.expressionmapping.gnu_trove_adapter;

import java.util.HashMap;

/* This class can be replaced with the Trove class "gnu.trove.TIntIntHashMap" at any time to
 * reduce memory consumption 
 */ 
 


@SuppressWarnings("serial")
public class TIntIntHashMap extends HashMap<Integer,Integer> {

	public TIntIntHashMap(int numberOfPoints) {
		super( numberOfPoints );
	}
	
	public TIntIntIterator iterator() {
		return new TIntIntIterator(entrySet().iterator());
	}
	
}
