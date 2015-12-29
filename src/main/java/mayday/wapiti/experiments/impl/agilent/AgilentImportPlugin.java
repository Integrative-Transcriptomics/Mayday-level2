package mayday.wapiti.experiments.impl.agilent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.gui.PreferencePane;
import mayday.core.io.csv.ParsedLine;
import mayday.core.io.csv.ParserSettings;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.FilesSetting;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.core.tasks.AbstractTask;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;
import mayday.wapiti.experiments.importer.ExperimentImportPlugin;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class AgilentImportPlugin extends ExperimentImportPlugin {

	protected FilesSetting inputFiles = new FilesSetting("Agilent files",null,null,false,"txt");	
	
	public Setting getSetting() {
		return inputFiles;
	}
	
	@Override
	public void importInto(final TransMatrix transMatrix) {	

		final LinkedList<Experiment> result = new LinkedList<Experiment>();
		
		AbstractTask at = new AgilentParserTask(inputFiles.getFileNames(), result, transMatrix);
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
				MC+".Agilent", 
				new String[0], 
				MC, 
				null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Import experiments from Agilent feature extraction files", 
		"Agilent files");
	}

	protected static class AgilentParserTask extends AbstractTask {

		protected List<Experiment> result;
		protected List<String> files;
		protected TransMatrix transMatrix;
		
		public AgilentParserTask(List<String> files, List<Experiment> result, TransMatrix transMatrix) {
			super("Parsing Agilent Files");
			this.result = result;
			this.files = files;
			this.transMatrix = transMatrix;
		}

		@Override
		protected void doWork() throws Exception {
			
			ParserSettings sett = new ParserSettings();
			sett.separator="\t";
			ParsedLine pl = new ParsedLine("", sett);
			
			int experiments = files.size();			
			
			for (int exp=0; exp!=experiments; ++exp) {
				
				File f = new File(files.get(exp));
				
				setProgress((10000*exp)/experiments, f.getName());
				
				try {
					FileReader reader = new FileReader(f);					
					BufferedReader inputStream = new BufferedReader(reader);
					String line  = inputStream.readLine();
					
					while(!line.startsWith("FEPARAMS")) {
						line = inputStream.readLine();
					}
					pl.replaceLine(line);
					
					int subrows=1, subcols=1, rows=1, cols=1;
					
					int cur=0;
					for (String s : pl) {
						if ("Grid_NumSubGridRows".equals(s))
							subrows=cur;
						if ("Grid_NumSubGridCols".equals(s))
							subcols=cur;
						if ("Grid_NumRows".equals(s))
							rows=cur;
						if ("Grid_NumCols".equals(s))
							cols=cur;
						++cur;
					}
					
					line = inputStream.readLine();
					pl.replaceLine(line);
					
					subrows = Integer.parseInt( pl.get(subrows) );
					subcols = Integer.parseInt( pl.get(subcols) );
					rows = Integer.parseInt( pl.get(rows) );
					cols = Integer.parseInt( pl.get(cols) );
					
					ArrayLayout layout = new ArrayLayout(subrows, subcols, rows, cols);
					
					while(!line.startsWith("FEATURES")) {
						line = inputStream.readLine();
					}
					
					int R=-1, G=-1, Rb=-1, Gb=-1, pid=-1;
					
					pl.replaceLine(line);
					cur=0;
					for (String s : pl) {
						if ("gMeanSignal".equals(s))
							G=cur;
						if ("rMeanSignal".equals(s))
							R=cur;
						if ("gBGMedianSignal".equals(s))
							Gb=cur;
						if ("rBGMedianSignal".equals(s))
							Rb=cur;
						if ("ProbeName".equals(s))
							pid=cur;
						++cur;
					}
					
					HashMap<String, Integer> nameCache = new HashMap<String, Integer>();
					String[] featureNames = new String[layout.features()];
					DoubleVector redF = new DoubleVector(layout.features());
					DoubleVector redB = new DoubleVector(layout.features());
					DoubleVector greenF = new DoubleVector(layout.features());
					DoubleVector greenB = new DoubleVector(layout.features());
								
					redF.setNamesDirectly(featureNames, nameCache); //only link the names. this is an important optimization
					redB.setNamesDirectly(featureNames, nameCache); //only link the names. this is an important optimization
					greenF.setNamesDirectly(featureNames, nameCache); //only link the names. this is an important optimization
					greenB.setNamesDirectly(featureNames, nameCache); //only link the names. this is an important optimization
					
					int numLine = 0;
					
					while(!line.startsWith("DATA")) {
						line = inputStream.readLine();
					}
					
					while(line != null) {
						pl.replaceLine(line);
						
						String fname = pl.get(pid);
						String rval = pl.get(R);
						String gval = pl.get(G);
						String rbval = pl.get(Rb);
						String gbval = pl.get(Gb);
						
						featureNames[numLine] = fname;
						redF.set(numLine, Double.valueOf(rval));
						redB.set(numLine, Double.valueOf(rbval));
						greenF.set(numLine, Double.valueOf(gval));
						greenB.set(numLine, Double.valueOf(gbval));
						
						line = inputStream.readLine();
						numLine++;
//						setProgress((int) (exp+(numLine/(double)layout.features()))* (10000/experiments), f.getName());
					}
					
					makeNamesUnique(featureNames, f.getName());
					
					AgilentExperiment agilentE = new AgilentExperiment(transMatrix, f.getCanonicalPath(), redF, redB, greenF, greenB, layout);
					result.add(agilentE);

				} 
				catch (Exception e) {
					throw new RuntimeException("Error during parsing the agilent file: "+f.getName()+"\n"+e+"\n"+e.getMessage());				
				}
				
				
			}
		}

		@Override
		protected void initialize() {
		}
		
	}
	
	
}
