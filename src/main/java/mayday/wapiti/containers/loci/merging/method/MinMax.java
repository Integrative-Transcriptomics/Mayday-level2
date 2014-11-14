package mayday.wapiti.containers.loci.merging.method;

import java.util.Comparator;
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
import mayday.genetics.advanced.chromosome.LocusChromosome;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.locusmap.LocusMap;
import mayday.wapiti.containers.loci.merging.LocusMergeMethod;
import mayday.wapiti.containers.loci.merging.MergeSortIterator;

public class MinMax extends LocusMergeMethod {

	protected BooleanSetting removeMinimal;
	protected IntSetting minimalSize, maximalSize;
	protected HierarchicalSetting mySetting;
	
	public Setting getSetting() {
		if (mySetting==null) {
			removeMinimal = new BooleanSetting("Remove small loci","Discard loci below a minimum length?", true);
			minimalSize = new IntSetting("Minimal locus size", null, 10, 0,null,false, false);
			maximalSize = new IntSetting("Maximal locus size", "Loci are not grown beyond this size", 10, 0,null,false, false);
			mySetting = new HierarchicalSetting("MinMax merging").addSetting(removeMinimal).addSetting(minimalSize).addSetting(maximalSize);
		}
		
		return mySetting;
	}
	
	
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".Minmax", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"MinMax (merge loci in order of decreasing size)", 
		"MinMax (merge loci in order of decreasing size)");
	}

	@SuppressWarnings("unchecked")
	public LocusMap run(List<LocusData> input, String name) {
		
		boolean removemin = removeMinimal.getBooleanValue();
		int minsize = minimalSize.getIntValue();
		int maxsize = maximalSize.getIntValue();
		
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

				List<Iterator<AbstractGeneticCoordinate>> bySizeIterators = new LinkedList<Iterator<AbstractGeneticCoordinate>>();
				List<AbstractLocusChromosome> chromes = new LinkedList<AbstractLocusChromosome>();
				
				for (ChromosomeSetContainer csc : lcsc) {
					AbstractLocusChromosome olc = (AbstractLocusChromosome)csc.getChromosome(chrome.getSpecies(), chrome.getId());
					chromes.add(olc);
					bySizeIterators.add(olc.iterateByLocusSize(false));
				}
				MergeSortIterator<AbstractGeneticCoordinate> bySize 
					= new MergeSortIterator<AbstractGeneticCoordinate>(bySizeIterators, new DecreasingSizeComparator());
				
				StrandFilterIterator iter = new StrandFilterIterator(bySize, strand);
				
				LocusChromosome finishedGroups = new LocusChromosome(null, "tmp", 0);
				
				while(iter.hasNext()) {
					
					while(iter.hasNext()) {
						
						// pick the seed for this overlaygroup
						long start = -1;
						long end = -1;

						while (iter.hasNext() && start==-1) {
							AbstractGeneticCoordinate agc = iter.next();
							start = agc.getFrom();
							end = agc.getTo();
							// check if this completely overlapped by a known locus
							if (finishedGroups.isCompletelyOverlapped(start, end, strand)) 
								start=-1;							
						}

						if (start==-1) 
							continue; //this one is a fail
							
						// grow until maxsize
						LinkedList<AbstractGeneticCoordinate> candidates = new LinkedList<AbstractGeneticCoordinate>();
						for (AbstractLocusChromosome alc : chromes)
							candidates.addAll(alc.getOverlappingLoci(start, end, strand));
						
						for (AbstractGeneticCoordinate cand : candidates) {
							finishedGroups.trimByOverlapping(cand);
							long c_start = Math.min(start, cand.getFrom());
							long c_end = Math.max(end, cand.getTo());
							if (c_end-c_start+1 < maxsize) {
								end = c_end;
								start = c_start;
							}
						}
						
						if (!removemin || end-start+1 > minsize) {
							GeneticCoordinate gc = new GeneticCoordinate(chrome,strand,start,end);
							String locusname = gc.toString();
							lm.put(locusname, gc);
							finishedGroups.addLocus(start, end, strand);
						}
						
						
					}
				}
			}

		}
	
		return lm; 
	}
	
	
	protected static class DecreasingSizeComparator implements Comparator<AbstractGeneticCoordinate> {

		public int compare(AbstractGeneticCoordinate o1, AbstractGeneticCoordinate o2) {
			Long l1 = o1.length();
			return - l1.compareTo(o2.length());
		}
	
	}
	

	
}
