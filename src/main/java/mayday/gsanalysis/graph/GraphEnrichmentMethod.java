package mayday.gsanalysis.graph;

import java.util.List;

import mayday.core.ProbeList;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.maps.BidirectionalHashMap;
import mayday.gsanalysis.Geneset;

public interface GraphEnrichmentMethod extends ProbelistPlugin{
	public static final String MC = "GraphEnrichmentMethod";
	
	public List<ProbeList> calculateGraphEnrichment(Graph graph, BidirectionalHashMap<Node, Geneset> genesetMap,
			ProbeList probes, ProbeList probesMasterTable);
	
	
}
