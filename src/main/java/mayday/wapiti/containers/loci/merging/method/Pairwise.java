package mayday.wapiti.containers.loci.merging.method;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.genetics.advanced.LocusData;
import mayday.genetics.advanced.StrandFilterIterator;
import mayday.genetics.advanced.chromosome.AbstractLocusChromosome;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.locusmap.LocusMap;
import mayday.wapiti.containers.loci.merging.LocusMergeMethod;
import mayday.wapiti.containers.loci.merging.MergeSortIterator;

public class Pairwise extends LocusMergeMethod {

	protected BooleanSetting removeMinimal;
	protected IntSetting minimalSize;
	protected HierarchicalSetting mySetting;
	
	public Setting getSetting() {
		if (mySetting==null) {
			removeMinimal = new BooleanSetting("Remove small loci","Discard loci below a minimum length?", true);
			minimalSize = new IntSetting("Minimal locus size", null, 10, 0,null,false, false);
			mySetting = new HierarchicalSetting("Restricted merging").addSetting(removeMinimal).addSetting(minimalSize);
		}
		
		return mySetting;
	}
	
	
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".Pairwise", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Pairwise (create a locus from each pair of neighboring coordinates)", 
		"Pairwise (create a locus from each pair of neighboring coordinates)");
	}

	@SuppressWarnings("unchecked")
	public LocusMap run(List<LocusData> input, String name) {
		
		boolean removemin = removeMinimal.getBooleanValue();
		int minsize = minimalSize.getIntValue();
		
		List<ChromosomeSetContainer> lcsc = new LinkedList<ChromosomeSetContainer>();
		Set<Chromosome> commonChromes = new HashSet<Chromosome>();
		
		for (LocusData ld : input) {
			ChromosomeSetContainer csc = ld.asChromosomeSetContainer();
			lcsc.add(csc);
			commonChromes.addAll(csc.getAllChromosomes());
		}
		
		LocusMap lm = new LocusMap(name);	

		for (Strand strand : new Strand[]{Strand.PLUS, Strand.MINUS}) {
			
			for (Chromosome chrome : commonChromes) {			

				List<Iterator<AbstractGeneticCoordinate>> startiters = new LinkedList<Iterator<AbstractGeneticCoordinate>>();
				List<Iterator<AbstractGeneticCoordinate>> enditers = new LinkedList<Iterator<AbstractGeneticCoordinate>>();
				
				for (ChromosomeSetContainer csc : lcsc) {
					AbstractLocusChromosome olc = (AbstractLocusChromosome)csc.getChromosome(chrome.getSpecies(), chrome.getId());
					System.out.println(olc.toString());
					startiters.add(olc.iterateByStartPosition(true));
					enditers.add(olc.iterateByEndPosition(true));
				}
				
				MergeSortIterator<AbstractGeneticCoordinate> starts = new MergeSortIterator<AbstractGeneticCoordinate>(startiters);
				MergeSortIterator<AbstractGeneticCoordinate> ends = new MergeSortIterator<AbstractGeneticCoordinate>(enditers);
				
				Iterator<AbstractGeneticCoordinate> startI = new StrandFilterIterator(starts, strand);
				Iterator<AbstractGeneticCoordinate> endI = new StrandFilterIterator(ends, strand);
				
				long start = -1;
				long end = 0;
				String locusname = null;
				long p1 = -1; 
				long p2 = -1;
				long coveredUntil=0;
				LinkedList<AbstractGeneticCoordinate> coveringLater = new LinkedList<AbstractGeneticCoordinate>();

				AbstractGeneticCoordinate c1=null,c2=null;

				while(startI.hasNext() || endI.hasNext()) {
					
					if (p1==-1 && startI.hasNext()) {
						c1 = startI.next();
						p1 = c1.getFrom();
						coveringLater.add(c1);
					}
					if (p2==-1 && endI.hasNext()) {
						c2 = endI.next();
						p2 = c2.getTo();
					}
					
					long l;
					
					if (p1>-1 && p1<p2) {
						// next position is a start position
						l = p1;
						p1 = -1;						
					} else {
						l = p2;
						p2 = -1;
					}

									
					if (l==-1 && (p1>-1&&p2>-1)) {
						throw new RuntimeException("This is strange and virtually impossible");
					}
						
					
					if (start==-1) {
						start = l;
					} else {
						start = end;
						end = l;			

						if (coveringLater.size()>0 && coveringLater.get(0).getFrom()<=start) {
							coveredUntil = Math.max(coveredUntil, coveringLater.removeFirst().getTo());	
						}
						
//						System.out.println("Looking at "+start+"--"+end+" covered "+coveredUntil);
						// check coverage
						if (start<coveredUntil) {
							if (!removemin || (end-start+1 >= minsize)) {
								GeneticCoordinate gc = new GeneticCoordinate(chrome,strand,start,end);
								// 	find a name
								locusname = gc.toString();
								lm.put(locusname, gc);
								continue;
							}
						}
					}					
				}
			}
		}
	
		return lm; 
	}
	
}
