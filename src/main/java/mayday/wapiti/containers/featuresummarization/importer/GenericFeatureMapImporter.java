/*
 * Created on 29.11.2005
 */
package mayday.wapiti.containers.featuresummarization.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.table.TableModel;

import mayday.core.gui.abstractdialogs.SimpleStandardDialog;
import mayday.core.gui.columnparse.ColumnTypeDialog;
import mayday.core.io.csv.CSVImportSettingComponent;
import mayday.core.io.csv.ParsingTableModel;
import mayday.wapiti.containers.featuresummarization.FeatureSummarizationMap;

public abstract class GenericFeatureMapImporter<DialogType extends ColumnTypeDialog<?>> extends AbstractFeatureMapImportPlugin implements FeatureMapFileImportPlugin {


	public FeatureSummarizationMap importFrom(List<String> files) {
		TableModel tm = getTableModel(new File(files.get(0)));
		return getLocusMap(tm, files.get(0));
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
		SimpleStandardDialog dlg = new SimpleStandardDialog("Import Feature Mapping",comp,false);
		dlg.setVisible(true);
		if(!dlg.okActionsCalled())
			return null;
		return comp.getTableModel();
	}
		
	protected FeatureSummarizationMap getLocusMap(final TableModel tm, final String name) {
		if (tm==null)
			return null;		
		// create the dialog now
		DialogType ctd = makeDialog(tm);
		ctd.setVisible(true);
		if (!ctd.canceled())
			return makeFeatureMap(name, tm, ctd);
		return null;
	}
	
	protected abstract DialogType makeDialog(TableModel tm);
	
	protected abstract FeatureSummarizationMap makeFeatureMap(String name, TableModel tm, DialogType ctd);
	
		
}
