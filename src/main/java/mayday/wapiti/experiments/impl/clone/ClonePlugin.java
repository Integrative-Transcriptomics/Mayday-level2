package mayday.wapiti.experiments.impl.clone;

import java.util.LinkedList;
import java.util.List;

import mayday.core.gui.PreferencePane;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.importer.ExperimentImportPlugin;
import mayday.wapiti.gui.ExperimentPanel;
import mayday.wapiti.gui.layeredpane.ReorderableHorizontalPanel;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class ClonePlugin extends ExperimentImportPlugin {
	
	public Setting getSetting() {
		return null;
	}
	
	@Override
	public void importInto(final TransMatrix transMatrix) {	

		List<Experiment> result  = new LinkedList<Experiment>();

		
		for (ReorderableHorizontalPanel rhp : transMatrix.getPane().getSelectionModel().getSelection()) {
			Experiment e  = ((ExperimentPanel)rhp).getExperiment();			
			Experiment c = new ClonedExperiment(e);
			result.add(c);
		}
		
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
				MC+".Clone", 
				new String[0], 
				MC, 
				null, 
				"Florian Battke, Nastasja Trunk", 
				"battke@informatik.uni-tuebingen.de", 
				"Clone existing experiments", 
		"Clone selected experiments");
	}

	protected void initialize() {
	}
			
}
