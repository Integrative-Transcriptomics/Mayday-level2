package mayday.wapiti.experiments.importer;

import java.util.Set;
import java.util.TreeSet;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.wapiti.experiments.impl.legacy.LegacyImportPlugin;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class ExperimentImport {

	public static void run(final TransMatrix transMatrix) throws PluginManagerException {

		TreeSet<PluginInfo> plis = new TreeSet<PluginInfo>(PluginManager.getInstance().getPluginsFor(ExperimentImportPlugin.MC));

		for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(Constants.MC_DATASET_IMPORT)) {
			plis.add( LegacyImportPlugin.createSurrogatePluginInfo(pli));
		}

		ExperimentImportPlugin Default = (ExperimentImportPlugin)plis.iterator().next().getInstance();

		StrangePluginTypeSetting<ExperimentImportPlugin> s = new StrangePluginTypeSetting<ExperimentImportPlugin>(
				"Import Plugin",
				"Select how to import experiments", 
				Default, 
				plis
		);

		SettingDialog sd = new SettingDialog(null, "Import Experiments", s);
		sd.showAsInputDialog();

		final PluginTypeSetting<ExperimentImportPlugin> final_s = s;
		if (!sd.canceled())
			new Thread("Importer Task") {
				public void run() {
					final_s.getInstance().importInto(transMatrix);
				}
			}.start();


	}

	protected static class StrangePluginTypeSetting<T extends AbstractPlugin> extends PluginTypeSetting<T> {
		
		public StrangePluginTypeSetting(String Name, String Description,
				T Default, String MC) {
			super(Name, Description, Default, MC);
		}
		
		public StrangePluginTypeSetting(String Name, String Description,
				T Default, Set<PluginInfo> plis) {
			super(Name, Description, Default, plis);
		}

		protected PluginInfo pliFromID(String id) {
			if (id.startsWith(LegacyImportPlugin.PREFIX)) {
				try {
					return LegacyImportPlugin.createSurrogatePluginInfo(
							PluginManager.getInstance().getPluginFromID(
									id.substring(LegacyImportPlugin.PREFIX.length())
							)
					);
				} catch (PluginManagerException e) {
					System.out.println(e);
					return null;
				}
			}
			return PluginManager.getInstance().getPluginFromID(id);
		}

		protected PluginInfo pliFromInstance(AbstractPlugin instance) {
			if (instance instanceof LegacyImportPlugin) {
				try {
					return LegacyImportPlugin.createSurrogatePluginInfo(
							((LegacyImportPlugin)instance).getWrappedPluginInfo()
					);
				} catch (PluginManagerException e) {
					System.out.println(e);
					return null;
				}
			}
			return PluginManager.getInstance().getPluginFromClass(instance.getClass());
		}
		
		public StrangePluginTypeSetting<T> clone() {
			return new StrangePluginTypeSetting<T>(getName(),getDescription(),getInstance(),predef);
		}
	}
	
}
