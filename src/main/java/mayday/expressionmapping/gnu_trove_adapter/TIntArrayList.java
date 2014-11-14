package mayday.expressionmapping.gnu_trove_adapter;

import java.util.ArrayList;

/* This class can be replaced with the Trove class "gnu.trove.TIntArrayList" at any time to
 * reduce memory consumption 
 */ 
 


@SuppressWarnings("serial")
public class TIntArrayList extends ArrayList<Integer> {
	
	public int[] toNativeArray() {
		int[] ret = new int[size()];
		for (int i=0; i!=ret.length; ++i)
			ret[i] = get(i);
		return ret;		
	}
}
