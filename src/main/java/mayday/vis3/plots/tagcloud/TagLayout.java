package mayday.vis3.plots.tagcloud;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.math.JamaSubset.Matrix;
import mayday.core.math.JamaSubset.PCA;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.model.GraphModel;

public class TagLayout implements CanvasLayouter 
{

	private int xSpace=20;
	private int ySpace=20;

	public static final int HIGHEST_CENTER=0;
	public static final int HIGHEST_FIRST=1;
	public static final int LEXICOGRAPHICAL=2;
	public static final int RANDOM=3;
	public static final int PCA=4;

	private int mode=HIGHEST_CENTER;
	
	public static final String[] layoutNames={"Center largest Tags","Largest Tags first","Sort by name","Random", "PCA"};

	public TagLayout() 
	{	
		// nothing to do;
	}
	
	public TagLayout(int mode) 
	{	
		if(mode >4)
			throw new IllegalArgumentException("No such mode.");
		this.mode=mode;
	}
	
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		bounds=new Rectangle(0,0,Math.min(container.getWidth(),800),container.getHeight());
		switch (mode) 
		{
		case HIGHEST_CENTER:
			layoutFill(highestCenter(model),bounds);			
			break;
		case HIGHEST_FIRST:
			layoutFill(highestFirst(model),bounds);			
			break;
		case LEXICOGRAPHICAL:
			layoutFill(lexicographical(model),bounds);			
			break;
		case RANDOM:
			layoutFill(random(model),bounds);			
			break;	
		case PCA:
			layoutPCA( container,  bounds,  model);
			break;
			
		default:
			throw new IllegalArgumentException("No such mode");		
		}


	}

	private void layoutPCA(Container container, Rectangle bounds,
			GraphModel model) 
	{
		List<Probe> probes=new ArrayList<Probe>();
		Map<Probe, CanvasComponent> probeCompMap=new HashMap<Probe, CanvasComponent>();
		for(CanvasComponent cc:model.getComponents()) {
			MultiProbeComponent n=(MultiProbeComponent)cc;
			Probe p=((MultiProbeNode)n.getNode()).getProbeList().getStatistics().getMean();
			probes.add(p);
			probeCompMap.put(p, cc);
		}
		int n=probes.size();
		int m = probes.get(0).getNumberOfExperiments();
		double[][]indat = new double[n][m];
		try {

			int i=0;
			for (Probe tmp : probes) 
			{ 
				for (int j=0; j!=m; ++j) 
				{
					indat[i][j] = tmp.getValue(j);
				}
				++i;
			}
		} catch (NullPointerException e){
			throw new RuntimeException("Cannot work on Probes containing missing values");
		}
		
		PCA pca = new PCA(indat);
		Matrix pcaM=pca.getResult();
		

		double min1=Double.MAX_VALUE;
		double max1=Double.MIN_VALUE;
		double min2=Double.MAX_VALUE;
		double max2=Double.MIN_VALUE;
		
		for(int i=0; i!=pcaM.getRowDimension(); ++i)
		{
			min1=Math.min(pcaM.get(i, 0), min1);
			min2=Math.min(pcaM.get(i, 1), min2);			
			max1=Math.max(pcaM.get(i, 0), max1);
			max2=Math.max(pcaM.get(i, 1), max2);
		}
		int i=0;
		for(Probe p:probes)
		{
			double x= (pcaM.get(i, 0)-min1) / (max1-min1) * bounds.getWidth();
			double y= (-pcaM.get(i, 1)-min2) / (max2-min2) * bounds.getHeight();
			CanvasComponent comp=probeCompMap.get(p);
			comp.setLocation((int) x, (int)y );
			++i;
		}
	}

	private List<CanvasComponent> lexicographical(GraphModel model)
	{
		Graph g=model.getGraph();
		List<Tag> l1=new LinkedList<Tag>();
		Map<Tag,CanvasComponent> nodeMap=new HashMap<Tag, CanvasComponent>();
		for(Node n:g.getNodes())
		{
			DefaultNode dn=(DefaultNode)n;
			Tag t=new Tag(dn.getPropertyValue(TagConstants.TAG_KEY), Double.parseDouble(dn.getPropertyValue(TagConstants.TAG_FREQUENCY)));
			nodeMap.put(t, model.getComponent(n));
			l1.add(t);
	
		}
		Collections.sort(l1,new Tag.TagNameComparator());
		List<CanvasComponent> res=new LinkedList<CanvasComponent>();
		for(Tag t:l1)
		{
			res.add(nodeMap.get(t));
		}
		return res;
	}

	private List<CanvasComponent> random(GraphModel model)
	{
		Graph g=model.getGraph();
		List<Tag> l1=new LinkedList<Tag>();
		Map<Tag,CanvasComponent> nodeMap=new HashMap<Tag, CanvasComponent>();
		for(Node n:g.getNodes())
		{
			DefaultNode dn=(DefaultNode)n;
			Tag t=new Tag(dn.getPropertyValue(TagConstants.TAG_KEY), Double.parseDouble(dn.getPropertyValue(TagConstants.TAG_FREQUENCY)));
			nodeMap.put(t, model.getComponent(n));
			l1.add(t);
		}
		Collections.shuffle(l1);
		List<CanvasComponent> res=new LinkedList<CanvasComponent>();
		for(Tag t:l1)
		{
			res.add(nodeMap.get(t));
		}
		return res;
	}

	
	private List<CanvasComponent> highestFirst(GraphModel model)
	{
		Graph g=model.getGraph();
		List<Tag> l1=new LinkedList<Tag>();
		Map<Tag,CanvasComponent> nodeMap=new HashMap<Tag, CanvasComponent>();
		for(Node n:g.getNodes())
		{
			DefaultNode dn=(DefaultNode)n;
			Tag t=new Tag(dn.getPropertyValue(TagConstants.TAG_KEY), Double.parseDouble(dn.getPropertyValue(TagConstants.TAG_FREQUENCY)));
			nodeMap.put(t, model.getComponent(n));
			l1.add(t);
		}
		Collections.sort(l1,new Tag.TagHighestFirstComparator());
		List<CanvasComponent> res=new LinkedList<CanvasComponent>();
		for(Tag t:l1)
		{
			res.add(nodeMap.get(t));
		}
		return res;
	}

	private List<CanvasComponent> highestCenter(GraphModel model)
	{
		Graph g=model.getGraph();
		List<Tag> l1=new LinkedList<Tag>();
		List<Tag> l2=new LinkedList<Tag>();
		Map<Tag,CanvasComponent> nodeMap=new HashMap<Tag, CanvasComponent>();
		boolean b=true;
		for(Node n:g.getNodes())
		{
			DefaultNode dn=(DefaultNode)n;
			Tag t=new Tag(dn.getPropertyValue(TagConstants.TAG_KEY), Double.parseDouble(dn.getPropertyValue(TagConstants.TAG_FREQUENCY)));
			nodeMap.put(t, model.getComponent(n));
			if(b)
				l1.add(t);
			else
				l2.add(t);
			b= !b;			
		}
		Collections.sort(l1);
		Collections.sort(l2,new Tag.TagHighestFirstComparator());

		l1.addAll(l2);
		List<CanvasComponent> res=new LinkedList<CanvasComponent>();
		for(Tag t:l1)
		{
			res.add(nodeMap.get(t));
		}
		return res;
	}

	public void layoutFill(List<CanvasComponent> components, Rectangle bounds) 
	{
		int usedSpace=bounds.x+xSpace;
		int maxY=0;
		int yPos=bounds.y+ySpace;
		for(CanvasComponent comp:components)
		{
			if(!comp.isVisible()) continue;
			if(usedSpace+comp.getWidth()+xSpace > bounds.x+bounds.getWidth() )
			{
				usedSpace=bounds.x+xSpace;
				maxY+=ySpace;
				yPos=maxY;
			}
			comp.setLocation(usedSpace,yPos );
			usedSpace+=xSpace+comp.getWidth();
			if(yPos+comp.getHeight() > maxY) maxY= yPos+comp.getHeight();			
		}
	}

	public void layoutFill(List<Tag> tags, Map<Tag,CanvasComponent> map, Rectangle bounds, GraphModel model) 
	{
		int usedSpace=bounds.x+xSpace;
		int maxY=0;
		int yPos=bounds.y+ySpace;
		for(Tag t:tags)
		{
			CanvasComponent comp=map.get(t);
			if(!comp.isVisible()) continue;
			if(usedSpace+comp.getWidth()+xSpace > bounds.x+bounds.getWidth() )
			{
				usedSpace=bounds.x+xSpace;
				maxY+=ySpace;
				yPos=maxY;
			}

			comp.setLocation(usedSpace,yPos );
			usedSpace+=xSpace+comp.getWidth();
			if(yPos+comp.getHeight() > maxY) maxY= yPos+comp.getHeight();			
		}
	}
	
}
