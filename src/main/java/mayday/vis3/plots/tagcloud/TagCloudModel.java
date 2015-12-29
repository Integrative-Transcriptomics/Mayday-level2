package mayday.vis3.plots.tagcloud;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.renderer.RendererTools;

public class TagCloudModel extends GraphModel
{
	
	private Map<Color, Integer> legend = new HashMap<Color, Integer>();
	
	public TagCloudModel(List<Tag> tags)
	{
		super(createTagGraph(tags));
		
	}
	
	public TagCloudModel(Graph graph) 
	{
		super(graph);
	}

	protected void init()
	{
		clear();
		for(Node n:getGraph().getNodes())
		{
			TagComponent comp=new TagComponent((MultiProbeNode)n);
			addComponent(comp);	
			getNodeMap().put(comp, n);
			getComponentMap().put(n, comp);			
		}
		Collections.sort(getComponents());
	}
	
	public void filter(double cutoff, boolean invert)
	{
		for(CanvasComponent comp:getComponents())
		{
			int count = Integer.parseInt(((MultiProbeNode)getNode(comp)).getPropertyValue(TagConstants.TAG_COUNT));
			if( count < cutoff )
			{
				comp.setVisible(invert);
			}else
			{
				comp.setVisible(!invert);
			}
		}
	}
	
	public void updateLegend(ColorGradient gradient) {
		legend.clear();
		for(CanvasComponent comp : getComponents()) {
			if(comp.isVisible()) {
				int count = Integer.parseInt(((MultiProbeNode)getNode(comp)).getPropertyValue(TagConstants.TAG_COUNT));
				double freq = Double.parseDouble(((MultiProbeNode)getNode(comp)).getPropertyValue(TagConstants.TAG_FREQUENCY));
				
				Color back = gradient.mapValueToColor(freq);
				back = RendererTools.alphaColor(back,100);
				legend.put(back, count);
			}
		}
	}
	
	public Map<Color, Integer> getLegendMap() {
		return this.legend;
	}
	
	public static Graph createTagGraph(List<Tag> tags)
	{
		Graph graph=new Graph();
		
		for(Tag t:tags)
		{
			MultiProbeNode node=new MultiProbeNode(graph);
			node.setRole(TagConstants.TAG_ROLE);
			node.setName(t.getTag().toString());
			node.setProperty(TagConstants.TAG_KEY, t.getTag().toString());
			node.setProperty(TagConstants.TAG_FREQUENCY, Double.toString(t.getFrequency()));
			node.setProperty(TagConstants.TAG_COUNT, Integer.toString(t.getCount()));
			if(!t.getProbes().isEmpty())
				node.setProbes(t.getProbes());
			graph.addNode(node);
		}	
		return graph;
	}
	
	@Override
	public GraphModel buildSubModel(List<Node> selectedNodes) 
	{
		Graph g=Graphs.restrict(getGraph(), selectedNodes);
		return new TagCloudModel(g);
	}
}

