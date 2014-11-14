package mayday.wapiti.experiments.impl.genepix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import mayday.core.MaydayDefaults;
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

public class GPRImportPlugin extends ExperimentImportPlugin {

	protected final static String MEAN = "Mean";
	protected final static String MEDIAN = "Median";
	
	protected String[] choices= new String[]{MEAN, MEDIAN};
	
	protected RestrictedStringSetting extractedFValues = new RestrictedStringSetting("Foreground values to extract",null, 0, choices);
	protected RestrictedStringSetting extractedBValues = new RestrictedStringSetting("Background values to extract",null, 1, choices);
	protected FilesSetting inputFiles = new FilesSetting("GenePix files",null,null,false,"gpr");	
	
	protected HierarchicalSetting mySetting = new HierarchicalSetting("GenePix Import")
	.addSetting(inputFiles)
	.addSetting(extractedFValues)
	.addSetting(extractedBValues);
	
	public Setting getSetting() {
		return mySetting;
	}
	
	@Override
	public void importInto(final TransMatrix transMatrix) {	

		final LinkedList<Experiment> result = new LinkedList<Experiment>();
		
		AbstractTask at = new GPRParserTask(inputFiles.getFileNames(), result, transMatrix, extractedFValues.getStringValue(), extractedBValues.getStringValue());
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
				MC+".Genepix", 
				new String[0], 
				MC, 
				null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Import experiments from GenePix files", 
		"GenePix files");
	}

	protected static class GPRParserTask extends AbstractTask {

		protected List<Experiment> result;
		protected List<String> files;
		protected TransMatrix transMatrix;
		protected String fVal, bVal;
		
		public GPRParserTask(List<String> files, List<Experiment> result, TransMatrix transMatrix, String F, String B) {
			super("Parsing GenePix Files");
			this.result = result;
			this.files = files;
			this.transMatrix = transMatrix;
			fVal = F;
			bVal = B;
		}

		@Override
		protected void doWork() throws Exception {
			
			int numbers[] = getNumbers(new File(files.get(0)));
			ArrayLayout layout = new ArrayLayout(numbers[2],numbers[1],numbers[0]);
			
			int experiments = files.size();
			
			ParserSettings psett = new ParserSettings();
			ParsedLine pl = new ParsedLine("",psett);
			
			for (int exp=0; exp!=experiments; ++exp) {
				
				String fileName = new File(files.get(exp)).getName();
				
				setProgress((10000*exp)/experiments, fileName);
				
				int[] waveLengths = null;
				
				try {
					FileReader reader = new FileReader(files.get(exp));					
					BufferedReader inputStream = new BufferedReader(reader);
					String line  = inputStream.readLine();
					
					while(!line.startsWith("\"Block\"") && !line.startsWith("Block")) {
						line = inputStream.readLine();
						if(line.startsWith("\"ImageName=") || line.startsWith("\"Wavelengths="))	{							
							waveLengths = getWaveLengths(line);
						}
					}
					
					if (waveLengths==null) {
						inputStream.close();
						throw new RuntimeException("Could not find wavelength specification in the GPR file "+files.get(exp));
					}
					
					HashMap<String, Integer> colMapping = makeColMapping(waveLengths, fVal, bVal);					
					int[] cols = new int[6];				
					getColsToRead(line, cols, colMapping);
					
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
					
					line = inputStream.readLine();
					int numLine = 0;
					

					while(line != null) {
						pl.replaceLine(line);
						
						String fname = pl.get(cols[0]);
						if (fname.startsWith("\"") && fname.endsWith("\""))
							fname = fname.substring(1, fname.length()-1);
						featureNames[numLine] = fname;
						redF.set(numLine, Double.valueOf(pl.get(cols[1]).trim()));
						redB.set(numLine, Double.valueOf(pl.get(cols[2]).trim()));
						greenF.set(numLine, Double.valueOf(pl.get(cols[3]).trim()));
						greenB.set(numLine, Double.valueOf(pl.get(cols[4]).trim()));
						flags.set(numLine, Integer.valueOf(pl.get(cols[5]).trim()));
						
						line = inputStream.readLine();
						numLine++;
					}
					
					makeNamesUnique(featureNames, fileName);

					GPRExperiment imgE = new GPRExperiment(transMatrix, files.get(exp), fVal, bVal, redF, redB, greenF, greenB, flags, layout);
					result.add(imgE);
					inputStream.close();

				} 
				catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Error during parsing the GPR file: "+files.get(exp)+"\n"+e+"\n"+e.getMessage());				
				}
				
				
			}
		}

		@Override
		protected void initialize() {
		}
		
	}
	
	
	private static int[] getNumbers(File input) 
	{
		int rows = 0;
		int cols = 0;
		int blocks = 0;
		
		try 
		{
			FileReader r = new FileReader(input);
			BufferedReader br = new BufferedReader(r);
			String line = "";
			String tmp = "";
			while(tmp!=null)
			{
				tmp = br.readLine();
				if(tmp != null)
				{
					line = tmp;
				}
			}
			StringTokenizer st = new StringTokenizer(line, "\t");
			blocks = Integer.valueOf(st.nextToken().replaceAll(" ", ""));
			cols = Integer.valueOf(st.nextToken().replaceAll(" ", ""));
			rows = Integer.valueOf(st.nextToken().replaceAll(" ", ""));
			br.close();
		}
		catch (Exception e) 
		{
			JOptionPane.showMessageDialog(null, "Something went wrong!\nMaybe one of the files is corrupted!",
					MaydayDefaults.Messages.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
		}
		return new int[]{cols,rows,blocks};
	}
	
	private static int[] getWaveLengths(String line) 
	{
		if (line.contains("nm")) {
			line = line.replaceAll("nm", "");
		}
		line = line.replaceAll("\"", "");
		StringBuffer sb = new StringBuffer(line);		
		int start1 = sb.indexOf("=")+1;
		int end1 = sb.indexOf("\t");
		int wlRed = Integer.parseInt(sb.substring(start1, end1).replaceAll(" ", "").trim());
		int start2 = sb.indexOf("\t")+1;
		int end2 = sb.length();
		int wlGreen = Integer.parseInt(sb.substring(start2, end2).replaceAll(" ", "").trim());
		return new int[]{wlRed, wlGreen};
	}
	
	private static HashMap<String, Integer> makeColMapping(int[] waveLengths, String Fvalue, String Bvalue) {
		HashMap<String, Integer> cm = new HashMap<String, Integer>();
		int wlRed = waveLengths[0];
		int wlGreen = waveLengths[1];
		String mfstr = Fvalue;
		String mbstr = Bvalue;
		cm.put("\"Name\"", 0);
		cm.put("\"F"+wlRed+" "+mfstr+"\"", 1);
		cm.put("\"F"+wlRed+" "+mfstr+"\"", 1);
		cm.put("\"B"+wlRed+" "+mbstr+"\"", 2);
		cm.put("\"F"+wlGreen+" "+mfstr+"\"", 3);
		cm.put("\"B"+wlGreen+" "+mbstr+"\"", 4);
		cm.put("\"Flags\"", 5);

		// add extra treatment for files with strange quoting (GenePix Results 2)
		cm.put("Name", 0);
		cm.put("F"+wlRed+" "+mfstr+"", 1);
		cm.put("F"+wlRed+" "+mfstr+"", 1);
		cm.put("B"+wlRed+" "+mbstr+"", 2);
		cm.put("F"+wlGreen+" "+mfstr+"", 3);
		cm.put("B"+wlGreen+" "+mbstr+"", 4);
		cm.put("Flags", 5);

		return cm;
	}
	
	
	private static void getColsToRead(String line, int[] cols, HashMap<String, Integer> colMap) {
		StringTokenizer st = new StringTokenizer(line, "\t");
		int colNum = 0;		
		while(st.hasMoreTokens()) {
			String nToken = st.nextToken();
			Integer index = colMap.get(nToken);
			if (index!=null)
				cols[index] = colNum;
			colNum++;
		}
	}
	
}
