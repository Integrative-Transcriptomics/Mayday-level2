package mayday.Reveal.actions.snplist;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.Haplotypes;
import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.data.Subject;
import mayday.Reveal.data.SubjectList;
import mayday.Reveal.data.meta.Genome;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.data.meta.snpcharacterization.SNPCharacterization;
import mayday.Reveal.data.meta.snpcharacterization.SNPCharacterizations;
import mayday.Reveal.functions.CodonsAminoacids;
import mayday.Reveal.functions.CodonsAminoacids.Aminoacid;
import mayday.Reveal.functions.CodonsAminoacids.Codon;
import mayday.Reveal.io.gff3.ChromosomalLocation;
import mayday.Reveal.io.gff3.GFFElement;
import mayday.Reveal.io.gff3.GFFTree;
import mayday.Reveal.settings.SubjectListSetting;
import mayday.Reveal.utilities.SNPLists;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.LongSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.tasks.AbstractTask;

public class SNPCharacterizationPlugin extends SNPListPlugin {

	@Override
	public String getName() {
		return "SNP Characterization";
	}

	@Override
	public String getType() {
		return "data.snplist.idnonsynsnps";
	}

	@Override
	public String getDescription() {
		return "Characterize a list of snps based on given gff annotations";
	}

	@Override
	public String getMenuName() {
		return "SNP Characterization";
	}

	@Override
	public void run(Collection<SNPList> snpLists) {
		if(snpLists == null) {
			JOptionPane.showMessageDialog(null, "No SNPList selected!");
			return;
		}
		
		DataStorage ds = projectHandler.getSelectedProject();
		List<MetaInformation> gff3mios = ds.getMetaInformationManager().get(GFFTree.MYTYPE);
		
		if(gff3mios == null || gff3mios.size() == 0) {
			JOptionPane.showMessageDialog(null, "No genome annotation information available");
			return;
		}
		
		String[] gff3Names = new String[gff3mios.size()];
		
		for(int i = 0; i < gff3mios.size(); i++) {
			gff3Names[i] = gff3mios.get(i).toString();
		}
		
		HierarchicalSetting setting = new HierarchicalSetting("SNP Characterization Setting");
		RestrictedStringSetting gff3Selection = new RestrictedStringSetting("Select GFF3 Annotation", null, 0, gff3Names);
		
		setting.addSetting(gff3Selection);
		
		LongSetting upstreamRegion = new LongSetting("Upstream Region", "Specify the number of nucleotides upstream that should be considered for characterization.", 1000);
		LongSetting downstreamRegion = new LongSetting("Downstream Region", "Specify the number of nucleotides downstream that should be considered for characterization", 1000);
		
		SubjectList persons = ds.getSubjects();
		SubjectListSetting personSetting = new SubjectListSetting(persons);
		
		HierarchicalSetting characterizationSettings = new HierarchicalSetting("Characterization Settings");
		characterizationSettings.addSetting(upstreamRegion);
		characterizationSettings.addSetting(downstreamRegion);
		
		setting.addSetting(personSetting);
		setting.addSetting(characterizationSettings);

		SettingDialog dialog = new SettingDialog(null, setting.getName(), setting);
		dialog.showAsInputDialog();
		
		if(dialog.closedWithOK()) {
			SNPList snps = SNPLists.createUniqueSNPList(snpLists);
			GFFTree tree = (GFFTree)gff3mios.get(gff3Selection.getSelectedIndex());
			Genome genome = ds.getGenome();
			SubjectList selPersons = personSetting.getSelectedSubjects();
			
			if(genome == null) {
				JOptionPane.showMessageDialog(null, "No genome available");
				return;
			}
			
			long upstream = upstreamRegion.getLongValue();
			long downstream = downstreamRegion.getLongValue();
			
			this.characterizeSNPs(selPersons, snps, tree, genome, upstream, downstream);
		}
	}
	
	public void characterizeSNPs(final SubjectList persons, final SNPList snps, final GFFTree tree, final Genome genome, final long upstream, final long downstream) {
		AbstractTask task = new AbstractTask("Characterize SNPs") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				DataStorage ds = projectHandler.getSelectedProject();
				
				writeLog("Starting SNP characterization ...\n\t");
				writeLog("Number of subjects:\t" + persons.size() + "\n\t");
				writeLog("Number of SNPs:\t" + snps.size() + "\n\t");
				writeLog("Genome:\t\t" + genome + "\n\t");
				writeLog("Genome annotations:\t" + tree + "\n");
				
				SNPCharacterizations snpChars = new SNPCharacterizations();
				
				int count = 0;
				for(Subject person : persons) {
					for(int i = 0; i < snps.size(); i++) {
						count++;
						SNP s = snps.get(i);
						
						String chromosome = genome.getOriginalSequenceName(s.getChromosome());
						int position = s.getPosition();
						
						Map<GFFElement, Integer> overlappingElements = tree.getElementsAtPosition(chromosome, position, upstream, downstream);
						
						for(GFFElement e : overlappingElements.keySet()) {
							SNPCharacterization snpChar = characterizeSNPs(person, genome, e, s);
							snpChars.add(snpChar);
						}
						
						int progress = (int)Math.rint((((double)(count) / (snps.size() * persons.size())) * 10000));
						setProgress(progress);
						
						if(hasBeenCancelled()) {
							snpChars.clear();
							return;
						}
					}
				}

				if(!hasBeenCancelled()) {
					if(snpChars.size() > 0)
						ds.getMetaInformationManager().add(SNPCharacterizations.MYTYPE, snpChars);
					else {
						JOptionPane.showMessageDialog(null, "No interesting regions found for the selected SNPs");
					}
				}
			}
		};
		
		task.start();
	}
	
	private SNPCharacterization characterizeSNPs(Subject p, Genome g, GFFElement e, SNP snp) throws Exception {
		SNPCharacterization snpChar = new SNPCharacterization(p.getID(), p.getName(), snp);
		
		CodonsAminoacids cas = new CodonsAminoacids();
		ChromosomalLocation loc = e.getChromosomalLocation();
		String nucSeq = g.getSequence(loc.getChromosome(), loc.getStart(), loc.getStop());
		
		int phase = e.getPhase();
		char strand = loc.getStrand();
		
		DataStorage ds = projectHandler.getSelectedProject();
		Haplotypes phs = ds.getHaplotypes().get(p.getIndex());
		
		char snpA = phs.getSNPA(snp.getIndex());
		char snpB = phs.getSNPB(snp.getIndex());
		
		snpChar.setIndividualNucleotideA(snpA);
		snpChar.setIndividualNucleotideB(snpB);
		
		//snp lies on the '+' strand
		if(strand == '+') {
			//correct for phase differences
			nucSeq = nucSeq.substring(phase);
			
			String feature = e.getFeature().toLowerCase();
			
			//snp hits a coding feature
			if(feature.contains("gene") || feature.contains("mrna") || feature.contains("transcript") || feature.contains("cds")) {
				
				int startCodonPosition = getFirstStartCodonPosition(nucSeq, cas);
				int stopCodonPosition = getFirstStopCodonPosition(nucSeq, cas, startCodonPosition);
				
				//transform to global coordinates
				startCodonPosition += loc.getStart();
				stopCodonPosition += loc.getStart();
				
				//snp hits coding region
				if(snp.getPosition() >= startCodonPosition && snp.getPosition() <= stopCodonPosition+2) {
					//get original codon and modified codons
					int localSNPPos = getLocalSNPPosition(e, snp);
					String originalCodon = getCodon(nucSeq, localSNPPos);
					String modifiedCodonA = modifyCodon(originalCodon, snpA, localSNPPos);
					String modifiedCodonB = modifyCodon(originalCodon, snpB, localSNPPos);
					
					//translate the codons
					String prot = translateCodon(originalCodon, cas);
					String protModifiedA = translateCodon(modifiedCodonA, cas);
					String protModifiedB = translateCodon(modifiedCodonB, cas);
					
					//check if the original codon has changed
					boolean sameA = protModifiedA.equals(prot);
					boolean sameB = protModifiedB.equals(prot);
					
					//update the characterization object
					snpChar.setOriginalAA(prot);
					snpChar.setModifiedAAA(protModifiedA);
					snpChar.setModifiedAAB(protModifiedB);
					snpChar.setIsModifiedAAA(!sameA);
					snpChar.setIsModifiedAAB(!sameB);
					
					//if snp is non-synonymous
					if(snpChar.nonSynonymous()) {
						int globalSNPPos = snp.getPosition();
						
						//start lost
						if(globalSNPPos >= startCodonPosition && globalSNPPos <= startCodonPosition+2) {
							snpChar.setCharacterizationFeature(SNPCharacterization.START_CODON_LOST);
						}
						//stop lost
						else if(globalSNPPos >= stopCodonPosition && globalSNPPos <= stopCodonPosition+2) {
							/*
							 * since we have a non-synonymous change in the stop codon
							 * we immediately know that no other stop codon was introduced but some different AA
							 * otherwise we would have declared it as a synonymous change since the modified AA would aloso be '*'
							 */
							snpChar.setCharacterizationFeature(SNPCharacterization.STOP_LOST);
						} else {
							//some AA between start and stop is modified!
							//check for stop codon introduction
							if(cas.isStopCodon(modifiedCodonA) || cas.isStopCodon(modifiedCodonB)) {
								snpChar.setCharacterizationFeature(SNPCharacterization.STOP_GAINED);
							} else { //the new codon is not a stop codon
								snpChar.setCharacterizationFeature(-1);
							}
						}
					}
					//snp is synonymous
					else {
						//if the snp is synonymous we are done since we don't need to have a look at the exact change
						snpChar.setCharacterizationFeature(-1);
					}
				}
				//snp hits the non-coding region of a coding feature
				else {
					int globalSNPPos = snp.getPosition();
					
					/*
					 * in the non-coding region we have to distinguish two cases:
					 * 1. SNP is upstream the start codon
					 * 	- then either a new start codon can be introduced
					 *  - or the upstream region is modified such that no new start codon is introduced
					 * 2. SNP is downstream of the stop codon
					 *  - such regions are not important for protein formation and function
					 */
					
					//in the 5' UTR
					if(globalSNPPos < startCodonPosition) {
						//get the position of the probably new start codon
						int localSNPPos = (3 - ((startCodonPosition - globalSNPPos) % 3)) % 3;
						int snpStartCodonPosition = globalSNPPos - localSNPPos;
						
						//fetch 2 codons because of phase, to have enough sequence
						String seq = g.getSequence(loc.getChromosome(), snpStartCodonPosition, snpStartCodonPosition + 6);
						//correct for phase
						seq = seq.substring(phase);
						//translate first codon
						String codon = getCodon(seq, 0);
						String modifiedCodonA = modifyCodon(codon, snpA, 0);
						String modifiedCodonB = modifyCodon(codon, snpB, 0);
						
						//translate the codons
						String prot = translateCodon(codon, cas);
						String protModifiedA = translateCodon(modifiedCodonA, cas);
						String protModifiedB = translateCodon(modifiedCodonB, cas);
						
						//check if the original codon has changed
						boolean sameA = protModifiedA.equals(prot);
						boolean sameB = protModifiedB.equals(prot);
						
						//update the characterization object
						snpChar.setOriginalAA(prot);
						snpChar.setModifiedAAA(protModifiedA);
						snpChar.setModifiedAAB(protModifiedB);
						snpChar.setIsModifiedAAA(!sameA);
						snpChar.setIsModifiedAAB(!sameB);
						
						if(snpChar.nonSynonymous()) {
							if(cas.isStartCodon(modifiedCodonA) || cas.isStartCodon(modifiedCodonB)) {
								snpChar.setCharacterizationFeature(SNPCharacterization.FIVE_PRIME_UTR_START_CODON_INSERTED);
							} else {
								snpChar.setCharacterizationFeature(SNPCharacterization.FIVE_PRIME_UTR);
							}
						}
						
						snpChar.setCharacterizationFeature(SNPCharacterization.FIVE_PRIME_UTR_SYNONYMOUS);
					} 
					//in the 3' UTR
					else if(globalSNPPos > startCodonPosition) {
						snpChar.setCharacterizationFeature(SNPCharacterization.THREE_PRIME_UTR);
					}
				}
			}
			
			//snp hits an exon
			else if(feature.contains("exon")) {
				//check first / last exon
				int numChildren = e.getParent().numChildren();
				int minExonStart = Integer.MAX_VALUE;
				int maxExonEnd = Integer.MIN_VALUE;
				
				for(int i = 0; i < numChildren; i++) {
					GFFElement e2 = e.getParent().getChild(i);
					if(e2.getFeature().toLowerCase().contains("exon")) {
						if(e2.getChromosomalLocation().getStrand() == strand) {
							minExonStart = Math.min(minExonStart, e2.getChromosomalLocation().getStart());
							maxExonEnd = Math.max(maxExonEnd, e2.getChromosomalLocation().getStop());
						}
					}
				}
				
				boolean firstExon = false;
				boolean lastExon = false;
				
				boolean acceptor = false;
				boolean donor = false;
				
				firstExon = loc.getStart() == minExonStart ? true : false;
				lastExon = loc.getStop() == maxExonEnd ? true : false;
				
				if(!firstExon) {
					//splice acceptor site (AG just before an exon)
					if(loc.getStart() > snp.getPosition() 
							&& loc.getStart() - snp.getPosition() <= 1) {
						acceptor = true;
						snpChar.setCharacterizationFeature(SNPCharacterization.SPLICE_SITE_ACCEPTOR);
					}
				}
				
				if(!lastExon) {
					//splice donor site (GU just after an exon)
					if(snp.getPosition() > loc.getStop() 
							&& snp.getPosition() - loc.getStop() <= 1) {
						donor = true;
						snpChar.setCharacterizationFeature(SNPCharacterization.SPLICE_SITE_DONOR);
					}
				}
				
				//not acceptor and not donor -> must be an intron
				if(!acceptor && !donor) {
					if(firstExon) {
						if(snp.getPosition() < loc.getStart()) { //upstream of first exon -> 5' UTR
							int globalSNPPos = snp.getPosition();
							String exonSeq = g.getSequence(loc.getChromosome(), loc.getStart(), loc.getStop());
							int startCodonPosition = getFirstStartCodonPosition(exonSeq, cas);
							//to global position
							startCodonPosition += loc.getStart();
							
							//get the position of the probably new start codon
							int localSNPPos = (3 - ((startCodonPosition - globalSNPPos) % 3)) % 3;
							int snpStartCodonPosition = globalSNPPos - localSNPPos;
							
							//fetch 2 codons because of phase, to have enough sequence
							String seq = g.getSequence(loc.getChromosome(), snpStartCodonPosition, snpStartCodonPosition + 6);
							//correct for phase
							seq = seq.substring(phase);
							//translate first codon
							String codon = getCodon(seq, 0);
							String modifiedCodonA = modifyCodon(codon, snpA, 0);
							String modifiedCodonB = modifyCodon(codon, snpB, 0);
							
							//translate the codons
							String prot = translateCodon(codon, cas);
							String protModifiedA = translateCodon(modifiedCodonA, cas);
							String protModifiedB = translateCodon(modifiedCodonB, cas);
							
							//check if the original codon has changed
							boolean sameA = protModifiedA.equals(prot);
							boolean sameB = protModifiedB.equals(prot);
							
							//update the characterization object
							snpChar.setOriginalAA(prot);
							snpChar.setModifiedAAA(protModifiedA);
							snpChar.setModifiedAAB(protModifiedB);
							snpChar.setIsModifiedAAA(!sameA);
							snpChar.setIsModifiedAAB(!sameB);
							
							if(snpChar.nonSynonymous()) {
								if(cas.isStartCodon(modifiedCodonA) || cas.isStartCodon(modifiedCodonB)) {
									snpChar.setCharacterizationFeature(SNPCharacterization.FIVE_PRIME_UTR_START_CODON_INSERTED);
								} else {
									snpChar.setCharacterizationFeature(SNPCharacterization.FIVE_PRIME_UTR);
								}
							}
							
							snpChar.setCharacterizationFeature(SNPCharacterization.FIVE_PRIME_UTR_SYNONYMOUS);
						} else if(snp.getPosition() > loc.getStop()){ //down stream of first exon -> Intron
							snpChar.setCharacterizationFeature(SNPCharacterization.INTRON);
						} else { //snp hits somewhere in the exon
							snpChar.setCharacterizationFeature(SNPCharacterization.EXON);
						}
					} else if(lastExon) {
						if(snp.getPosition() < loc.getStart()) { //upstream of last exon -> Intron 
							snpChar.setCharacterizationFeature(SNPCharacterization.INTRON);
						} else if(snp.getPosition() > loc.getStop()) { //downstream of last exon -> 3' UTR
							snpChar.setCharacterizationFeature(SNPCharacterization.THREE_PRIME_UTR);
						} else { //Inside last exon
							snpChar.setCharacterizationFeature(SNPCharacterization.EXON);
						}
					} else { //some exon between two introns
						if(snp.getPosition() < loc.getStart()) { //upstream of the exon -> Intron
							snpChar.setCharacterizationFeature(SNPCharacterization.INTRON);
						} else if(snp.getPosition() > loc.getStop()){ //down stream of the exon -> Intron
							snpChar.setCharacterizationFeature(SNPCharacterization.INTRON);
						} else { //snp hits somewhere in the exon
							snpChar.setCharacterizationFeature(SNPCharacterization.EXON);
						}
					}
				}
			}
			
			//snp hits something else
			else {
				snpChar.setCharacterizationFeature(-1);
			}
		} 
		//snp lies on the '-' strand
		else {
			//correct for phase differences
			nucSeq = nucSeq.substring(0, nucSeq.length() - phase);
			
			String feature = e.getFeature().toLowerCase();
			
			//snp hits a coding feature
			if(feature.contains("gene") || feature.contains("mrna") || feature.contains("transcript") || feature.contains("cds")) {
				
				String rNucSeq = reverseComplement(nucSeq);
				
				int startCodonPosition = getFirstStartCodonPosition(rNucSeq, cas);
				int stopCodonPosition = getFirstStopCodonPosition(rNucSeq, cas, startCodonPosition);
				
				//transform to global coordinates
				startCodonPosition = startCodonPosition + loc.getStart();
				stopCodonPosition = stopCodonPosition + loc.getStart();
				
				//snp hits coding region
				if(snp.getPosition() >= stopCodonPosition-2 && snp.getPosition() <= startCodonPosition) {
					//get original codon and modified codons
					int localSNPPos = loc.getStop() - getLocalSNPPosition(e, snp);
					String originalCodon = getCodon(rNucSeq, localSNPPos);
					String modifiedCodonA = modifyCodon(originalCodon, snpA, localSNPPos);
					String modifiedCodonB = modifyCodon(originalCodon, snpB, localSNPPos);
					
					//translate the codons
					String prot = translateCodon(originalCodon, cas);
					String protModifiedA = translateCodon(modifiedCodonA, cas);
					String protModifiedB = translateCodon(modifiedCodonB, cas);
					
					//check if the original codon has changed
					boolean sameA = protModifiedA.equals(prot);
					boolean sameB = protModifiedB.equals(prot);
					
					//update the characterization object
					snpChar.setOriginalAA(prot);
					snpChar.setModifiedAAA(protModifiedA);
					snpChar.setModifiedAAB(protModifiedB);
					snpChar.setIsModifiedAAA(!sameA);
					snpChar.setIsModifiedAAB(!sameB);
					
					//if snp is non-synonymous
					if(snpChar.nonSynonymous()) {
						int globalSNPPos = snp.getPosition();
						
						//start lost
						if(globalSNPPos >= startCodonPosition-2 && globalSNPPos <= startCodonPosition) {
							snpChar.setCharacterizationFeature(SNPCharacterization.START_CODON_LOST);
						}
						//stop lost
						else if(globalSNPPos >= stopCodonPosition-2 && globalSNPPos <= stopCodonPosition) {
							/*
							 * since we have a non-synonymous change in the stop codon
							 * we immediately know that no other stop codon was introduced but some different AA
							 * otherwise we would have declared it as a synonymous change since the modified AA would aloso be '*'
							 */
							snpChar.setCharacterizationFeature(SNPCharacterization.STOP_LOST);
						} else {
							//some AA between start and stop is modified!
							//check for stop codon introduction
							if(cas.isStopCodon(modifiedCodonA) || cas.isStopCodon(modifiedCodonB)) {
								snpChar.setCharacterizationFeature(SNPCharacterization.STOP_GAINED);
							} else { //the new codon is not a stop codon
								snpChar.setCharacterizationFeature(-1);
							}
						}
					}
					//snp is synonymous
					else {
						//if the snp is synonymous we are done since we don't need to have a look at the exact change
						snpChar.setCharacterizationFeature(-1);
					}
				}
				//snp hits the non-coding region of a coding feature
				else {
					int globalSNPPos = snp.getPosition();
					
					/*
					 * in the non-coding region we have to distinguish two cases:
					 * 1. SNP is upstream the start codon
					 * 	- then either a new start codon can be introduced
					 *  - or the upstream region is modified such that no new start codon is introduced
					 * 2. SNP is downstream of the stop codon
					 *  - such regions are not important for protein formation and function
					 */
					
					//in the 5' UTR
					if(globalSNPPos > startCodonPosition) {
						//get position of the potential new start codon
						int localSNPPos = (3 - (globalSNPPos - startCodonPosition) % 3) % 3;
						int snpStartCodonPosition = globalSNPPos + localSNPPos;
						
						//fetch 2 codons because of phase, to have enough sequence
						String seq = g.getSequence(loc.getChromosome(), snpStartCodonPosition-6, snpStartCodonPosition);
						//use the reverse complement since we are on the '-' strand
						String rseq = reverseComplement(seq);
						//correct for phase
						rseq = rseq.substring(phase);
						
						//translate first codon
						String codon = getCodon(rseq, 0);
						String modifiedCodonA = modifyCodon(codon, snpA, 0);
						String modifiedCodonB = modifyCodon(codon, snpB, 0);
						
						//translate the codons
						String prot = translateCodon(codon, cas);
						String protModifiedA = translateCodon(modifiedCodonA, cas);
						String protModifiedB = translateCodon(modifiedCodonB, cas);
						
						//check if the original codon has changed
						boolean sameA = protModifiedA.equals(prot);
						boolean sameB = protModifiedB.equals(prot);
						
						//update the characterization object
						snpChar.setOriginalAA(prot);
						snpChar.setModifiedAAA(protModifiedA);
						snpChar.setModifiedAAB(protModifiedB);
						snpChar.setIsModifiedAAA(!sameA);
						snpChar.setIsModifiedAAB(!sameB);
						
						if(snpChar.nonSynonymous()) {
							if(cas.isStartCodon(modifiedCodonA) || cas.isStartCodon(modifiedCodonB)) {
								snpChar.setCharacterizationFeature(SNPCharacterization.FIVE_PRIME_UTR_START_CODON_INSERTED);
							} else {
								snpChar.setCharacterizationFeature(SNPCharacterization.FIVE_PRIME_UTR);
							}
						}
						
						snpChar.setCharacterizationFeature(SNPCharacterization.FIVE_PRIME_UTR_SYNONYMOUS);
					}
					//in the 3' UTR
					else if(globalSNPPos < startCodonPosition) {
						snpChar.setCharacterizationFeature(SNPCharacterization.THREE_PRIME_UTR);
					}
				}
			}
			
			//snp hits an exon
			else if(feature.contains("exon")) {
				//check first / last exon
				int numChildren = e.getParent().numChildren();
				int minExonStart = Integer.MAX_VALUE;
				int maxExonEnd = Integer.MIN_VALUE;
				
				for(int i = 0; i < numChildren; i++) {
					GFFElement e2 = e.getParent().getChild(i);
					if(e2.getFeature().toLowerCase().contains("exon")) {
						if(e2.getChromosomalLocation().getStrand() == strand) {
							minExonStart = Math.min(minExonStart, e2.getChromosomalLocation().getStart());
							maxExonEnd = Math.max(maxExonEnd, e2.getChromosomalLocation().getStop());
						}
					}
				}
				
				boolean firstExon = false;
				boolean lastExon = false;
				
				boolean acceptor = false;
				boolean donor = false;
				
				lastExon = loc.getStart() == minExonStart ? true : false;
				firstExon = loc.getStop() == maxExonEnd ? true : false;
				
				if(!firstExon) {
					//splice acceptor site (AG just before an exon)
					if(loc.getStop() < snp.getPosition() 
							&& snp.getPosition() - loc.getStop() <= 1) {
						acceptor = true;
						snpChar.setCharacterizationFeature(SNPCharacterization.SPLICE_SITE_ACCEPTOR);
					}
				}
				
				if(!lastExon) {
					//splice donor site (GU just after an exon)
					if(snp.getPosition() < loc.getStart() 
							&& loc.getStart() - snp.getPosition() <= 1) {
						donor = true;
						snpChar.setCharacterizationFeature(SNPCharacterization.SPLICE_SITE_DONOR);
					}
				}
				
				//not acceptor and not donor -> must be an intron
				if(!acceptor && !donor) {
					if(firstExon) {
						if(snp.getPosition() > loc.getStop()) { //upstream of first exon -> 5' UTR
							int globalSNPPos = snp.getPosition();
							String exonSeq = g.getSequence(loc.getChromosome(), loc.getStart(), loc.getStop());
							String rExonSeq = reverseComplement(exonSeq);
							int startCodonPosition = getFirstStartCodonPosition(rExonSeq, cas);
							
							//to global position
							startCodonPosition += loc.getStart();
							
							//get position of the potential new start codon
							int localSNPPos = (3 - (globalSNPPos - startCodonPosition) % 3) % 3;
							int snpStartCodonPosition = globalSNPPos + localSNPPos;
							
							//fetch 2 codons because of phase, to have enough sequence
							String seq = g.getSequence(loc.getChromosome(), snpStartCodonPosition-6, snpStartCodonPosition);
							//use the reverse complement since we are on the '-' strand
							String rseq = reverseComplement(seq);
							//correct for phase
							rseq = rseq.substring(phase);
							
							//translate first codon
							String codon = getCodon(rseq, 0);
							String modifiedCodonA = modifyCodon(codon, snpA, 0);
							String modifiedCodonB = modifyCodon(codon, snpB, 0);
							
							//translate the codons
							String prot = translateCodon(codon, cas);
							String protModifiedA = translateCodon(modifiedCodonA, cas);
							String protModifiedB = translateCodon(modifiedCodonB, cas);
							
							//check if the original codon has changed
							boolean sameA = protModifiedA.equals(prot);
							boolean sameB = protModifiedB.equals(prot);
							
							//update the characterization object
							snpChar.setOriginalAA(prot);
							snpChar.setModifiedAAA(protModifiedA);
							snpChar.setModifiedAAB(protModifiedB);
							snpChar.setIsModifiedAAA(!sameA);
							snpChar.setIsModifiedAAB(!sameB);
							
							if(snpChar.nonSynonymous()) {
								if(cas.isStartCodon(modifiedCodonA) || cas.isStartCodon(modifiedCodonB)) {
									snpChar.setCharacterizationFeature(SNPCharacterization.FIVE_PRIME_UTR_START_CODON_INSERTED);
								} else {
									snpChar.setCharacterizationFeature(SNPCharacterization.FIVE_PRIME_UTR);
								}
							}
							
							snpChar.setCharacterizationFeature(SNPCharacterization.FIVE_PRIME_UTR_SYNONYMOUS);
							
						} else if(snp.getPosition() < loc.getStart()){ //down stream of first exon -> Intron
							snpChar.setCharacterizationFeature(SNPCharacterization.INTRON);
						} else { //snp hits somewhere in the exon
							snpChar.setCharacterizationFeature(SNPCharacterization.EXON);
						}
					} else if(lastExon) {
						if(snp.getPosition() > loc.getStop()) { //upstream of last exon -> Intron 
							snpChar.setCharacterizationFeature(SNPCharacterization.INTRON);
						} else if(snp.getPosition() < loc.getStart()) { //downstream of last exon -> 3' UTR
							snpChar.setCharacterizationFeature(SNPCharacterization.THREE_PRIME_UTR);
						} else { //Inside last exon
							snpChar.setCharacterizationFeature(SNPCharacterization.EXON);
						}
					} else { //some exon between two introns
						if(snp.getPosition() > loc.getStop()) { //upstream of the exon -> Intron
							snpChar.setCharacterizationFeature(SNPCharacterization.INTRON);
						} else if(snp.getPosition() < loc.getStart()){ //down stream of the exon -> Intron
							snpChar.setCharacterizationFeature(SNPCharacterization.INTRON);
						} else { //snp hits somewhere in the exon
							snpChar.setCharacterizationFeature(SNPCharacterization.EXON);
						}
					}
				}
			}
			
			//snp hits something else
			else {
				snpChar.setCharacterizationFeature(-1);
			}
		}
		
		return snpChar;
	}
	
	private int getFirstStopCodonPosition(String nucSeq, CodonsAminoacids cas, int startCodonPosition) {
		for(int i = startCodonPosition + 3; i < nucSeq.length(); i+=3) {
			if(nucSeq.length() - i >= 3) {
				String codon = nucSeq.substring(i, i+3);
				if(cas.isStopCodon(codon)) {
					return i;
				}
			}
		}
		return nucSeq.length();
	}

	private int getFirstStartCodonPosition(String nucSeq, CodonsAminoacids cas) {
		for(int i = 0; i < nucSeq.length(); i+=3) {
			if(nucSeq.length() - i >= 3) {
				String codon = nucSeq.substring(i, i+3);
				if(cas.isStartCodon(codon)) {
					return i;
				}
			}
		}
		return -1;
	}

	private String getCodon(String nucSequence, int position) {
		switch(position % 3) {
		case 0:
			return nucSequence.substring(position, position+3);
		case 1:
			return nucSequence.substring(position-1, position+2);
		case 2:
			return nucSequence.substring(position-2, position+1);
		}
		
		return null;
	}

	private String reverseComplement(String string) {
		StringBuilder build = new StringBuilder(string.length());
		for (int i = string.length() -1 ; i>=0 ;i-- ){
			build.append(replace(string.charAt(i)));
		}
		return build.toString();
	}
	
	private char replace(char in) {
		switch(in) {
		case 'A' : return 'T';
		case 'T' : return 'A';
		case 'G' : return 'C';
		case 'C' : return 'G';
		default : return in;
		}
	}
	
	private String modifyCodon(String codon, char snpChar, int position) {
		char[] seq = codon.toCharArray();
		seq[position%3] = snpChar;
		return new String(seq);
	}
	
	private String translateCodon(String nucSequence, CodonsAminoacids cas) {
		Codon c = cas.new Codon(nucSequence.charAt(0), nucSequence.charAt(1), nucSequence.charAt(2));
		Aminoacid aa = cas.translate(c);
		return aa.oneLetter+"";
	}
	
	public int getLocalSNPPosition(GFFElement gffElement, SNP snp) {
		int start =  gffElement.getChromosomalLocation().getStart();
		int snpPos = snp.getPosition();
		return snpPos - start;
	}
}
