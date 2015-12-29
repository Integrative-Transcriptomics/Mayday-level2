/*
 * Created on 29.11.2005
 */
package mayday.wapiti.containers.featuresummarization.importer.csvmulti;

import java.util.HashMap;

import javax.swing.table.TableModel;

import mayday.core.io.gudi.GUDIConstants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.wapiti.containers.featuresummarization.FeatureSummarizationMap;
import mayday.wapiti.containers.featuresummarization.importer.AbstractFeatureMapImportPlugin;
import mayday.wapiti.containers.featuresummarization.importer.GenericFeatureMapImporter;
import mayday.wapiti.containers.featuresummarization.importer.csvmulti.FeatureMapColumnTypes.CTYPE;

public class FeatureMapImporter extends GenericFeatureMapImporter<FeatureMapColumnDialog>  {

	public final PluginInfo register() throws PluginManagerException {

		PluginInfo pli = new PluginInfo(
				this.getClass(),
				AbstractFeatureMapImportPlugin.MC+".StringMIO",
				new String[0],
				AbstractFeatureMapImportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads feature mapping from a tabular file",
				"From tabular file (one line per feature, multiple lines per feature set)"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.ONEFILE);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"");
//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,"Feature mapping file parser");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"Tabular feature map (tsv,csv,...)");		
		return pli;
	}

	public void init() {}

	protected FeatureSummarizationMap makeFeatureMap(String name, TableModel tm, FeatureMapColumnDialog ctd) {
		
		FeatureSummarizationMap fsm = new FeatureSummarizationMap(name);
		HashMap<CTYPE, Integer> columns = ctd.getColumns();
		
		int idCol = columns.get(CTYPE.Feature);
		int subCol = columns.get(CTYPE.Subfeature);
		
		for (int i=0; i!=tm.getRowCount(); ++i) {
			String fname = (String)tm.getValueAt(i, idCol);
			String subf = (String)tm.getValueAt(i, subCol);
			fsm.put(fname, subf);
		}
		
		return fsm;
	}

	@Override
	protected FeatureMapColumnDialog makeDialog(TableModel tm) {
		return new FeatureMapColumnDialog(tm);
	}



}
