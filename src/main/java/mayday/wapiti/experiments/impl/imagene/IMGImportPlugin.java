package mayday.wapiti.experiments.impl.imagene;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.gui.PreferencePane;
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

public class IMGImportPlugin extends ExperimentImportPlugin {

	
	protected final static String MEAN = "mean";
	protected final static String MEDIAN = "median";
	protected final static String MODE = "mode";
	protected final static String TOTAL = "total";	
	protected String[] choices= new String[]{MEAN, MEDIAN, MODE, TOTAL};
	
	protected RestrictedStringSetting extractedValues = new RestrictedStringSetting("Values to extract",null, 0, choices);
	protected FilesSetting inputFiles = new FilesSetting("ImaGene files",
			"File names must reflect the corresponding channels, e.g. after alphabetical sorting of the files\n" +
			"the corresponding files must have consecutive indices.",
			null,false,"");
	
	protected HierarchicalSetting mySetting = new HierarchicalSetting("ImaGene Import")
				.addSetting(inputFiles)
				.addSetting(extractedValues);
	
	public Setting getSetting() {
		return mySetting;
	}
	
	@Override
	public void importInto(final TransMatrix transMatrix) {	

		if (inputFiles.getFileNames().size()%2!=0)
			throw new RuntimeException("Number of files for ImaGene import must be a multiple of two.");
		
		final LinkedList<Experiment> result = new LinkedList<Experiment>();
		
		AbstractTask at = new IMGParserTask(inputFiles.getFileNames(), result, transMatrix, extractedValues.getStringValue());
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
				MC+".Imagene", 
				new String[0], 
				MC, 
				null, 
				"Florian Battke, Nastasja Trunk", 
				"battke@informatik.uni-tuebingen.de", 
				"Import experiments from ImaGene files", 
		"ImaGene files");
	}

	protected static class IMGParserTask extends AbstractTask {

		protected List<Experiment> result;
		protected List<String> files;
		protected TransMatrix transMatrix;
		protected String method;
		
		public IMGParserTask(List<String> files, List<Experiment> result, TransMatrix transMatrix, String method) {
			super("Parsing ImaGene Files");
			this.result = result;
			this.files = files;
			this.transMatrix = transMatrix;
			this.method = method;
		}

		@Override
		protected void doWork() throws Exception {
			
			int valueType;
			
			if (method.equals(MEAN))
				valueType = ImaGeneDataStructure.sMean;
			else if (method.equals(MEDIAN))
				valueType = ImaGeneDataStructure.sMedian;
			else if (method.equals(MODE))
				valueType = ImaGeneDataStructure.sMode;
			else 
				valueType = ImaGeneDataStructure.sTotal;
			
			int experiments = files.size()/2;
			
			ArrayList<String> currentFiles = new ArrayList<String>();
			currentFiles.ensureCapacity(2);
			currentFiles.add("");
			currentFiles.add("");
						
			for (int exp=0; exp!=experiments; ++exp) {
				
				currentFiles.set(0,files.get(2*exp));
				currentFiles.set(1,files.get(2*exp+1));
				
				String s1 = new File(currentFiles.get(0)).getName();
				String s2 = new File(currentFiles.get(1)).getName();
				
				int prefLen;
				for (prefLen=0; prefLen!=Math.min(s1.length(), s2.length()); ++prefLen)
					if (s1.charAt(prefLen)!=s2.charAt(prefLen))
						break;
				String prefix = s1.substring(0, prefLen);							
				String name = s1+", "+s2;

				setProgress((10000*exp)/experiments, name);

				ImaGeneParser imaParse = new ImaGeneParser(currentFiles, valueType);
				
				HashMap<String, Integer> nameCache = new HashMap<String, Integer>();
				
				try {
					imaParse.parseArray();
					ImaGeneDataStructure structure = imaParse.getStructure();
					ImaGeneArray array = imaParse.getAr();					
					ArrayLayout layout = new ArrayLayout(structure.getMetaRows(), structure.getMetaCols(), structure.getRows(), structure.getCols());
					
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
					
					for (int i=0; i!=layout.totalRows(); ++i) {
						for (int j=0; j!=layout.totalCols(); ++j) {
							int index = layout.position2Index(i,j);
							redF.set(index, array.getForegroundR(i, j));
							redB.set(index, array.getBackgroundR(i, j));
							greenF.set(index, array.getForegroundG(i, j));
							greenB.set(index, array.getBackgroundR(i, j));
							boolean isPoor = array.getFlagPoor(i, j);
							boolean isEmpty = array.getFlagEmpty(i, j);
							boolean isNegative = array.getFlagNegative(i, j);
							double flag = isPoor?1: (isEmpty?2: (isNegative?3:0));
							flags.set(index, flag );
							featureNames[index] = array.getGeneID(i,j);							
						}
					}
					
					makeNamesUnique(featureNames, name);
					
					IMGExperiment imgE = new IMGExperiment(
							transMatrix, 
							prefix, 
							currentFiles.toArray(new String[0]), 
							name, 
							method, 
							redF, redB, greenF, greenB, flags, layout);
					result.add(imgE);

				} 
				catch (Exception e) {
					throw new RuntimeException("Error during parsing the IMG files: "+currentFiles+"\n"+e+"\n"+e.getMessage());				
				}
				
				
			}
		}

		@Override
		protected void initialize() {
		}
		
	}
	
}
