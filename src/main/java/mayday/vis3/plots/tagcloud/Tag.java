package mayday.vis3.plots.tagcloud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import mayday.core.Probe;

/**
 * Keeps a tag and its frequency. We will implement this as a rather stupid struct. 
 * @author symons
 *
 */
public class Tag implements Comparable<Tag>
{
	private Object tag;
	private double frequency;
	private int count; 
	
	private List<Probe> probes;
	
	/**
	 * Creates a new tag with the given frequency. 
	 * @param tag The name of the tag.
	 * @param frequency The frequency of the tag, element of [0,1]
	 */
	public Tag(Object tag, double frequency) 
	{
		this.tag = tag;
		this.frequency = frequency;
		probes=new ArrayList<Probe>();
	}
	
	/**
	 * Creates a new tag that does not appear at all
	 * @param tag The name of the tag
	 */
	public Tag(Object tag) 
	{
		this.tag=tag;
		frequency=0.0d;
		probes=new ArrayList<Probe>();
	}

	/**
	 * @return the tag
	 */
	public Object getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(Object tag) {
		this.tag = tag;
	}

	/**
	 * @return the frequency
	 */
	public double getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	
	public int compareTo(Tag o) 
	{
		int r= Double.compare(frequency, o.frequency);
		if(r==0)
			r= tag.toString().compareTo(o.tag.toString());
		return r;
	}
	
	@Override
	public String toString() 
	{
		return tag.toString()+"="+frequency;
	}
	
	public static class TagHighestFirstComparator implements Comparator<Tag>
	{
		public int compare(Tag o1, Tag o2) 
		{
			int r= Double.compare(1.0-o1.frequency, 1.0-o2.frequency);
			if(r==0)
				r= o1.tag.toString().compareTo(o2.tag.toString());
			return r;
		}
	}
	
	public static class TagNameComparator implements Comparator<Tag>
	{
		public int compare(Tag o1, Tag o2) 
		{
			return o1.tag.toString().compareTo(o2.tag.toString());
		}
	}

	public List<Probe> getProbes() {
		return probes;
	}

	public void setProbes(List<Probe> probes) {
		this.probes = probes;
	}
	
	public void addProbe(Probe p)
	{
		probes.add(p);
	}
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	
}
