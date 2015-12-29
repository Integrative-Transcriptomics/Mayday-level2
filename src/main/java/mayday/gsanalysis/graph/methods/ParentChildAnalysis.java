package mayday.gsanalysis.graph.methods;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.core.structures.maps.BidirectionalHashMap;
import mayday.gsanalysis.Enrichment;
import mayday.gsanalysis.Geneset;
import mayday.gsanalysis.graph.GraphEnrichmentMethod;
import mayday.gsanalysis.ora.ORAPlugin;

public class ParentChildAnalysis extends ORAPlugin implements GraphEnrichmentMethod{
	protected final String name="Parent-Child";
	protected RestrictedStringSetting methodSetting;
	protected Graph graph;
	protected BidirectionalHashMap<Node, Geneset> genesetMap;
	protected BidirectionalHashMap<Node,Enrichment> enrichmentMap;
	protected HashMap<String,Enrichment> enrichments;
	
	@Override
	public String getName() {
		return name;
	}

	public ParentChildAnalysis() {
		genesetSetting=false;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.GraphEnrichmentMethods.ParentChildAnalysis",
				new String[0], 
				GraphEnrichmentMethod.MC,
				(HashMap<String,Object>)null,
				"Roland Keller",
				"no e-mail provided",
				"Plugin for Parent Child Analysis",
		"Parent-Child Analysis");
		return pli;
	}

	@Override
	protected String additionalPreferences() {
		String preferences = "Parent Child Analysis (" + methodSetting.getStringValue() + ") with " + overreptest.getStringValue() +"<p/>";
		if(select.getSelectedIndex()!=0) {
			preferences+= "Threshold: " + m2_or_m3_threshold.getDoubleValue() + "<p/>";
		}
		if(select.getSelectedIndex()==1) {
			preferences+="MIGroup for p-values " + m2_pvalues.getName() + "<p/>";
		}
		if(select.getSelectedIndex()==2) {
			preferences+= "Statistic for single genes: " + m3_testmethod.getPluginInfo().getName() +"<p/>";
			preferences+= "ClassSelectionModel: " + m3_classes.getModel().toString(true)+ "<p/>";
		}
		
		if(selectCorrection.getSelectedIndex()==0) {
			preferences+= "Method for p-value correction: " + correctionMethodSetting.getPluginInfo().getName()+ "<p/>";
		}
		else {
			preferences+= "Method for p-value correction: " + correctionMethodSettingPerm.getPluginInfo().getName()+ "<p/>";
			preferences+= "Number of permutations: " + nPermutations.getIntValue()+ "<p/>";
		}
		return preferences;
	}

	@Override
	public List<Setting> additionalSettings() {
		List<Setting> settings = new LinkedList<Setting>();
		methodSetting = new RestrictedStringSetting("Union or intersection?", null, 0, new String[] {"Union","Intersection"});
		settings.add(methodSetting);
		settings.addAll(super.additionalSettings());
		
		return settings;
	}

	protected List<Enrichment> calculateTestStatistics() {
		enrichmentMap=new BidirectionalHashMap<Node,Enrichment>();
		enrichments = new HashMap<String,Enrichment>();
		List<Enrichment> testResults = new LinkedList<Enrichment>();
		
		List<Node> sorted = Graphs.topologicalSort(graph);
		
		for(Node n:sorted) {
			Geneset g=genesetMap.get(n);
			Enrichment e = new Enrichment(g);
			if(!g.getRemoved()) {
				enrichments.put(g.getName(), e);
				enrichmentMap.put(n, e);
				calculateTestStatistic(interestingGenes,allGenes.size(),e,g.getGenes());
				testResults.add(e);
			}
		}
		return testResults;
	}

	@Override
	public void calculateTestStatistic(Set<String> interestingGenes,int numberOfAllGenes, Enrichment enr, Set<String> genesInSet) {
		Node n = enrichmentMap.get(enrichments.get(enr.getGeneset().getName()));
		
		Set<String> genesPat=new TreeSet<String>();
		Set<String> interestingGenesPat = new TreeSet<String>();
		
		Set<Node> parents = graph.getOutNeighbors(n);
		if(parents.size()==0) {
			super.calculateTestStatistic(interestingGenes, numberOfAllGenes, enr, genesInSet);
			return;
		}
		for(Node parent: parents) {
			Geneset g = genesetMap.get(parent);
			if(methodSetting.getValueString().equals("Union")) {
				genesPat.addAll(g.getGenes());
			}
			else {
				if(genesPat.size()==0) {
					genesPat.addAll(g.getGenes());
				}
				else {
					genesPat.retainAll(g.getGenes());
				}
			}	
		}
		for(String gene: genesPat) {
			if(interestingGenes.contains(gene)) {
				interestingGenesPat.add(gene);
			}
		}
		super.calculateTestStatistic(interestingGenesPat, genesPat.size(), enr, enr.getGeneset().getGenes());	
	}
	
	@Override
	public List<ProbeList> calculateGraphEnrichment(Graph graph, BidirectionalHashMap<Node, Geneset> genesetMap,
			ProbeList probes, ProbeList probesMasterTable) {
		this.graph=graph;
		this.genesetMap=genesetMap;
		return run(probes,probesMasterTable);
		
	}

}

