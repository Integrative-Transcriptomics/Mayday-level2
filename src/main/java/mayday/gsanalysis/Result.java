package mayday.gsanalysis;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import mayday.core.structures.Pair;

public class Result {
	private HashMap<Geneset,HashMap<Pair<String,String>,Enrichment>> resultObject;
	private boolean enrichmentWithClasses;
	
	public Result() {
		resultObject = new HashMap<Geneset,HashMap<Pair<String,String>,Enrichment>>();
		enrichmentWithClasses = false;
	}
	
	private void setEnrichmentWithClasses() {
		enrichmentWithClasses=true;
	}
	
	public boolean getEnrichmentWithClasses() {
		return enrichmentWithClasses;
	}
	
	public Enrichment getEnrichment(Geneset g, Pair<String,String> classes) {
		if(resultObject.containsKey(g)) {
			return resultObject.get(g).get(classes);
		}
		else {
			return null;
		}
	}
	
	public Enrichment getEnrichment(Geneset g) {
		return getEnrichment(g,null);
		
	}
	
	public HashMap<Pair<String,String>, Enrichment> getEnrichments(Geneset g) {
		return resultObject.get(g);
	}
	
	public HashMap<Geneset,Enrichment> getEnrichments(Pair<String,String> classes) {
		HashMap<Geneset,Enrichment> h = new HashMap<Geneset,Enrichment>();
		for(Geneset g: resultObject.keySet()) {
			h.put(g, getEnrichment(g,classes));
		}
		return h;
	}
	
	public List<Enrichment> getSortedEnrichments(Pair<String,String> classes) {
		LinkedList<Enrichment> list = new LinkedList<Enrichment>();
		for(Enrichment e: getEnrichments(classes).values()) {
			list.add(e);
		}
		Collections.sort(list);
		Collections.reverse(list);
		return list;
	}
	
	
	
	public void addEnrichment(Geneset g, Pair<String,String> classes, Enrichment e) {
		if(!enrichmentWithClasses && classes!=null) {
			setEnrichmentWithClasses();
		}
		if(resultObject.containsKey(g)==false) {
			resultObject.put(g, new HashMap<Pair<String,String>,Enrichment>());
		}
		resultObject.get(g).put(classes, e);
		
	}
	
	public void addEnrichment(Geneset g, Enrichment e) {
		addEnrichment(g,null,e);
	}
	
	public void addEnrichments(Pair<String,String> classes, List<Enrichment> enrichments) {
		for(Enrichment e:enrichments) {
			Geneset g = e.getGeneset();
			addEnrichment(g,classes,e);
		}
	}
	
	public void addEnrichments(List<Enrichment> enrichments) {
		addEnrichments(null,enrichments);
	}
	
	public Set<Pair<String,String>> getClassCombinations() {
		Iterator<Geneset> i = resultObject.keySet().iterator();
		if(i.hasNext()) {
			return resultObject.get(i.next()).keySet();
		}
		else {
			return null;
		}
	}
	
	public void printEnrichments(BufferedWriter writer) {
		try {
			String firstLine="";
			if(enrichmentWithClasses) {
				firstLine="Class1\tClass2\t" + getSortedEnrichments(getClassCombinations().iterator().next()).get(0).getNameOfValues();
			}
			else {
				firstLine=getSortedEnrichments(null).get(0).getNameOfValues();
			}
			writer.write(firstLine);
			writer.newLine();
			
			if(enrichmentWithClasses) {
				for(Pair<String,String> classPair : getClassCombinations()) {
					for(Enrichment e: getSortedEnrichments(classPair)) {
						writer.write(classPair.getFirst()+ "\t" + classPair.getSecond() + "\t" + e.toString());
						writer.newLine();
					}
				}	
			}
			else {
				for(Enrichment e: getSortedEnrichments(null)) {
					writer.write(e.toString());
					writer.newLine();
				}
			}
			writer.close();
		
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void printEnrichments() {
		printEnrichments(new BufferedWriter(new OutputStreamWriter(System.out)));
	}
	
	
	
}
