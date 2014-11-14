package mayday.wapiti.experiments.impl.affy;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import mayday.core.gui.PreferencePane;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.FilesSetting;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.tasks.AbstractTask;
import mayday.wapiti.containers.featuresummarization.FeatureSummarizationMapContainer;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.importer.ExperimentImportPlugin;
import mayday.wapiti.transformations.impl.log.ExpressionLogarithm;
import mayday.wapiti.transformations.impl.rma.bg.RMABGCorrection;
import mayday.wapiti.transformations.impl.rma.mp.MedianPolish;
import mayday.wapiti.transformations.impl.rma.qn.QuantileNorm;
import mayday.wapiti.transformations.matrix.TransMatrix;
import affymetrix.calvin.exception.UnsignedOutOfLimitsException;
import affymetrix.fusion.cdf.FusionCDFData;
import affymetrix.fusion.cel.FusionCELData;

public class CELImportPlugin extends ExperimentImportPlugin {

	protected BooleanSetting performRMA = new BooleanSetting("Perform RMA","Check this if you want to add RMA transformations to the data (recommended)",true);
	protected FilesSetting inputFiles = new FilesSetting("CEL files",null,null,false,"cel");
	protected HierarchicalSetting mySetting = new HierarchicalSetting("CEL Import")
				.addSetting(inputFiles)
				.addSetting(performRMA);
	
	public Setting getSetting() {
		return mySetting;
	}
	
	@Override
	public void importInto(final TransMatrix transMatrix) {	

		final LinkedList<Experiment> result = new LinkedList<Experiment>();
		
		MultiHashMap<String, String> filenamesByCDF = new MultiHashMap<String, String>();
//		HashMap<String, Integer> CDF2numberOfCells = new HashMap<String, Integer>();
		MultiHashMap<String, Experiment> expByCDF = new MultiHashMap<String, Experiment>();
		
		HashSet<String> directories = new HashSet<String>();
		
		// parse CDF files that were supplied
//		for (String n : cdfFiles.getFileNames()) {
//			CDFRepository.addCDF(n, retainCDF.getBooleanValue());
//		}
				
		// get all the chip types, check if we have all cdfs
		for (String n : inputFiles.getFileNames()) {
			
			FusionCELData cel = new FusionCELData();
			cel.setFileName(n);
			cel.readHeader();
			
			String cdfId = cel.getChipType();
			filenamesByCDF.put(cdfId, n);
//			CDF2numberOfCells.put(cdfId, cel.getCells());
			
			directories.add(new File(n).getParent());
		}

		int start=0;
		int count = inputFiles.getFileNames().size();
		
		for (String cdfId : filenamesByCDF.keySet()) {
			// first try to get the cdf data from mayday's repository			
			FusionCDFData cdfd = CDFRepository.getCDF(cdfId);
			
			// if not found, try the directory/ies of the CEL files
			if (cdfd == null) {
				for (String dirname : directories) {
					String possibleCDF = dirname+"/"+cdfId+".CDF";
					if (new File(possibleCDF).exists()) {
						CDFRepository.addCDF(possibleCDF, true);
						cdfd = CDFRepository.getCDF(cdfId);
					}					
				}
			}
			
			// if still not found, prompt user for the file
			CDFData CeDeEf = CDFData.get(cdfId, true, directories.iterator().next());
//			cdfd = CDFRepository.getCDFinteractive(cdfId, directories.iterator().next());
						
			if (CeDeEf==null)
				throw new RuntimeException("CDF data not found for chip type "+cdfId);
			
//			CDFData CeDeEf  = new CDFData(cdfd);
			// add the probeset mapping to the feature summarization data container
			FeatureSummarizationMapContainer.INSTANCE.add(CeDeEf);
			// parse the cels
			AbstractTask at = new CELParserTask(start, count, result, filenamesByCDF.get(cdfId), transMatrix, CeDeEf);
			at.start();
			at.waitFor();
			for (int i=start; i!=result.size(); ++i)
				expByCDF.put(cdfId, result.get(i));
			start+=filenamesByCDF.get(cdfId).size();			
			if (at.hasBeenCancelled()) 
				return;
		}
		
		addExperiments(result, transMatrix);
		
		if (performRMA.getBooleanValue()) {

			for (String cdfName : expByCDF.keySet()) {
				List<Experiment> exs = expByCDF.get(cdfName);
				CDFData cdf = ((CELExperiment)exs.get(0)).getCDFData();
				transMatrix.addTransformation(new RMABGCorrection(), exs);
				transMatrix.addTransformation(new QuantileNorm(), exs);
				transMatrix.addTransformation(new ExpressionLogarithm(2.0), exs);
				MedianPolish sf = new MedianPolish(cdf);  
				transMatrix.addTransformation(sf, exs);
			}

			
		}
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
				MC+".CEL", 
				new String[]{"LIB.Fusion"}, 
				MC, 
				null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Import experiments from AffyMetrix CEL files", 
		"Affymetrix CEL files");
	}

	protected static class CELParserTask extends AbstractTask {

		protected int s,c;
		protected List<Experiment> result;
		protected Collection<String> files;
		protected TransMatrix transMatrix;
		protected CDFData cdfData;
		
		/* optimization if several CELS are loaded individually with the same CDF */
		protected static WeakHashMap<CDFData, String[]> lastFeatureNames = new WeakHashMap<CDFData, String[]>(1);
		protected static WeakHashMap<CDFData, HashMap<String, Integer>> lastNameCache = new WeakHashMap<CDFData, HashMap<String,Integer>>(1);
		
		public CELParserTask(int start, int count, List<Experiment> result, Collection<String> files, TransMatrix tm, CDFData cdfData) {
			super("Parsing CEL Files");
			s=start;
			c=count;
			this.result = result;
			this.files = files;
			transMatrix = tm;
			this.cdfData = cdfData;
		}

		@Override
		protected void doWork() throws Exception {
			
			String[] featureNames = null;
			HashMap<String, Integer> nameCache = null;
			
			featureNames = lastFeatureNames.get(cdfData);
			nameCache = lastNameCache.get(cdfData);
			boolean reuseExisting = true;
			
			if (nameCache == null || featureNames == null) {
				nameCache = new HashMap<String, Integer>();
				featureNames = null;
				reuseExisting = false;
			}
			
			for (String file : files) {					
				setProgress((10000*s++)/c, new File(file).getName());
				
				FusionCELData cel = new FusionCELData();
				
				cel.setFileName(file);			
				if (cel.read() == false) 
					throw new RuntimeException("Failed to read the cel file \""+file+"\"");
				
				if (featureNames==null) {
					featureNames = new String[cel.getCells()];
					for (int i=0; i!=featureNames.length; ++i)
						featureNames[i] = String.valueOf(i);
				}
				
				double[] exp = new double[cel.getCells()];
				
				DoubleVector expression = new DoubleVector(exp);
				expression.setNamesDirectly(featureNames, nameCache); //only link the names. this is an important optimization
				
				for (int i=0; i!=exp.length; ++i) {
					double d = Double.NaN;
					try {
						d = cel.getIntensity(i);
					} catch (UnsignedOutOfLimitsException e) {
						e.printStackTrace();
					}
					exp[i] = d;
				}
				
				CELExperiment experiment = new CELExperiment(transMatrix, file, expression, cdfData);
				result.add(experiment);
				
				if (hasBeenCancelled())
					return;
			}
			
			if (!reuseExisting) {
				lastNameCache.clear();
				lastFeatureNames.clear();
				lastNameCache.put(cdfData, nameCache);
				lastFeatureNames.put(cdfData, featureNames);
			}

		}

		@Override
		protected void initialize() {
		}
		
	}
	
}
