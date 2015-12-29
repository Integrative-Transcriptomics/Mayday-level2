package mayday.vis3.plots.tagcloud;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.StringListMIO;
import mayday.core.structures.maps.MultiHashMap;

public abstract class AbstractTagCloudFactory 
{
	private static AbstractTagCloudFactory linearInstance;
	private static AbstractTagCloudFactory logInstance;
	private static AbstractTagCloudFactory expInstance;
	
	public static AbstractTagCloudFactory getLinearInstance(){
		if(linearInstance==null)
			linearInstance=new TagCloudFactory();
		return linearInstance;
	}
	
	public static AbstractTagCloudFactory getLogInstance(){
		if(logInstance==null)
			logInstance=new LogTagCloudFactory();
		return logInstance;
	}
	
	public static AbstractTagCloudFactory getExpInstance(){
		if(expInstance==null)
			expInstance=new ExpTagCloudFactory();
		return expInstance;
	}
	
	public List<Tag> probeListsByProbes(List<ProbeList> probeLists)
	{
		Map<Object, Integer> map=new HashMap<Object, Integer>();
		MultiHashMap<Object, Probe> probeMap=new MultiHashMap<Object, Probe>();
		for(ProbeList pl:probeLists)
		{
			map.put(pl, pl.getNumberOfProbes());
			for(Probe p:pl)
				probeMap.put(pl, p);
		}
		List<Tag> tags=fromMap(map);
		for(Tag t:tags)
		{
			t.setProbes(probeMap.get(t.getTag(),true));
		}
		return tags;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Tag> MIOByFrequency(Iterable<Probe> probes, MIGroup miGroup)
	{
		Map<Object, Integer> map=new HashMap<Object, Integer>();
		MultiHashMap<Object, Probe> probeMap=new MultiHashMap<Object, Probe>();
		for(Probe p:probes)
		{
			if(miGroup.getMIO(p)==null) continue;
			Object o=((GenericMIO)miGroup.getMIO(p)).getValue();
			if(!map.containsKey(o))
			{
				map.put(o, 1);
			}else
			{
				map.put(o, map.get(o)+1);
			}
			probeMap.put(o, p);
		}
		if(map.isEmpty())
			return Collections.emptyList();
		List<Tag> tags=fromMap(map);
		for(Tag t:tags)
		{
			t.setProbes(probeMap.get(t.getTag(),true));
		}
		return tags;
	}
	
	public List<Tag> StringByStringListMIO(Iterable<Probe> probes, MIGroup miGroup)
	{
		Map<Object, Integer> map=new HashMap<Object, Integer>();
		MultiHashMap<Object, Probe> probeMap=new MultiHashMap<Object, Probe>();
		for(Probe p:probes)
		{
			if(miGroup.getMIO(p)==null) continue;
			if(!(miGroup.getMIO(p) instanceof StringListMIO)) continue;
				
			List<String> list=((StringListMIO)miGroup.getMIO(p)).getValue();
			for(String o:list)
			{
				if(!map.containsKey(o))
				{
					map.put(o, 1);
				}else
				{
					map.put(o, map.get(o)+1);
				}
				probeMap.put(o, p);
			}
		}
		List<Tag> tags=fromMap(map);
		for(Tag t:tags)
		{
			t.setProbes(probeMap.get(t.getTag(),true));
		}
		return tags;
	}
	
	protected double map(int count, int min, int max)
	{
		return map(1.0*count, 1.0*min, 1.0*max);
	}
	
	protected double map(double count, double min, double max)
	{
		if(min==max)
			return 1.0;
		return (count -min) / (max-min);
	}
	
	public abstract List<Tag> fromMap(Map<? extends Object, Integer> map);
	
	/**
	 * Determines whether child is somewhere (not necessarily directly) below parent.
	 * @param child 
	 * @param parent
	 * @return 
	 */
	protected boolean isChildOf(ProbeList child, ProbeList parent)
	{
		ProbeList pl=child.getParent();
		while(pl!=null)
		{
			if(pl==parent) return true;
			pl=pl.getParent();
		}
		return false;
	}
}
