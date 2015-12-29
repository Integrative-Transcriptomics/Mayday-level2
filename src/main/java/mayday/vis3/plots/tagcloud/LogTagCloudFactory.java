package mayday.vis3.plots.tagcloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogTagCloudFactory extends AbstractTagCloudFactory {

	@Override
	public List<Tag> fromMap(Map<? extends Object, Integer> map) 
	{
		Map<Object, Double> tmap=new HashMap<Object, Double>();
		for(Object o:map.keySet()){
			tmap.put(o, Math.log(map.get(o)));
		}
		
		double max=Collections.max(tmap.values());
		double min=Collections.min(tmap.values());
		System.out.println(min+"\t"+max);
		List<Tag> res=new ArrayList<Tag>();
		for(Object o:tmap.keySet())
		{
			Tag tag=new Tag(o, map(tmap.get(o),min,max));
			tag.setCount(map.get(o));
			res.add(tag);
		}
		return res;
	}

}
