package mayday.wapiti.experiments.impl.scanarray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.gui.PreferencePane;
import mayday.core.io.csv.ParsedLine;
import mayday.core.io.csv.ParserSettings;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.FilesSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.core.tasks.AbstractTask;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;
import mayday.wapiti.experiments.importer.ExperimentImportPlugin;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class ScanArrayImportPlugin extends ExperimentImportPlugin {

	protected final static String MEAN = "Mean";
	protected final static String MEDIAN = "Median";
	
	protected String[] choices= new String[]{MEAN, MEDIAN};
	
	protected RestrictedStringSetting extractedFValues = new RestrictedStringSetting("Foreground values to extract",null, 0, choices);
	protected RestrictedStringSetting extractedBValues = new RestrictedStringSetting("Background values to extract",null, 1, choices);
	protected FilesSetting inputFiles = new FilesSetting("ScanArray Express files",null,null,false,"csv");	
	
	protected HierarchicalSetting mySetting = new HierarchicalSetting("ScanArray Import")
	.addSetting(inputFiles)
	.addSetting(extractedFValues)
	.addSetting(extractedBValues);

	
	public Setting getSetting() {
		return mySetting;
	}
	
	@Override
	public void importInto(final TransMatrix transMatrix) {	

		final LinkedList<Experiment> result = new LinkedList<Experiment>();
		
		AbstractTask at = new ScanArrayParserTask(inputFiles.getFileNames(), result, transMatrix, extractedFValues.getObjectValue(), extractedBValues.getObjectValue());
		at.start();
		at.waitFor();

		if (!at.hasBeenCancelled())
			addExperiments(result, transMatrix);
		
	}

	public PreferencePane getPreferencesPanel() {
		return null;
	}


	protected void addExperiments(List<Experiment> experiments, TransMatrix transMatrix) {
		if (experiments.size()==0)
			return;
		super.addExperiments(experiments, transMatrix);
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".ScanArray", 
				new String[0], 
				MC, 
				null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Import experiments from ScanArray Expression files (format version 2.0)", 
		"ScanArray Express CSV files");
	}

	protected static class ScanArrayParserTask extends AbstractTask {

		protected List<Experiment> result;
		protected List<String> files;
		protected TransMatrix transMatrix;
		protected ParsedLine pl;
		protected String foreValue, backValue;
		
		public ScanArrayParserTask(List<String> files, List<Experiment> result, TransMatrix transMatrix, String F, String B) {
			super("Parsing ScanArray Files");
			this.result = result;
			this.files = files;			
			this.transMatrix = transMatrix;
			foreValue = F;
			backValue = B;
		}
		
		private int findAndCheckHeader(BufferedReader br, String file) throws IOException {
			String line;
			do {
				line = br.readLine();
			} while (line!=null && !line.equals("BEGIN HEADER"));
			
			if (line==null) 
				throw new RuntimeException("Could not find header information (\"BEGIN HEADER\") in file "+file);
			
			while (line!=null && !line.equals("END HEADER")) {
				if (line.startsWith("ScanArrayCSVFileFormat")) {
					pl.replaceLine(line);
					try {
						int version = (int)Double.parseDouble(pl.get(1));
						return version;
					} catch (Exception e) {
						throw new RuntimeException("Could not parse file format version in file "+file);
					}
				}
				line = br.readLine();
			}
			throw new RuntimeException("Header does not seem to contain file format version (\"ScanArrayCSVFileFormat\") in file "+file);
		}
		
		private String skipTo(BufferedReader br, String start) throws IOException {
			String line;
			do {
				line = br.readLine();
				if (line.startsWith(start))
					return line;
			} while (line!=null);
			return null;
		}
		
		private ArrayLayout findAndReadLayout(BufferedReader br, String file) throws IOException {
			String line;
			do {
				br.mark(2500);
				line = br.readLine();
			} while (line!=null && !line.equals("BEGIN ARRAY PATTERN INFO") && !line.equals("BEGIN DATA"));
			
			if (line==null) 
				throw new RuntimeException("Could not find data section (\"BEGIN DATA\") in file "+file);
			
			if (line.equals("BEGIN DATA")) {
				br.reset();
				throw new RuntimeException("No array layout information in file "+file);
			}
			
			String s_rows = skipTo(br, "Array Rows");
			String s_cols = skipTo(br, "Array Columns");
			String s_srows = skipTo(br, "Spot Rows");
			String s_scols = skipTo(br, "Spot Columns");

			if (s_cols==null || s_rows==null || s_srows==null || s_scols==null)
				throw new RuntimeException("Incomplete Array Layout information in file "+file);
			
			try {
				pl.replaceLine(s_rows);
				int rows = Integer.parseInt( pl.get(1) );
				pl.replaceLine(s_cols);
				int cols = Integer.parseInt( pl.get(1) );
				pl.replaceLine(s_srows);
				int subrows = Integer.parseInt( pl.get(1) );
				pl.replaceLine(s_scols);
				int subcols = Integer.parseInt( pl.get(1) );
				return  new ArrayLayout(subrows, subcols, rows, cols);
			} catch (Exception e) {
				throw new RuntimeException("Could not parse Array Layout information in file "+file);
			}
			
		}


		@Override
		protected void doWork() throws Exception {
			
			ParserSettings sett = new ParserSettings();
			sett.separator=",";
			pl = new ParsedLine("", sett);
			
			int experiments = files.size();			
			
			for (int exp=0; exp!=experiments; ++exp) {
				
				File f = new File(files.get(exp));
				
				setProgress((10000*exp)/experiments, f.getName());
				
				try {
					FileReader reader = new FileReader(f);					
					BufferedReader inputStream = new BufferedReader(reader);
					
					int version = findAndCheckHeader(inputStream, f.getName() );
					
					if (version!=2 && version!=3)
						writeLog("This parser was only tested with version 2 and version 3 ScanArray files.\n" +
								"Your file appears to have version "+version+" (file: "+f.getName()+").\n" +
								"Trying to continue anyway.");
					
					ArrayLayout layout = findAndReadLayout(inputStream, f.getName());
					
					if (skipTo(inputStream, "BEGIN DATA")==null)
						throw new RuntimeException("Could not find data section (\"BEGIN DATA\") in file "+f.getName());
					
					// now find the correct columns					
					pl.replaceLine(inputStream.readLine());
					int colName=-1, colFlags=-1, colForeA=-1, colBackA=-1, colForeB=-1, colBackB=-1;
					String colForeAname = "Ch1 "+foreValue;
					String colBackAname = "Ch1 B "+backValue;
					String colForeBname = "Ch2 "+foreValue;
					String colBackBname = "Ch2 B "+backValue;
					for (int i=0; i!=pl.size(); ++i) {
						String s = pl.get(i);
						if (s.equals("Name"))
							colName = i;
						if (s.equals("Flags"))
							colFlags = i;
						if (s.equals(colForeAname))
							colForeA = i;
						if (s.equals(colBackAname))
							colBackA = i;
						if (s.equals(colForeBname))
							colForeB = i;
						if (s.equals(colBackBname))
							colBackB = i;
					}
					if (colName<0 || colFlags<0 || colForeA<0 || colForeB<0 || colBackA<0 || colBackB<0) 
						throw new RuntimeException("One of these columns could not be found: " +
								"Name, Flags, "+colForeAname+", "+colBackAname+", "+colForeBname+", "+colBackBname+" in file "+f.getName() );

					HashMap<String, Integer> nameCache = new HashMap<String, Integer>();
					String[] featureNames = new String[layout.features()];
					DoubleVector redF = new DoubleVector(layout.features());
					DoubleVector redB = new DoubleVector(layout.features());
					DoubleVector greenF = new DoubleVector(layout.features());
					DoubleVector greenB = new DoubleVector(layout.features());
					DoubleVector flags = new DoubleVector(layout.features());
								
					redF.setNamesDirectly(featureNames, nameCache); //only link the names. this is an important optimization
					redB.setNamesDirectly(featureNames, nameCache); //only link the names. this is an important optimization
					greenF.setNamesDirectly(featureNames, nameCache); //only link the names. this is an important optimization
					greenB.setNamesDirectly(featureNames, nameCache); //only link the names. this is an important optimization
					flags.setNamesDirectly(featureNames, nameCache); //only link the names. this is an important optimization
					
					int numLine = 0;
					String line;
					
					while((line=inputStream.readLine())!=null && !line.startsWith("END DATA")) {
						pl.replaceLine(line);
						
						String fname = pl.get(colName);
						String flag  = pl.get(colFlags);
						String rval = pl.get(colForeA);
						String gval = pl.get(colForeB);
						String rbval = pl.get(colBackA);
						String gbval = pl.get(colBackB);

						featureNames[numLine] = fname;
						redF.set(numLine, Double.valueOf(rval));
						redB.set(numLine, Double.valueOf(rbval));
						greenF.set(numLine, Double.valueOf(gval));
						greenB.set(numLine, Double.valueOf(gbval));
						flags.set(numLine, Double.valueOf(flag));
						
						numLine++;
//						setProgress((int) (exp+(numLine/(double)layout.features()))* (10000/experiments), f.getName());
					}
					
					if (numLine<layout.features())
						writeLog("The chip layout suggests "+layout.features()+" features, \n" +
								"but only "+numLine+" features were defined in DATA section of file\n" +
								f.getCanonicalPath());
					
					makeNamesUnique(featureNames, f.getName());
					
					ScanArrayExperiment agilentE = new ScanArrayExperiment(transMatrix, f.getCanonicalPath(), foreValue, backValue, redF, redB, greenF, greenB, flags, layout);
					result.add(agilentE);

				} 
				catch (Exception e) {
					throw new RuntimeException("Error during parsing the scanarray file: "+f.getName()+"\n"+e+"\n"+e.getMessage());				
				}
				
				
			}
		}

		@Override
		protected void initialize() {
		}
		
	}
	
	
}
