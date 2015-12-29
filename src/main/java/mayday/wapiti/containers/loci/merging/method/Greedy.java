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
import mayday.genetics.advanced.chromosome.AbstractLocusChromosome;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.locusmap.LocusMap;
import mayday.wapiti.containers.loci.merging.LocusMergeMethod;
import mayday.wapiti.containers.loci.merging.MergeSortIterator;

public class Greedy extends LocusMergeMethod {

	protected BooleanSetting removeMinimal;
	protected IntSetting minimalSize;
	protected IntSetting minimalOverlap;
	protected HierarchicalSetting mySetting;
	
	public Setting getSetting() {
		if (mySetting==null) {
			removeMinimal = new BooleanSetting("Remove small loci","Discard loci below a minimum length?", true);
			minimalSize = new IntSetting("Minimal locus size", null, 10, 0,null,false, false);
			minimalOverlap = new IntSetting("Minimal overlap", "The minimal overlap needed for two loci to be merged.\n" +
					"Positive values specify minimum overlap (stricter)\n" +
					"Negative values specify maximum distance, i.e. no overlap is needed (less strict).\n" +
					"A value of zero joins loci that are directly adjacent", 10);
			mySetting = new HierarchicalSetting("Greedy merging").addSetting(minimalOverlap).addSetting(removeMinimal).addSetting(minimalSize);
		}
		
		return mySetting;
	}
	
	
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".Greedy", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Greedy (combine all overlapping loci)", 
		"Greedy (combine all overlapping loci)");
	}

	@SuppressWarnings("unchecked")
	public LocusMap run(List<LocusData> input, String name) {
		
		boolean removemin = removeMinimal.getBooleanValue();
		int minsize = minimalSize.getIntValue();
		int minOverlap = minimalOverlap.getIntValue();
		
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

				List<Iterator<AbstractGeneticCoordinate>> iters = new LinkedList<Iterator<AbstractGeneticCoordinate>>();
				for (ChromosomeSetContainer csc : lcsc) {
					AbstractLocusChromosome olc = (AbstractLocusChromosome)csc.getChromosome(chrome.getSpecies(), chrome.getId());	
					iters.add( olc.iterateByStartPosition(true) );
				}
				MergeSortIterator<AbstractGeneticCoordinate> msiolgc = new MergeSortIterator<AbstractGeneticCoordinate>(iters, new AGCComparator());
				
				AbstractGeneticCoordinate agc = null;

				while(msiolgc.hasNext()) {
				
					long start = -1;
					long end = -1;
					String locusname = null;
					
					while (msiolgc.hasNext()) {
						if (agc==null)
							 agc = msiolgc.next();
						if (agc.getStrand()!=strand) {
							agc = null;
							continue;
						}
						long newStart = agc.getFrom();
						long newEnd = agc.getTo();
						if (start==-1) {
							start = newStart;
							end = newEnd;
						} else if (newEnd>end){
							long overlap = end - newStart + 1;
							if (overlap>=minOverlap)
								end = newEnd;
							else
								break;
						}
						if (locusname==null && agc instanceof LocusGeneticCoordinateObject) {
							LocusGeneticCoordinateObject<?> olgc = (LocusGeneticCoordinateObject<?>)agc;
							if (olgc.getObject() instanceof String)
								locusname = (String)olgc.getObject();
						}
						agc = null;
					}
					
					if (!removemin || (end-start+1 >= minsize)) {
						GeneticCoordinate gc = new GeneticCoordinate(chrome,strand,start,end);
						// find a name
						if (locusname==null)
							locusname = gc.toString();
						lm.put(locusname, gc);
					}
				}
			}

		}
	
		return lm; 
	}
	
	
	protected static class AGCComparator implements Comparator<AbstractGeneticCoordinate> {

		public int compare(AbstractGeneticCoordinate o1, AbstractGeneticCoordinate o2) {
			Long l1 = o1.getFrom();
			return l1.compareTo(o2.getFrom());
		}
	
	}
	

	
}
