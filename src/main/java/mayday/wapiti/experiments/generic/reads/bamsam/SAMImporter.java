/*
 * Created on 29.11.2005
 */
package mayday.wapiti.experiments.generic.reads.bamsam;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.tasks.AbstractTask;
import mayday.core.tasks.TaskStateEvent;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.importer.SelectableDefaultLocusSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.generic.mapping.AbstractMappingImportPlugin;
import mayday.wapiti.experiments.generic.mapping.MappingData;
import mayday.wapiti.experiments.generic.mapping.MappingExperiment;
import mayday.wapiti.experiments.generic.reads.ReadsData;
import mayday.wapiti.experiments.generic.reads.ReadsExperiment;
import mayday.wapiti.experiments.generic.reads.bamsam.samtools.SAMFileHeader;
import mayday.wapiti.experiments.generic.reads.bamsam.samtools.SAMFileReader;
import mayday.wapiti.experiments.generic.reads.bamsam.samtools.SAMRecord;
import mayday.wapiti.experiments.generic.reads.bamsam.samtools.SAMSequenceDictionary;
import mayday.wapiti.experiments.generic.reads.bamsam.samtools.SAMSequenceRecord;
import mayday.wapiti.transformations.matrix.TransMatrix;


public class SAMImporter extends AbstractMappingImportPlugin {

	protected HierarchicalSetting mySetting;
	protected BooleanSetting completeReads;
	protected BooleanSetting eachFileAlone;
	protected BooleanSetting movePairs;


	protected BooleanSetting showStats;
	
	public SAMImporter() {
		super(true, false);
	}
	
	public Setting getSetting() {
		if (mySetting == null) {
			eachFileAlone = new BooleanSetting("One experiment per file","Alternative: All files are one experiment",false);
			movePairs = new BooleanSetting("Flip strand of paired-end second mate",
					"If checked, the second mate in paired-end reads are moved to the opposite strand.\n" +
					"Use this if your mapping software does not place paired-end reads on the same strand.",true);
			completeReads = new BooleanSetting("Also import read details","If selected, read identifiers, mapping quality and alignment data are imported\n" +
					"and can be added as meta data to the experiments. \n This consumes a considerably larger amount of memory.", true);
			showStats = new BooleanSetting("Show Stats", "Show additional statistics for the mapping files", true);

			mySetting = new HierarchicalSetting("Mapping import")
					.addSetting(super.getSetting())
					.addSetting(completeReads)
					.addSetting(eachFileAlone)
					.addSetting(movePairs)
					.addSetting(showStats);
		}
		return mySetting;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".SAMorBAM", 
				new String[0], 
				MC, 
				null, 
				"Günter Jäger", 
				"jaeger@informatik.uni-tuebingen.de", 
				"Import experiments from SAM/BAM files", 
		"Mapped reads from SAM/BAM");
	}


	protected class SettingsBlorb {
		HashMap<String,SelectableDefaultLocusSetting> fileDefaultLocus;
		SelectableDefaultLocusSetting defaultLocus;
		boolean _species;
		boolean _chrome;
	}

	public List<Experiment> getMappingExperiments(final List<String> files, TransMatrix transMatrix)  {
		LinkedList<Experiment> result = new LinkedList<Experiment>();

		SettingsBlorb psett = new SettingsBlorb();
		if (!getParserSettings(files, psett))
			return Collections.emptyList();
		
		if (eachFileAlone.getBooleanValue()) {
			for (String file : files) {
				if (getMappingExperiments0(Arrays.asList(new String[]{file}), transMatrix, result, psett).size()==0)
					return result; // premature break by user 
			}
		} else {
			getMappingExperiments0(files, transMatrix, result, psett);
		}

		if (showStats.getBooleanValue()) {
			// Calculate statistics in background (class is Task)
			RNAseqStat stats = new RNAseqStat(files);
			stats.start();
		}

		return result;
	}

	protected boolean getParserSettings(List<String> files, SettingsBlorb rv) {
		
		SelectableDefaultLocusSetting defaultLocus;
		HashMap<String,SelectableDefaultLocusSetting> fileDefaultLocus = new HashMap<String,SelectableDefaultLocusSetting>();
		
		HierarchicalSetting topSet = new HierarchicalSetting("Fill in missing locus information");
		String istr = "<html>Not all SAM/BAM files contain correct species / chromosome (reference) information.<br>" +
		"Please supply the missing information below.<br>" +
		(files.size()>1?"You can also supply per-file information.":"");
		defaultLocus = new SelectableDefaultLocusSetting("Default");
		defaultLocus.hideElements(false, false, true, true);
		topSet.addSetting(new ComponentPlaceHolderSetting("info", new JLabel(istr)));
		topSet.addSetting(defaultLocus);
		if (files.size()>1) {
			HierarchicalSetting perFileLocus = new HierarchicalSetting("Per-file settings");
			perFileLocus.setLayoutStyle(HierarchicalSetting.LayoutStyle.TREE);			
			topSet.addSetting(perFileLocus);
			for (String f : files) {
				SelectableDefaultLocusSetting ns = new SelectableDefaultLocusSetting(f);
				ns.hideElements(false, false, true, true);
				fileDefaultLocus.put(f,ns);
				perFileLocus.addSetting(ns);  
			}
		}		
		SettingDialog sd = new SettingDialog(null, "Locus completion", topSet).showAsInputDialog();
		if (sd.canceled()) 
			return false;
		
		rv.fileDefaultLocus = fileDefaultLocus;
		rv.defaultLocus = defaultLocus;
		
		return true;
	}
	
	protected List<Experiment> getMappingExperiments0(
			final List<String> files, 
			TransMatrix transMatrix,
			LinkedList<Experiment> result, 
			SettingsBlorb sv
	)  {
				
		final HashMap<String,SelectableDefaultLocusSetting> fileDefaultLocus = sv.fileDefaultLocus;
		final SelectableDefaultLocusSetting defaultLocus = sv.defaultLocus;
		
		// parse the files with the given settings, line per line, use locus completion

		final ReadsData rd_output = completeReads.getBooleanValue()?new ReadsData():null;
		final MappingData md_output = !completeReads.getBooleanValue()?new MappingData():null;
		
		final boolean movep = movePairs.getBooleanValue();
		
		AbstractTask parserTask = new AbstractTask("Parsing mapped reads") {

			@Override
			protected void doWork() throws Exception {				
				ChromosomeSetContainer csc=new ChromosomeSetContainer();

				for (int i=0; i!=files.size(); ++i) {
					
					try {

						int perc = (i*10000) / files.size();
						String progresstext = "Parsing file "+new File(files.get(i)).getName();
						this.setProgress(perc, progresstext);

						// build the default locus
						SelectableDefaultLocusSetting sdls = fileDefaultLocus.get(files.get(i));

						// has to be set BEFORE constructing a reader
						SAMFileReader.setDefaultValidationStringency(SAMFileReader.ValidationStringency.SILENT);
						
						SAMFileReader reader = new SAMFileReader(new File(files.get(i)));

						//get header and sequence dictionary if possible
						SAMFileHeader header = reader.getFileHeader();
						SAMSequenceDictionary dict = header.getSequenceDictionary();

						long readsParsed=0;
						DecimalFormat df = new DecimalFormat("###,###,###,###,###");
						
						//process every record and extract information
						for(final SAMRecord samRecord : reader) {
							// only mapped reads
							if (samRecord.getReadUnmappedFlag())
								continue;							
							//start position of the read
							int start = samRecord.getUnclippedStart();
							//end position of the read
							int end = samRecord.getUnclippedEnd();
							//start position of the alignment
							int startAlignment = samRecord.getAlignmentStart()-start;
							//length of the read, same as (end-start+1)
							//int length = samRecord.getReadLength();

							//get strand and parse to 'Strand'
							boolean reverseStrand = samRecord.getReadNegativeStrandFlag(); //strand

							Strand strand = reverseStrand ? Strand.MINUS : Strand.PLUS;
							//get name of reference sequence (mostly the chromosome)

							String refName = defaultLocus.getChromosome(); 
							
							if (sdls!=null && sdls.overrideChromosome())
								refName = sdls.getChromosome();
							else if (!defaultLocus.overrideChromosome())
								refName = samRecord.getReferenceName();

							String refSpecies = defaultLocus.getSpecies();
							
							if (sdls!=null && sdls.overrideSpecies())
								refSpecies = sdls.getSpecies();
							else if (!defaultLocus.overrideSpecies()){
								SAMSequenceRecord seqRecord = dict.getSequence(refName);
								if(seqRecord != null) {
									String tmpRefSpecies = seqRecord.getSpecies();
									if (tmpRefSpecies!=null)
										refSpecies = tmpRefSpecies;
								}								
							}

							Chromosome chromosome = csc.getChromosome(SpeciesContainer.getSpecies(refSpecies), refName);
							AbstractGeneticCoordinate locus = null;

							boolean pairedEnd = samRecord.getReadPairedFlag();
							boolean firstMate = pairedEnd?samRecord.getFirstOfPairFlag():false;
							boolean secondMate = pairedEnd?samRecord.getSecondOfPairFlag():false;
							
							if (pairedEnd && secondMate && movep) // of course the second mate maps to the other strand, we don't want this.
								strand = strand.reverse(); 
							
							if (rd_output!=null) {
								//get the read name
								String readName = samRecord.getReadName();
								
								//use the mapping quality field
								double mm = samRecord.getMappingQuality();
								
								// for paired-end data, we have to change the read name to make it unique
								if (pairedEnd) {
									if (firstMate) 
										readName+="/1";
									else if (secondMate)
										readName+="/2";
								}
								
								// TODO: use alignment blocks to create complex coordinates here.
								locus = new GeneticCoordinate(chromosome, strand, start, end);
								rd_output.addRead(readName, locus, mm, startAlignment);
							}

							if (md_output!=null) {
								if (locus==null)
									 locus = new GeneticCoordinate(chromosome, strand, start, end);
								md_output.addRead(locus);						
							} 
							
							readsParsed++;

							if (readsParsed % 10000 == 0) {
								long total = (rd_output==null?0:rd_output.getReadCount())+(md_output==null?0:md_output.getReadCount());
								this.setProgress(perc, progresstext+": "
										+df.format(readsParsed)+" reads, "
										+df.format(total)+" total");
								if (hasBeenCancelled()) {
									setTaskState(TaskStateEvent.TASK_CANCELLED);
									return;
								}
							}
							
						}
						reader.close();
					
					} catch (Exception e) {
						RuntimeException rte = new RuntimeException("Could not parse SAM/BAM file "+files.get(i), e);
						throw rte; 
					}
				}
				
				if (rd_output!=null)
					rd_output.getFullData().compact();
				if (md_output!=null)
					md_output.compact();
				setProgress(10000,"");
			}

			@Override
			protected void initialize() {
			}
			
		};
		
		long t1 = System.currentTimeMillis();
		parserTask.start();
		parserTask.waitFor();
		long t2 = System.currentTimeMillis();
		
		System.out.println("Parsed alignments in "+(t2-t1)+" ms");
		
		if (parserTask.getTaskState()!=TaskStateEvent.TASK_FINISHED)
			return Collections.emptyList();
		
		String names;
		if (files.size()==1)
			names = new File(files.get(0)).getName();
		else {
			names = files.size()+" files ("+new File(files.get(0)).getName();
				
			for (int i=1; i!=files.size(); ++i)
				names+=", "+files.get(i);
			if (names.length()>45)
				names = names.substring(0, 42)+"...";
			names += ")";
		}
		
		String suggestedName = new File(files.get(0)).getName();
		if (suggestedName.contains("."))
			suggestedName = suggestedName.substring(0, suggestedName.lastIndexOf('.'));
		
		if (rd_output!=null) {
			ReadsExperiment me = new ReadsExperiment(suggestedName,"Reads from "+names,transMatrix);
			me.setInitialData(rd_output);
			result.add(me);
		}
		
		if (md_output!=null) {
			MappingExperiment me = new MappingExperiment(suggestedName,"Reads from "+names,transMatrix);
			me.setInitialData(md_output);
			result.add(me);
			
		}
		
		return result;
	}

		
}
