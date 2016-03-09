/*
 * Created on 29.11.2005
 */
package mayday.wapiti.containers.featuresummarization.importer.csvsingle;

import java.util.HashMap;

import javax.swing.table.TableModel;

import mayday.core.io.gudi.GUDIConstants;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringListMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.wapiti.containers.featuresummarization.FeatureSummarizationMap;
import mayday.wapiti.containers.featuresummarization.importer.AbstractFeatureMapImportPlugin;
import mayday.wapiti.containers.featuresummarization.importer.GenericFeatureMapImporter;
import mayday.wapiti.containers.featuresummarization.importer.csvsingle.FeatureMapColumnTypes.CTYPE;

public class FeatureMapImporter extends GenericFeatureMapImporter<FeatureMapColumnDialog>  {

	public final PluginInfo register() throws PluginManagerException {

		PluginInfo pli = new PluginInfo(
				this.getClass(),
				AbstractFeatureMapImportPlugin.MC+".StringListMIO",
				new String[0],
				AbstractFeatureMapImportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads feature mapping from a tabular file (StringListMIO format)",
				"From tabular file (exactly one line per feature set)"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.ONEFILE);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"");
//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,"Feature mapping file parser");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"Tabular feature map (tsv,csv,...)");		
		return pli;
	}

	public void init() {}



	private StringSetting separatorSetting;

	/**
	 * Provide a custom setting for this plugin that contains the file chooser
	 * AND an extra field for the separator string.
	 * @return
     */
	@Override
	public Setting getSetting() {
		Setting file = super.getSetting();
		separatorSetting =  new StringSetting("Separator String",
				"Specifies the string that separates entries in your subfeature.",
				",",false);

		HierarchicalSetting hs = new HierarchicalSetting("");
		hs.addSetting(file)
				.addSetting(separatorSetting);

		return hs;
	}

	protected FeatureSummarizationMap makeFeatureMap(String name, TableModel tm, FeatureMapColumnDialog ctd) {
		
		FeatureSummarizationMap fsm = new FeatureSummarizationMap(name);
		HashMap<CTYPE, Integer> columns = ctd.getColumns();
		
		StringListMIO slm = new StringListMIO();
		
		int idCol = columns.get(CTYPE.Feature);
		int slmCol = columns.get(CTYPE.Subfeatures);
		
		for (int i=0; i!=tm.getRowCount(); ++i) {
			String fname = (String)tm.getValueAt(i, idCol);
			String subf = (String)tm.getValueAt(i, slmCol);
			// Separate Subfeature entries
			slm.deSerialize(MIType.SERIAL_TEXT, subf,
					separatorSetting.getStringValue());
			fsm.putReplace(fname, slm.getValue());
		}
		
		return fsm;
	}

	@Override
	protected FeatureMapColumnDialog makeDialog(TableModel tm) {
		return new FeatureMapColumnDialog(tm);
	}



}
