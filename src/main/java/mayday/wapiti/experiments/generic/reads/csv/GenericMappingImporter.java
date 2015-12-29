/*
 * Created on 29.11.2005
 */
package mayday.wapiti.experiments.generic.reads.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.table.TableModel;

import mayday.core.gui.abstractdialogs.SimpleStandardDialog;
import mayday.core.gui.columnparse.ColumnTypeDialog;
import mayday.core.io.csv.CSVImportSettingComponent;
import mayday.core.io.csv.ParsedLine;
import mayday.core.io.csv.ParserSettings;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.tasks.AbstractTask;
import mayday.core.tasks.TaskStateEvent;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.importer.DefaultLocusSetting;
import mayday.genetics.importer.SelectableDefaultLocusSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.generic.mapping.AbstractMappingImportPlugin;
import mayday.wapiti.experiments.generic.mapping.csv.LazyParsingTableModel;
import mayday.wapiti.experiments.generic.reads.ReadsData;
import mayday.wapiti.experiments.generic.reads.ReadsExperiment;
import mayday.wapiti.experiments.generic.reads.csv.MappingColumnTypes.CTYPE;
import mayday.wapiti.transformations.matrix.TransMatrix;


public class GenericMappingImporter<DialogType extends ColumnTypeDialog<MappingColumnTypes>> extends AbstractMappingImportPlugin {

	protected HierarchicalSetting mySetting;
	protected BooleanSetting eachFileAlone;

	
	public GenericMappingImporter() {
		super(true, false);
	}
	
	public Setting getSetting() {
		if (mySetting == null) {
			eachFileAlone = new BooleanSetting("One experiment per file","Alternative: All files are one experiment",false);
			mySetting = new HierarchicalSetting("Mapping import").addSetting(super.getSetting()).addSetting(eachFileAlone);
		}
		return mySetting;
	}

	protected TableModel getTableModel(File f) {
		if (f==null)
			return null;
		CSVImportSettingComponent comp;
		try {
			LazyParsingTableModel lptm = new LazyParsingTableModel(f,100);			
			comp = new CSVImportSettingComponent(lptm);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}            		
		SimpleStandardDialog dlg = new SimpleStandardDialog("Import Mapped Reads",comp,false);
		dlg.setVisible(true);
		if(!dlg.okActionsCalled())
			return null;
		return comp.getTableModel();
	}
		
	
	
	
	
	public static class VariableGeneticCoordinate extends mayday.genetics.advanced.VariableGeneticCoordinate {

		protected String identifier;
		protected double quality;
		
		public final static int Identifier = 0;
		public final static int Quality = 9;

		public VariableGeneticCoordinate(ChromosomeSetContainer csc) {
			super(csc);
		}
		
		@Override
		public boolean update_charseq_extended(int what, CharSequence value) {
			switch (what) {
			case Identifier:
				identifier = value.toString();
				return true;
			case Quality:
				quality = Double.parseDouble(value.toString()); 
				return true;
			}
			return false;
		}
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".CSV.full", 
				new String[0], 
				MC, 
				null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Import experiments from mapped reads files", 
		"Mapped Reads with ids and quality (CSV)");
	}


	protected class SettingsBlorb {
		HashMap<String,SelectableDefaultLocusSetting> fileDefaultLocus;
		DefaultLocusSetting defaultLocus;
		boolean _species;
		boolean _chrome;
		boolean _strand;
		boolean _length;
		HashMap<CTYPE, Integer> asCol;
		ParserSettings parserSettings;
	}

	public List<Experiment> getMappingExperiments(final List<String> files, TransMatrix transMatrix)  {
		LinkedList<Experiment> result = new LinkedList<Experiment>();

		SettingsBlorb psett = new SettingsBlorb();
		if (!getParserSettings(files, psett))
			return Collections.emptyList();
		
		if (eachFileAlone.getBooleanValue()) {
			for (String file : files) {
				getMappingExperiments0(Arrays.asList(new String[]{file}), transMatrix, result, psett);
			}
		} else {
			getMappingExperiments0(files, transMatrix, result, psett);
		}
		
		return result;
	}

	protected boolean getParserSettings(List<String> files, SettingsBlorb rv) {
		// configure columns, but read only a few lines of the first file
		final TableModel tm = getTableModel(new File(files.get(0)));
		if (tm==null)
			return false;
		MappingColumnDialog mcd = new MappingColumnDialog(tm);
		mcd.setModal(true);
		mcd.setVisible(true);
		if (mcd.canceled())
			return false;
		// check for missing columns and ask user accordingly
		
		// I need Species, Chromosome, Start, Strand, Length/End for all
		
		Set<CTYPE> foundTypes = ((MappingColumnTypeValidator)mcd.getValidator()).getTypes();
		final boolean _species = foundTypes.contains(CTYPE.Species);
		final boolean _chrome = foundTypes.contains(CTYPE.Chromosome);
		final boolean _strand = foundTypes.contains(CTYPE.Strand);
		final boolean _length = foundTypes.contains(CTYPE.Length) || foundTypes.contains(CTYPE.To);
		
		final DefaultLocusSetting defaultLocus;
		final HashMap<String,SelectableDefaultLocusSetting> fileDefaultLocus = new HashMap<String,SelectableDefaultLocusSetting>();
		
		if (!_species || !_chrome || !_strand || !_length) {
			HierarchicalSetting topSet = new HierarchicalSetting("Fill in missing locus information");
			String istr = "<html>The input file does not contain all the needed information<br>" +
					"Please supply the missing information below.<br>" +
					(files.size()>1?"You can also supply per-file information.":"");
			defaultLocus = new DefaultLocusSetting();
			defaultLocus.hideElements(_species, _chrome, _strand, _length);
			topSet.addSetting(new ComponentPlaceHolderSetting("info", new JLabel(istr)));
			topSet.addSetting(defaultLocus);
			if (files.size()>1) {
				HierarchicalSetting perFileLocus = new HierarchicalSetting("Per-file settings");
				perFileLocus.setLayoutStyle(HierarchicalSetting.LayoutStyle.TREE);			
				topSet.addSetting(perFileLocus);
				for (String f : files) {
					SelectableDefaultLocusSetting ns = new SelectableDefaultLocusSetting(f);
					ns.hideElements(_species, _chrome, _strand, _length);
					fileDefaultLocus.put(f,ns);
					perFileLocus.addSetting(ns);  
				}
			}		
			SettingDialog sd = new SettingDialog(null, "Locus completion", topSet).showAsInputDialog();
			if (sd.canceled()) 
				return false;
		} else {
			defaultLocus = null;
		}
		
		final HashMap<CTYPE, Integer> asCol = new HashMap<CTYPE, Integer>();
		for (int i=0; i!=tm.getColumnCount(); ++i) {
			asCol.put(mcd.getColumnType(i), i);
		}
		
		ParserSettings parserSettings = ((LazyParsingTableModel)tm).getSettings();
		
		rv.fileDefaultLocus = fileDefaultLocus;
		rv.defaultLocus = defaultLocus;
		rv._chrome = _chrome;
		rv._species = _species;
		rv._strand = _strand;
		rv._length = _length;
		rv.asCol = asCol;
		rv.parserSettings = parserSettings;		
		
		return true;
	}
	
	protected List<Experiment> getMappingExperiments0(
			final List<String> files, 
			TransMatrix transMatrix,
			LinkedList<Experiment> result, 
			SettingsBlorb sv
	)  {
				
		final HashMap<String,SelectableDefaultLocusSetting> fileDefaultLocus = sv.fileDefaultLocus;
		final DefaultLocusSetting defaultLocus = sv.defaultLocus;
		final boolean _species = sv._species;
		final boolean _chrome = sv._chrome;
		final boolean _strand = sv._strand;
		final boolean _length = sv._length;
		final HashMap<CTYPE, Integer> asCol = sv.asCol;
		final ParserSettings parserSettings = sv.parserSettings;

		
		// parse the files with the given settings, line per line, use locus completion

		final ReadsData rd = new ReadsData();
		

		
		AbstractTask parserTask = new AbstractTask("Parsing mapped reads") {

			@Override
			protected void doWork() throws Exception {
				ChromosomeSetContainer csc=new ChromosomeSetContainer();
				VariableGeneticCoordinate tmp = new VariableGeneticCoordinate(csc);

				for (int i=0; i!=files.size(); ++i) { 
					int perc = (i*10000) / files.size();
					String progresstext = "Parsing file "+new File(files.get(i)).getName();
					this.setProgress(perc, progresstext);
					// build the default locus
					HashMap<CTYPE, Object> ret = new MultiHashMap<CTYPE, Object>();
					SelectableDefaultLocusSetting sdls = fileDefaultLocus.get(files.get(i));
					if (defaultLocus!=null) {
						if (!_length) {
							if (sdls!=null && sdls.overrideLength())
								ret.put(CTYPE.Length, sdls.getLength());
							else
								ret.put(CTYPE.Length, defaultLocus.getLength());
						}
						if (!_chrome) {
							if (sdls!=null && sdls.overrideChromosome())
								ret.put(CTYPE.Chromosome, sdls.getChromosome());
							else
								ret.put(CTYPE.Chromosome, defaultLocus.getChromosome());
						}
						if (!_species) {
							if (sdls!=null && sdls.overrideSpecies())
								ret.put(CTYPE.Species, sdls.getSpecies());
							else
								ret.put(CTYPE.Species, defaultLocus.getSpecies());
						}
						if (!_strand) {
							if (sdls!=null && sdls.overrideStrand())
								ret.put(CTYPE.Strand, sdls.getStrand());
							else
								ret.put(CTYPE.Strand, defaultLocus.getStrand());
						}
					}
					for (Entry<CTYPE,Object> e : ret.entrySet()) {
						tmp.update(e.getKey().vgce(),e.getValue());
					}
					// now read and add elements
					DecimalFormat df = new DecimalFormat("###,###,###,###,###");
					
					ParsedLine pl = new ParsedLine("",parserSettings);
					
					String line=null;
					int linesParsed=0;	

					try {
						BufferedReader br = new BufferedReader(new FileReader(files.get(i)));
						CTYPE[] colTypes = asCol.keySet().toArray(new CTYPE[0]);
						Integer[] colIndices = asCol.values().toArray(new Integer[0]);
						
						// skip lines before header
						for (int skip=0; skip!=parserSettings.skipLines; ++skip)
							br.readLine();
						// skip header itself
						if (parserSettings.hasHeader)
							br.readLine();
						// now start parsing

						while (br.ready()) {
							line = br.readLine();
							pl.replaceLine(line);
							if (!pl.isCommentLine()) {
								tmp.setFrom(-1);
								tmp.setTo(-1);
								for (int k=0; k!=colIndices.length; ++k) {
									if (colTypes[k]!=null)
										tmp.update(colTypes[k].vgce(), pl.getOptimized(colIndices[k]));
								}
								rd.addRead(tmp.identifier, tmp, (int)tmp.quality, 1);
								linesParsed++;
							}
							if (linesParsed % 10000 == 0) {
								this.setProgress(perc, progresstext+": "
										+df.format(linesParsed)+" reads, "
										+df.format(rd.getReadCount())+" total");
								if (hasBeenCancelled()) {
									setTaskState(TaskStateEvent.TASK_CANCELLED);
									return;
								}
							}
						}
					} catch (Exception e) {
						RuntimeException rte = new RuntimeException("Could not parse reads from "+files.get(i)+"\nLine number: "+(linesParsed+1)+"\nLine content: "+line, e);
						throw rte; 
					}
				}
				rd.getFullData().compact();
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
		
		System.out.println("Parsed reads in "+(t2-t1)+" ms");
		
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
		
		ReadsExperiment me = new ReadsExperiment(suggestedName,"Reads from "+names,transMatrix);
		me.setInitialData(rd);
		
		result.add(me);
		return result;
	}

		
}
