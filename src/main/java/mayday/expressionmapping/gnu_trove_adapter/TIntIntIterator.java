package mayday.expressionmapping.gnu_trove_adapter;

import java.util.Map.Entry;

import java.util.Iterator;

public class TIntIntIterator extends TIntObjectIterator<Integer> {

	public TIntIntIterator(Iterator<Entry<Integer, Integer>> inner) {
		super(inner);
	}
	
}
