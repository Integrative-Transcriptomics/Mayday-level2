package mayday.expressionmapping.gnu_trove_adapter;

import java.util.ArrayList;

/* This class can be replaced with the Trove class "gnu.trove.TDoubleArrayList" at any time to
 * reduce memory consumption 
 */ 
 


@SuppressWarnings("serial")
public class TDoubleArrayList extends ArrayList<Double> {
	
	public TDoubleArrayList(int maxRounds) {
		super(maxRounds);
	}

	public double[] toNativeArray() {
		double[] ret = new double[size()];
		for (int i=0; i!=ret.length; ++i)
			ret[i] = get(i);
		return ret;		
	}
}
