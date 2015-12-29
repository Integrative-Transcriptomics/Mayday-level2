/*
 * Created on 29.11.2005
 */
package mayday.wapiti.containers.identifiermapping.importer.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import javax.swing.table.TableModel;

import mayday.core.gui.abstractdialogs.SimpleStandardDialog;
import mayday.core.io.csv.CSVImportSettingComponent;
import mayday.core.io.csv.ParsingTableModel;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.wapiti.containers.identifiermapping.IdentifierMap;
import mayday.wapiti.containers.identifiermapping.importer.AbstractIDImportPlugin;
import mayday.wapiti.containers.identifiermapping.importer.IDFileImportPlugin;
import mayday.wapiti.containers.identifiermapping.importer.csv.IdentifierColumnTypes.CTYPE;

public class IdentifierImporter extends AbstractIDImportPlugin implements IDFileImportPlugin  {

	public final PluginInfo register() throws PluginManagerException {

		PluginInfo pli = new PluginInfo(
				this.getClass(),
				AbstractIDImportPlugin.MC+".CSV",
				new String[0],
				AbstractIDImportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads identifier mapping from column-separated files",
				"From tabular file"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.ONEFILE);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"");
//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,"Tabular file parser");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"Tabular text file (tsv,csv,...)");		
		return pli;
	}

	public void init() {}

	public IdentifierMap importFrom(List<String> files) {
		TableModel tm = getTableModel(new File(files.get(0)));
		return getIDMap(tm, files.get(0));
	}
	
	protected TableModel getTableModel(File f) {
		if (f==null)
			return null;
		CSVImportSettingComponent comp;
		try {
			comp = new CSVImportSettingComponent(new ParsingTableModel(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}            		
		SimpleStandardDialog dlg = new SimpleStandardDialog("Import Identifier Mapping",comp,false);
		dlg.setVisible(true);
		if(!dlg.okActionsCalled())
			return null;
		return comp.getTableModel();
	}
		
	protected IdentifierMap getIDMap(final TableModel tm, final String name) {
		if (tm==null)
			return null;		
		// create the dialog now
		IdentifierColumnDialog ctd = new IdentifierColumnDialog(tm);
		ctd.setVisible(true);
		if (!ctd.canceled())
			return makeIDMap(name, tm, ctd);
		return null;
	}
	
	protected IdentifierMap makeIDMap(String name, TableModel tm, IdentifierColumnDialog ctd) {

		HashMap<CTYPE, Integer> columns = ctd.getColumns();
		int c1 = columns.get(CTYPE.Identifier);
		int c2 = columns.get(CTYPE.New_Identifier);
		
		IdentifierMap map = new IdentifierMap(name);

		for (int i=0; i!= tm.getRowCount(); ++i) {
			map.put( (String)tm.getValueAt(i, c1), (String)tm.getValueAt(i, c2) );
		}

		return map;
	}

}
