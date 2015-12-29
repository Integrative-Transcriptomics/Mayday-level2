package mayday.wapiti.experiments.impl.legacy;

import java.util.LinkedList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.gui.dataset.DataSetSelectionDialog;
import mayday.core.meta.MIGroup;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.genetics.LocusMIO;
import mayday.genetics.locusmap.LocusMap;
import mayday.genetics.locusmap.LocusMapContainer;
import mayday.wapiti.Constants;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.importer.ExperimentImportPlugin;
import mayday.wapiti.transformations.impl.addlocus.AddLocusData;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class DataSetImportPlugin extends ExperimentImportPlugin {

	public final static String PREFIX = Constants.MCBASE+".wrapping.";

	protected LocusMap primaryMap;
	protected mayday.core.pluma.PluginInfo plugin;
	protected AbstractPlugin ap;

	public mayday.core.pluma.PluginInfo getWrappedPluginInfo() {
		return plugin;
	}

	@Override
	public void importInto(TransMatrix transMatrix) {	
		
		DataSetSelectionDialog dssd = new DataSetSelectionDialog();
		dssd.setDialogDescription("Select DataSets to import");
		dssd.setModal(true);
		dssd.setVisible(true);

		List<DataSet> results = dssd.getSelection();
		
		if (results!=null) {
			LinkedList<Experiment> exps = new LinkedList<Experiment>();
			for (DataSet ds : results) {
				// Copy locus information into LocusMapContainer for later use
				for (MIGroup mg : ds.getMIManager().getGroups()) {
					if (mg.getMIOClass()==LocusMIO.class) {
						LocusMap lm = new LocusMap(mg);
						LocusMapContainer.INSTANCE.put(lm.toString(),lm);
						if (primaryMap==null)
							primaryMap = lm;
					}
				}
				// create experiments for all ...well... experiments
				for (int i=0; i!=ds.getMasterTable().getNumberOfExperiments(); ++i) {
					exps.add(new DataSetExperiment(transMatrix,ds,i));
				}
			}
			addExperiments(exps, transMatrix, primaryMap);
		} 
	}


	protected void addExperiments(List<Experiment> experiments, TransMatrix transMatrix, LocusMap primaryMap) {
		if (experiments.size()==0)
			return;
		super.addExperiments(experiments, transMatrix);
		AddLocusData t = new AddLocusData();
		SettingDialog sd = new SettingDialog(null, "Add locus data", t.getSetting());
		if (primaryMap!=null)
			t.getSetting().setLocusMap(primaryMap);
		sd.setModal(true);
		sd.setVisible(true);
		if (!sd.canceled() && t.getSetting().getLocusMap()!=null)
			transMatrix.addTransformation(t, experiments);
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".FromDSM", 
				new String[0], 
				MC, 
				null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Import experiments from a running Mayday session", 
		"Add open DataSet");
	}

}
