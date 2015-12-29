package mayday.gsanalysis.graph.methods;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.ProbeList;
import mayday.core.math.Binomial;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.core.structures.maps.BidirectionalHashMap;
import mayday.gsanalysis.Enrichment;
import mayday.gsanalysis.Geneset;
import mayday.gsanalysis.graph.GraphEnrichmentMethod;
import mayday.gsanalysis.ora.ORAPlugin;

public class ElimWeight extends ORAPlugin implements GraphEnrichmentMethod{
	protected final String name="Elim-Weight";
	protected RestrictedStringSetting methodSetting;
	protected Graph graph;
	protected BidirectionalHashMap<Node,Geneset> genesetMap;
	protected HashMap<Node,DoubleVector> weightsMap;
	protected HashMap<Node,DoubleVector> updateMap;
	protected HashMap<Node,Enrichment> enrichmentMap;
	
	@Override
	public String getName() {
		return name;
	}

	public ElimWeight() {
		genesetSetting=false;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.GraphEnrichmentMethods.ElimWeight",
				new String[0], 
				GraphEnrichmentMethod.MC,
				(HashMap<String,Object>)null,
				"Roland Keller",
				"no e-mail provided",
				"Plugin for ElimWeight",
		"Elim-Weight");
		return pli;
	}

	@Override
	protected String additionalPreferences() {
		String preferences =  methodSetting.getStringValue()+ " with " + overreptest.getStringValue() +"<p/>";
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
		
		preferences+= "Method for p-value correction: " + correctionMethodSetting.getPluginInfo().getName()+ "<p/>";
		return preferences;
	}

	@Override
	protected List<Setting> additionalSettings() {
		List<Setting> settings = new LinkedList<Setting>();
		methodSetting = new RestrictedStringSetting("Elim or Weight?", null, 0, new String[] {"Elim","Weight"});
		settings.add(methodSetting);
		settings.addAll(super.additionalSettings());
		settings.remove(settings.size()-1);
		settings.add(correctionMethodSetting);
		return settings;
	}

	
	protected List<Enrichment> calculateTestStatistics() {
		List<Enrichment> testResults = new LinkedList<Enrichment>();
		
		List<Node> sorted = Graphs.topologicalSort(graph);
		
		HashMap<Node,Set<String>> markedGenesMap=new HashMap<Node,Set<String>>();
		weightsMap = new HashMap<Node,DoubleVector>();
		updateMap = new HashMap<Node,DoubleVector>();
		enrichmentMap = new HashMap<Node,Enrichment>();
		for(Node n:sorted) {
			Geneset currentGeneset=genesetMap.get(n);
			if(!currentGeneset.getRemoved()) {
				if(methodSetting.getStringValue().equals("Elim")) {
					Set<String> markedGenes = new TreeSet<String>();
				
					for(Node source: graph.getInNeighbors(n)) {
						Set<String> markedGenesChild=markedGenesMap.get(source);
						if(markedGenesChild!=null) {
							markedGenes.addAll(markedGenesChild);
							
						}
						
					}
			
					Set<String> genesToTest=new TreeSet<String>();
					for(String gene:currentGeneset.getGenes()) {
						if(!markedGenes.contains(gene)) {
							genesToTest.add(gene);
						}
					}
					
					
					Enrichment enr = createEnrichment(currentGeneset);
					calculateTestStatistic(enr,genesToTest);
					// threshold?
					if(enr.getPValue()<=0.01) {
						markedGenes.addAll(genesToTest);
					}
					testResults.add(enr);
					markedGenesMap.put(n, markedGenes);
					
				}
				else {
					testResults.add(computeTermSig(n));
					if(classesEnrichment) {
						progress+= 10000 / ((double)Binomial.binomial(m3_classes.getModel().getNumClasses(),2) * genesets.size());
					}
					else {
						progress+= 10000/ (double)genesets.size();
					}
					t.setProgress((int)progress);
				}
			}
			
		}
		return testResults;
		
	}

	protected Enrichment createEnrichment(Geneset currentGeneset) {
		return new Enrichment(currentGeneset);
	}

	protected void calculateTestStatistic(Enrichment enr, Set<String> genesToTest) {
		calculateTestStatistic(interestingGenes,allGenes.size(),enr,genesToTest);
		
	}

	protected Enrichment computeTermSig(Node n) {
		Geneset g=genesetMap.get(n);
		DoubleVector weights = new DoubleVector(g.getGenes().size());
		
		HashMap<String,Integer> indexMap = new HashMap<String,Integer>();
		int counter=0;
		for(String gene: g.getGenes()){
			weights.set(counter,1.0);
			weights.setName(counter, gene);
			indexMap.put(gene, counter);
			counter++;
		}
		weightsMap.put(n, weights);
		Enrichment newEnrichment = createEnrichment(g);
		enrichmentMap.put(n, newEnrichment);
		
		Set<Node> children = new TreeSet<Node>();
		HashMap<Node,Double> weightUpdate = new HashMap<Node,Double>();
		for(Node source: graph.getInNeighbors(n)) {
			//update weights of node n
			DoubleVector updatesChild = updateMap.get(source);
			if(updatesChild!=null) {
				//add children only if there are weights defined (i. e. the corresponding geneset was not too small)
				children.add(source);
				for(int i=0;i!=updatesChild.size();i++) {
					String name=updatesChild.getName(i);
					double childValue=updatesChild.get(i);
					int index=indexMap.get(name);
					double oldValue=weights.get(index);
					weights.set(index,oldValue*childValue);
				}
			}
		}
		if(children==null || children.size()==0) {
			weightedGenesetTest(n);
			updateMap.put(n, weights.clone());
		}
		while(children!=null && children.size()!=0) {
			weightUpdate.clear();
			double pValue=weightedGenesetTest(n);
			List<Node> sigChildren=new LinkedList<Node>();
			
			for(Node ch:children) {
				double numerator=Math.min(Math.max(enrichmentMap.get(ch).getPValue(),0.00000001),0.99999999);
				double denominator=Math.min(Math.max(pValue,0.0000001),0.99999999);
				//double newWeight=Math.log(numerator)/Math.log(denominator);
				double newWeight=denominator/numerator;
				weightUpdate.put(ch, newWeight);
				if(newWeight>=1) {
					sigChildren.add(ch);
				}
			}	
			if(sigChildren.size()==0) {
				for(Node ch:children) {
					DoubleVector weightsChild = weightsMap.get(ch);
					weightsChild.multiply(weightUpdate.get(ch));
					weightedGenesetTest(ch);
				}
				updateMap.put(n, weights.clone());
				break;
			}
			else {
				for(Node ch:sigChildren) {
					for(String gene:genesetMap.<Geneset>get(ch).getGenes()) {
						int index=indexMap.get(gene);
						double oldValue=weights.get(index);
						weights.set(index,oldValue/weightUpdate.get(ch));
						
					}
				}
			}
			children.removeAll(sigChildren);
		}
		return newEnrichment;
	}
	
	protected double weightedGenesetTest(Node n) {
		double significantGenesInGeneset=0.0;
		double notSignificantGenesInGeneset=0.0;
		
		DoubleVector weights=weightsMap.get(n);
		for(int i=0;i!=weights.size();i++) {
			double weight=weights.get(i);
			String gene = weights.getName(i);
			if(interestingGenes.contains(gene)) {
				significantGenesInGeneset+=weight;
			}
			else {
				notSignificantGenesInGeneset+=weight;
			}
		}
		int n11=(int) Math.round(significantGenesInGeneset);
		int n12=(int) Math.round(notSignificantGenesInGeneset);
		int n21=interestingGenes.size()-n11;
		int n22=allGenes.size()-interestingGenes.size()-n12;
		if(n11==0&&n12==0) {
			n11=(int)Math.ceil(significantGenesInGeneset);
			n12=(int)Math.ceil(significantGenesInGeneset);
		}
		Enrichment e=enrichmentMap.get(n);
		overreptest.getObjectValue().runTest(n11,n12,n21,n22,e);
		
		return e.getPValue();
	}

	@Override
	public List<ProbeList> calculateGraphEnrichment(Graph graph, BidirectionalHashMap<Node, Geneset> genesetMap,
			ProbeList probes, ProbeList probesMasterTable) {
		this.graph=graph;
		this.genesetMap=genesetMap;
		return run(probes,probesMasterTable);
		
	}

}
