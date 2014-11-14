package mayday.wapiti.experiments.impl.legacy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.io.gudi.prototypes.DatasetFileImportPlugin;
import mayday.core.io.gudi.prototypes.DatasetImportPlugin;
import mayday.core.meta.MIGroup;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.SurrogatePlugin;
import mayday.core.pluma.SurrogatePluginInfo;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.FilesSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.genetics.LocusMIO;
import mayday.genetics.locusmap.LocusMap;
import mayday.genetics.locusmap.LocusMapContainer;
import mayday.wapiti.Constants;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.importer.ExperimentImportPlugin;
import mayday.wapiti.transformations.impl.addlocus.AddLocusData;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class LegacyImportPlugin extends ExperimentImportPlugin implements SurrogatePlugin<PluginInfo> {

	public final static String PREFIX = Constants.MCBASE+".wrapping.";
	
	protected LocusMap primaryMap;
	protected PluginInfo plugin;
	protected AbstractPlugin ap;
	protected boolean multiSelection;
	protected boolean directories;
	protected Setting mySetting;

	public LegacyImportPlugin() {};
	
	public LegacyImportPlugin(PluginInfo p) {
		initializeWithObject(p, null);
	}
	
	public mayday.core.pluma.PluginInfo getWrappedPluginInfo() {
		return plugin;
	}
	
	@Override
	public void importInto(TransMatrix transMatrix) {		
		List<DataSet> results = null;
		if (ap instanceof DatasetFileImportPlugin) {
			DatasetFileImportPlugin dsip = (DatasetFileImportPlugin)ap;
			List<String> files;
			if (multiSelection)
				files = ((FilesSetting)mySetting).getFileNames();
			else
				files = Arrays.asList(new String[]{((PathSetting)mySetting).getStringValue()});			
			results = dsip.importFrom(files);			
		} else if (ap instanceof DatasetImportPlugin){
			DatasetImportPlugin dsip = (DatasetImportPlugin)ap;
			results = dsip.run();
		}
		
		if (results!=null) {
			LinkedList<Experiment> exps = new LinkedList<Experiment>();
			for (DataSet ds : results) {
				// Strip dataset down to save memory: Remove Probelists
				ds.getProbeListManager().clear();
				// Remove MIO groups, move locus information into LocusMapContainer for later use
				for (MIGroup mg : ds.getMIManager().getGroups()) {
					if (mg.getMIOClass()==LocusMIO.class) {
						LocusMap lm = new LocusMap(mg);
						LocusMapContainer.INSTANCE.put(lm.toString(),lm);
						if (primaryMap==null)
							primaryMap = lm;
					}
					if (!(mg.getMIOPluginInfo().getIdentifier().equals("PAS.MIO.Annotation")))
						ds.getMIManager().removeGroup(mg);
				}
				// create experiments for all ...well... experiments
				for (int i=0; i!=ds.getMasterTable().getNumberOfExperiments(); ++i) {
					exps.add(new DataSetExperiment(transMatrix,ds,i));
				}
			}
			addExperiments(exps, transMatrix, primaryMap);
		} 
	}
	
	public Setting getSetting() {
		return mySetting;
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
		return null;
	}
	
	public static PluginInfo createSurrogatePluginInfo(PluginInfo wrappedPli) throws PluginManagerException {
		return new SurrogatePluginInfo<PluginInfo, LegacyImportPlugin>(
				LegacyImportPlugin.class,
				wrappedPli,
				PREFIX+wrappedPli.getIdentifier(),
				wrappedPli.getDependencies(),
				wrappedPli.getMasterComponent(),
				new HashMap<String, Object>(),
				wrappedPli.getAuthor(),
				wrappedPli.getEmail(),
				wrappedPli.getAbout(),
				"\255 Mayday Importer: "+wrappedPli.getName()
				);
	}

	@Override
	public void initializeWithObject(PluginInfo p, PluginInfo ignoredOuterPLI) {
		plugin=p;
		ap = plugin.getInstance();
		if (ap instanceof DatasetFileImportPlugin) {
			Integer type = (Integer)(plugin.getProperties().get(GUDIConstants.FILESYSTEM_IMPORTER_TYPE));
			if (type==null) {
				System.err.println("GUDI: "+plugin.getIdentifier()+" has no valid FILESYSTEM_IMPORTER_TYPE");
			} else {
				switch (type) {
				case GUDIConstants.ONEFILE:
					multiSelection = false;
					directories = false;
			        break;
				case GUDIConstants.MANYFILES:
					multiSelection = true;
					directories = false;
			        break;
				case GUDIConstants.DIRECTORY:
					multiSelection = false;
					directories = true;
			        break;			        
			     } 
			}
			mySetting = multiSelection ? new FilesSetting("Input Files",null,null) : 
						(new PathSetting("Input "+(directories?"directory":"file"),null,null,directories,true,false));
		}		
	}		
	
}
