package mayday.wapiti.experiments.impl.wiggle;

import java.util.LinkedList;
import java.util.List;

import mayday.core.gui.PreferencePane;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.FilesSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.tasks.AbstractTask;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.importer.ExperimentImportPlugin;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class WiggleImportPlugin extends ExperimentImportPlugin {

	protected FilesSetting inputFilesFWD = new FilesSetting("Forward strand files",
			"The wiggle format does not contain strand information, only chromosome IDs.\n" +
			"Here you can select files that contain measurements for the forward strand.",null);
	protected FilesSetting inputFilesBWD = new FilesSetting("Reverse strand files",
			"The wiggle format does not contain strand information, only chromosome IDs.\n" +
			"Here you can select files that contain measurements for the backward strand.",null);
	protected FilesSetting inputFilesBOTH = new FilesSetting("Both strands files",
			"The wiggle format does not contain strand information, only chromosome IDs.\n" +
			"Here you can select files that contain measurements for both strands\n" +
			"or when the strand plays no role for the data. If forward or reverse strand\n" +
			"data is supplied, it will be used preferentially.",null);
	protected StringSetting speciesName = new StringSetting("Species", 
			"The wiggle format does not contain species identifiers. \n" +
			"Please define the species here", "");
	
	protected HierarchicalSetting mySetting = new HierarchicalSetting("Wiggle Import")
				.addSetting(speciesName)
				.addSetting(inputFilesFWD)
				.addSetting(inputFilesBWD)
				.addSetting(inputFilesBOTH);

	
	public Setting getSetting() {
		return mySetting;
	}
	
	@Override
	public void importInto(final TransMatrix transMatrix) {	

		final LinkedList<Experiment> result = new LinkedList<Experiment>();
		
		AbstractTask at = new WiggleParserTask(
				inputFilesFWD.getFileNames(), 
				inputFilesBWD.getFileNames(), 
				inputFilesBOTH.getFileNames(), 
				speciesName.getStringValue(), result, transMatrix);
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
				MC+".Wiggle", 
				new String[0], 
				MC, 
				null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Import expression data from Wiggle files", 
		"Wiggle files");
	}

	protected static class WiggleParserTask extends AbstractTask {

		protected List<Experiment> result;
		protected List<String> filesFWD;
		protected List<String> filesBWD;
		protected List<String> filesBOTH;
		protected TransMatrix transMatrix;
		protected String species;
		
		public WiggleParserTask(
				List<String> filesFWD, 
				List<String> filesBWD, 
				List<String> filesBOTH,
				String species, 
				List<Experiment> result, 
				TransMatrix transMatrix) {
			super("Importing Wiggle files");
			this.result = result;
			this.filesFWD = filesFWD;
			this.filesBWD = filesBWD;
			this.filesBOTH = filesBOTH;
			this.transMatrix = transMatrix;
			this.species=species;
		}		

		@Override
		protected void doWork() throws Exception {
			result.add(new WiggleExperiment(transMatrix, filesFWD, filesBWD, filesBOTH, species));
		}

		@Override
		protected void initialize() {
		}
		
	}
	
	
	
}
