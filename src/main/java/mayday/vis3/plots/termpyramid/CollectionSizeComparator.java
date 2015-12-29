package mayday.vis3.plots.termpyramid;

import java.util.Collection;
import java.util.Comparator;

@SuppressWarnings("unchecked")
public class CollectionSizeComparator implements Comparator<Collection> 
{

	@Override
	public int compare(Collection o1, Collection o2) 
	{
		return o1.size() - o2.size();
	}
	
	@Override
	public String toString() 
	{
		return "Size";
	}

}
