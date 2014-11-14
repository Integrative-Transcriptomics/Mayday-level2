package mayday.wapiti.containers.loci.peakfind;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.ExtendableObjectListSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.Triple;
import mayday.core.tasks.AbstractTask;
import mayday.genetics.advanced.LocusData;
import mayday.genetics.advanced.chromosome.AbstractLocusChromosome;
import mayday.genetics.advanced.chromosome.AbstractLocusGeneticCoordinate;
import mayday.genetics.advanced.chromosome.LocusChromosome;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinate;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.locusmap.LocusMap;
import mayday.genetics.locusmap.LocusMapContainer;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.transformations.matrix.TransMatrix;

/**
 * a very very simple peak finding method.
 * Only consideres primitive genetic coordinates, i.e. a peak can also be called at a position where enough
 * elements SPAN but do not COVER the bases.
 * @author battke
 *
 */
public class PeakFinder {

	@SuppressWarnings("unchecked")
	public static void run(TransMatrix transMatrix, AbstractTask task) {

		Set<LocusData> av = new HashSet<LocusData>();

		for (Experiment e : transMatrix.getExperiments())
			if (e.hasLocusInformation())
				av.add(e.getLocusData());		

		StringSetting ss = new StringSetting( "Locus Set Name", "Specify a new name for the created Locus Data", "", false);
		PeakFinderSetting pfs = new PeakFinderSetting("Locus quality parameters");
		ExtendableObjectListSetting<LocusData> inputdata = 
			new ExtendableObjectListSetting<LocusData>("Input Locus Data",
					"Select at least one mapping experiment as a source for generating locus data",
					new LinkedList<LocusData>(av));

		HierarchicalSetting hs = new HierarchicalSetting("Peak finding from mapped reads")
		.addSetting(ss)
		.addSetting(inputdata)
		.addSetting(pfs)
		;

		SettingDialog sd = new SettingDialog(transMatrix.getFrame(), hs.getName(), hs);
		sd.showAsInputDialog();

		LocusMap m2 = new LocusMap(ss.getStringValue());

		if (!sd.canceled()) {
//			long t1 = System.currentTimeMillis();

			int minReads = pfs.getMinReads();
			int maxDist = pfs.getMaxDistance();
			double minCover = pfs.getMinCoverage();
			int minSize = pfs.getMinSize();

			List<ChromosomeSetContainer> lcsc = new LinkedList<ChromosomeSetContainer>();
			Set<Chromosome> commonChromes = new HashSet<Chromosome>();

			for (LocusData ld : inputdata.getSelection()) {
				ChromosomeSetContainer csc = ld.asChromosomeSetContainer();
				lcsc.add(csc);
				commonChromes.addAll(csc.getAllChromosomes());
			}

			ChromosomeSetContainer target_csc = new ChromosomeSetContainer(new LocusChromosome.Factory());

			// we work on each chromosome separately to save memory

			int percCount = 2*commonChromes.size();
			int percCur = 0;
			
			for (Chromosome chrome : commonChromes) {

				// 1) Maximally extend all overlapped regions and create initial loci for them
				LocusChromosome coveredBases_fwd = new LocusChromosome(null, "fwd",0);
				LocusChromosome coveredBases_bwd = new LocusChromosome(null, "bwd",0);

				for (ChromosomeSetContainer source_csc : lcsc) {
					AbstractLocusChromosome source_chr = (AbstractLocusChromosome)source_csc.getChromosome(chrome);
					Iterator<AbstractLocusGeneticCoordinate> i = source_chr.iterateUnsorted();
					while (i.hasNext()) {
						AbstractLocusGeneticCoordinate agc = i.next();
						if (agc.getStrand().similar(Strand.PLUS))
							coveredBases_fwd.addLocus(agc.getFrom(), agc.getTo(), agc.getStrand());
						if (agc.getStrand().similar(Strand.MINUS))
							coveredBases_bwd.addLocus(agc.getFrom(), agc.getTo(), agc.getStrand());
					}
				}

				LocusChromosome target_chr = (LocusChromosome)target_csc.getChromosome(chrome);

				// Now work on each strand separately
				for (Strand s : new Strand[]{Strand.PLUS, Strand.MINUS}) {
					
					LocusChromosome coveredBases = (s==Strand.PLUS)?coveredBases_fwd:coveredBases_bwd;

					Iterator<Long> coveredPositions = coveredBases.iterateAllCoveredPositions();

					TreeSet<Long> starts = new TreeSet<Long>();
					TreeSet<Long> ends = new TreeSet<Long>();
					boolean startNewComponent = true;
					
					String progStr = "Working on chromosome "+chrome.toString()+" "+s.toChar();

					if (task!=null) {
						task.setProgress(10000*percCur/percCount, progStr+", currently "+m2.size()+" peaks");
					}
					
					// work on each extension group
					while (coveredPositions.hasNext()) {

						// 2) collect all start/end positions in the current extension group
						long lastCovered=Long.MIN_VALUE;
						boolean maxNotExceeded = true;

						while (coveredPositions.hasNext() && maxNotExceeded) {
							long covered=coveredPositions.next();
							// begin a new component
							maxNotExceeded = lastCovered<0 || (covered-lastCovered) < maxDist; 
							if (startNewComponent && lastCovered<covered-1) {
								if (maxNotExceeded)
									starts.add(covered);
								// finish the last component
								if (lastCovered>=0)
									ends.add(lastCovered);
							}
							// add the last end position also
							if (!coveredPositions.hasNext() && maxNotExceeded)
								ends.add(lastCovered);
							lastCovered = covered;
							startNewComponent = true;
						} 

						// 3) now work on this extension group:

						if (minCover>0) {
							// 4) if coverage filter is active, compute each possible extension
							TreeSet<Candidate> possibleLoci = new TreeSet<Candidate>();
							// this set is now sorted by the first component of the triple
							
							for (long ls : starts) {
								for (long le : ends) {
									if (le-ls+1 >= minSize) {
										List<LocusGeneticCoordinate> lsgc = coveredBases.getOverlappingLoci(ls,le,s);
										// check for minimum reads
										if (lsgc.size() >= minReads) {
											double coverage = 0;
											for (LocusGeneticCoordinate slgc : lsgc)
												coverage+=slgc.getOverlappingBaseCount(ls, le);
											int locSize = (int)(le-ls+1);
											coverage /= (double)locSize;
											if (coverage>=minCover)
												possibleLoci.add(new Candidate(coverage,ls,le));
										}
										
									}
								}
							}
							
//							System.out.println("-- Checking group "+Collections.min(starts)+" - "+Collections.max(ends));

							while (possibleLoci.size()>0) {
								// get the best one
								Candidate best = possibleLoci.last();
								
//								System.out.println("From "+possibleLoci.size()+"\t pick "+best.start()+"\t"+best.end()+"\tscoring "+best.coverage());
								
								GeneticCoordinate newLocus = new GeneticCoordinate(target_chr, s, best.start(), best.end());
								m2.put(newLocus.toString(), newLocus);
								
								if (task!=null && m2.size() % 1000 == 0) {
									task.setProgress(10000*percCur/percCount, progStr+", currently "+m2.size()+" peaks");
								}
								
								// remove this element
								possibleLoci.remove(best);

								// remove all pairs that overlap this one
								Iterator<Candidate> ii = possibleLoci.iterator();
								while (ii.hasNext()) {
									Candidate nextLocus = ii.next();
									if (nextLocus.end()>best.start() && nextLocus.start()<best.end()) 
										ii.remove();										
									
								}
							}

						} else {
							// 6) if coverage filter is inactive, use maximally extended locus 
							long start = starts.first();
							long end = ends.last();
							long size = end-start+1;
							if (size>minSize) {
								// size ok, check readcount  
								List<LocusGeneticCoordinate> lsgc = coveredBases.getOverlappingLoci(start,end,s);
								if (lsgc.size()>=minReads) {
									GeneticCoordinate newLocus = new GeneticCoordinate(target_chr, s, start, end);
									m2.put(newLocus.toString(), newLocus);
									if (task!=null && m2.size() % 1000 == 0) {
										task.setProgress(10000*percCur/percCount, progStr+", currently "+m2.size()+" peaks");
									}

								}
							}
						}

						// finally: prepare for the next extension group
						starts.clear();
						ends.clear();
						if (!maxNotExceeded) {
							starts.add(lastCovered);
							startNewComponent = false;
						}

					}
					
					++percCur;
					
				}
//				long t2 = System.currentTimeMillis();
//				System.out.println(+ ": Runtime "+(t2-t1)+" ms");
//
//				try {
//					BufferedWriter bw = new BufferedWriter(new FileWriter("/tmp/"+m2.getName()));
//					for (String s : m2.keySet())
//						bw.write(s+"\n");
//					bw.flush();
//					bw.close();
//
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
				
			}

			if (JOptionPane.showConfirmDialog(null, 
					"Peak finding resulted in "+m2.size()+" loci.\n" +
					"Do you want to keep the result?", "Peak finding completed", 
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION)
				LocusMapContainer.INSTANCE.add(m2);
		}


	}
	
	private static class Candidate extends Triple<Double,Long,Long>{

		public Candidate(Double one, Long two, Long three) {
			super(one, two, three);
		}
		
		@SuppressWarnings("unused")
		public double coverage() {
			return ONE;
		}

		public long start() {
			return TWO;
		}
		
		public long end() {
			return THREE;
		}
		
		
	};

}
