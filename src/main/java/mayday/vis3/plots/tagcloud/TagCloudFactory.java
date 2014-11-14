package mayday.vis3.plots.tagcloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TagCloudFactory extends AbstractTagCloudFactory
{
	
	public List<Tag> fromMap(Map<? extends Object, Integer> map)
	{
		int max=Collections.max(map.values());
		int min=Collections.min(map.values());
		System.out.println(min+"\t"+max);
		List<Tag> res=new ArrayList<Tag>();
		for(Object o:map.keySet())
		{
			Tag tag=new Tag(o, map(map.get(o),min,max));
			tag.setCount(map.get(o));
			res.add(tag);
		}
		return res;
	}
	

	
	

	

}
